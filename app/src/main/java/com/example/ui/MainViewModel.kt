package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AccountDao
import com.example.data.AccountEntity
import com.example.data.AppDatabase
import com.example.data.PreferencesRepository
import com.example.network.FbAccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

import com.example.network.DeviceActivationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

import com.example.data.Country

import com.example.network.VoltxApiService
import com.example.network.VoltxOtpItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VoltxActiveNumber(
    val phone: String,
    val rangeCode: String,
    val timestamp: String = "",
    val otp: String? = null,
    val rawMessage: String? = null,
    val isAutoCopied: Boolean = false,
    val accountUid: String? = null
)

data class AccountCreatorUiState(
    val phoneInput: String = "",
    val passwordInput: String = "",
    val selectedCountry: Country = Country.BANGLADESH,
    val isCreating: Boolean = false,
    val lastCreatedAccount: AccountEntity? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showTelegramDialog: Boolean = false,
    val deviceId: String = "",
    val isActivated: Boolean = false,
    val isCheckingActivation: Boolean = false,
    val activationStatusMessage: String = "Checking device status...",
    val facebookRanges: List<String> = emptyList(),
    val isLoadingRanges: Boolean = false,
    val isFetchingNumber: Boolean = false,
    val selectedRangeCode: String? = null,
    val activeNumbers: List<VoltxActiveNumber> = emptyList(),
    val lastCopiedOtp: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val accountDao: AccountDao = AppDatabase.getDatabase(application).accountDao()
    private val prefsRepository = PreferencesRepository(application)

    private val _uiState = MutableStateFlow(AccountCreatorUiState())
    val uiState: StateFlow<AccountCreatorUiState> = _uiState.asStateFlow()

    val accountHistory: StateFlow<List<AccountEntity>> = accountDao.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Load persistent settings on launch
        val savedPassword = prefsRepository.savedPassword
        val savedCountryCode = prefsRepository.selectedCountryCode
        val savedCountry = Country.fromCode(savedCountryCode)
        val showTelegram = !prefsRepository.isTelegramJoined
        val devId = DeviceActivationService.getDeviceId(application)

        _uiState.value = _uiState.value.copy(
            passwordInput = savedPassword,
            selectedCountry = savedCountry,
            showTelegramDialog = showTelegram,
            deviceId = devId
        )

        // Start periodic activation check (every 1 minute)
        startPeriodicActivationCheck()

        // Fetch Facebook live ranges
        refreshFacebookRanges()

        // Start OTP polling every 3 seconds
        startOtpPolling()
    }

    private fun startPeriodicActivationCheck() {
        viewModelScope.launch {
            while (isActive) {
                checkDeviceActivationInternal()
                delay(60_000) // repeat every 60 seconds (1 minute)
            }
        }
    }

    private fun startOtpPolling() {
        viewModelScope.launch {
            while (isActive) {
                checkOtpsInternal()
                delay(3000) // check every 3 seconds as requested
            }
        }
    }

    private suspend fun checkOtpsInternal() {
        val currentActives = _uiState.value.activeNumbers
        if (currentActives.isEmpty()) return

        val otps = VoltxApiService.checkSuccessOtps()
        if (otps.isEmpty()) return

        val updatedActives = currentActives.map { active ->
            val match = otps.find { it.number == active.phone || it.number.contains(active.phone) || active.phone.contains(it.number) }
            if (match != null && match.otp != "N/A") {
                if (!active.isAutoCopied || active.otp != match.otp) {
                    // Auto copy OTP to clipboard
                    autoCopyOtpToClipboard(match.otp, active.phone)
                    active.copy(
                        otp = match.otp,
                        rawMessage = match.message,
                        isAutoCopied = true
                    )
                } else {
                    active
                }
            } else {
                active
            }
        }

        _uiState.value = _uiState.value.copy(activeNumbers = updatedActives)
    }

    private fun autoCopyOtpToClipboard(otp: String, phone: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FB_OTP", otp)
        clipboard.setPrimaryClip(clip)

        _uiState.value = _uiState.value.copy(
            lastCopiedOtp = otp,
            successMessage = "🎉 OTP Received & Auto-Copied: $otp for $phone!"
        )
    }

    fun refreshFacebookRanges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingRanges = true)
            val ranges = VoltxApiService.fetchLiveFacebookRanges()
            _uiState.value = _uiState.value.copy(
                facebookRanges = ranges,
                isLoadingRanges = false
            )
        }
    }

    fun onRangeClicked(rangeCode: String) {
        val currentState = _uiState.value
        if (!currentState.isActivated) {
            _uiState.value = currentState.copy(errorMessage = "Device not activated! Please activate your device first.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isFetchingNumber = true,
                selectedRangeCode = rangeCode,
                errorMessage = null,
                successMessage = "Fetching number for range $rangeCode..."
            )

            val number = VoltxApiService.fetchPhoneNumber(rangeCode)
            if (number.isNullOrEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isFetchingNumber = false,
                    errorMessage = "No number available for range $rangeCode. Please try another range!"
                )
            } else {
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val newActive = VoltxActiveNumber(
                    phone = number,
                    rangeCode = rangeCode,
                    timestamp = timeStr
                )
                val newActiveList = listOf(newActive) + _uiState.value.activeNumbers

                _uiState.value = _uiState.value.copy(
                    isFetchingNumber = false,
                    phoneInput = number,
                    activeNumbers = newActiveList,
                    successMessage = "Number $number received! Auto-creating account..."
                )

                // Auto create account
                createAccountWithNumber(number)
            }
        }
    }

    fun checkDeviceActivationManually() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCheckingActivation = true,
                activationStatusMessage = "Re-checking activation..."
            )
            checkDeviceActivationInternal()
        }
    }

    private suspend fun checkDeviceActivationInternal() {
        val (active, message) = DeviceActivationService.checkActivation(getApplication())
        _uiState.value = _uiState.value.copy(
            isActivated = active,
            isCheckingActivation = false,
            activationStatusMessage = message
        )
    }

    fun dismissTelegramDialog() {
        prefsRepository.isTelegramJoined = true
        _uiState.value = _uiState.value.copy(showTelegramDialog = false)
    }

    fun onPhoneChanged(newPhone: String) {
        _uiState.value = _uiState.value.copy(phoneInput = newPhone, errorMessage = null)
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(passwordInput = newPassword, errorMessage = null)
        prefsRepository.savedPassword = newPassword
    }

    fun onCountrySelected(country: Country) {
        prefsRepository.selectedCountryCode = country.code
        _uiState.value = _uiState.value.copy(selectedCountry = country)
    }

    private fun createAccountWithNumber(phone: String) {
        val currentState = _uiState.value
        val password = currentState.passwordInput.ifEmpty { "Pass123456" }

        _uiState.value = currentState.copy(isCreating = true, errorMessage = null)

        viewModelScope.launch {
            val result = FbAccountService.createAccount(
                phoneInput = phone,
                passwordInput = password,
                country = currentState.selectedCountry
            )

            if (result.success) {
                val entity = AccountEntity(
                    phone = result.phone,
                    uid = result.uid,
                    name = result.name,
                    password = result.password,
                    cookies = result.cookies
                )
                val newId = accountDao.insertAccount(entity)
                val savedEntity = entity.copy(id = newId)

                // Update active number with UID
                val updatedActives = _uiState.value.activeNumbers.map {
                    if (it.phone == phone) it.copy(accountUid = result.uid) else it
                }

                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    lastCreatedAccount = savedEntity,
                    activeNumbers = updatedActives,
                    successMessage = "Account Created! UID: ${result.uid}. Waiting for OTP..."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    errorMessage = "Account Creation Failed: ${result.error}"
                )
            }
        }
    }

    fun createAccount() {
        val currentState = _uiState.value
        val phone = currentState.phoneInput.trim()
        if (phone.isNotEmpty()) {
            createAccountWithNumber(phone)
        } else if (currentState.selectedRangeCode != null) {
            onRangeClicked(currentState.selectedRangeCode)
        } else if (currentState.facebookRanges.isNotEmpty()) {
            onRangeClicked(currentState.facebookRanges.first())
        } else {
            _uiState.value = currentState.copy(errorMessage = "Please select a range to get a number!")
        }
    }

    fun clearAllAccounts() {
        viewModelScope.launch {
            accountDao.clearAllAccounts()
            _uiState.value = _uiState.value.copy(
                lastCreatedAccount = null,
                successMessage = "All saved accounts deleted!"
            )
        }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch {
            accountDao.deleteAccountById(id)
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label Copied!", Toast.LENGTH_SHORT).show()
    }

    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}

