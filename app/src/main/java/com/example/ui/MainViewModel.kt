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

data class AccountCreatorUiState(
    val phoneInput: String = "",
    val passwordInput: String = "",
    val isCreating: Boolean = false,
    val lastCreatedAccount: AccountEntity? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showTelegramDialog: Boolean = false,
    val deviceId: String = "",
    val isActivated: Boolean = false,
    val isCheckingActivation: Boolean = false,
    val activationStatusMessage: String = "Checking device status..."
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
        val showTelegram = !prefsRepository.isTelegramJoined
        val devId = DeviceActivationService.getDeviceId(application)

        _uiState.value = _uiState.value.copy(
            passwordInput = savedPassword,
            showTelegramDialog = showTelegram,
            deviceId = devId
        )

        // Start periodic activation check (every 1 minute)
        startPeriodicActivationCheck()
    }

    private fun startPeriodicActivationCheck() {
        viewModelScope.launch {
            while (isActive) {
                checkDeviceActivationInternal()
                delay(60_000) // repeat every 60 seconds (1 minute)
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

    fun createAccount() {
        val currentState = _uiState.value

        if (!currentState.isActivated) {
            _uiState.value = currentState.copy(errorMessage = "Device not activated! Please activate your device first.")
            return
        }

        val phone = currentState.phoneInput.trim()
        val password = currentState.passwordInput.trim()

        if (phone.isEmpty()) {
            _uiState.value = currentState.copy(errorMessage = "Phone number is required!")
            return
        }

        if (password.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "Password must be at least 6 characters!")
            return
        }

        _uiState.value = currentState.copy(isCreating = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val result = FbAccountService.createAccount(
                phoneInput = phone,
                passwordInput = password
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

                // Save password persistently if modified
                prefsRepository.savedPassword = password

                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    lastCreatedAccount = savedEntity,
                    successMessage = "Account created successfully! UID: ${result.uid}"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    errorMessage = "Failed to create account: ${result.error}"
                )
            }
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
