package com.example.network

import android.content.Context
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

object DeviceActivationService {

    private const val FIREBASE_DB_URL = "https://admin-pnal-ed74f-default-rtdb.firebaseio.com"

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        if (!androidId.isNullOrEmpty() && androidId != "9774d56d682e549c" && androidId.length >= 8) {
            return androidId.uppercase()
        }
        val prefs = context.getSharedPreferences("fb_creator_prefs", Context.MODE_PRIVATE)
        var customId = prefs.getString("unique_device_id", null)
        if (customId.isNullOrEmpty()) {
            customId = UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
            prefs.edit().putString("unique_device_id", customId).apply()
        }
        return customId
    }

    suspend fun checkActivation(context: Context): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val deviceId = getDeviceId(context)
        val upperId = deviceId.uppercase()

        val deviceUrl = "$FIREBASE_DB_URL/devices/$upperId.json"

        try {
            val request = Request.Builder()
                .url(deviceUrl)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Pair(false, "Server Error (${response.code})")
                }
                val bodyString = response.body?.string()?.trim() ?: ""

                if (bodyString.isEmpty() || bodyString == "null") {
                    return@withContext checkAllDevicesFallback(upperId)
                }

                return@withContext parseDeviceData(bodyString, upperId)
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Connection failed: ${e.localizedMessage}")
        }
    }

    private suspend fun checkAllDevicesFallback(deviceId: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val allDevicesUrl = "$FIREBASE_DB_URL/devices.json"
        try {
            val request = Request.Builder()
                .url(allDevicesUrl)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Pair(false, "Device ID ($deviceId) is NOT activated.")
                }
                val bodyString = response.body?.string()?.trim() ?: ""
                if (bodyString.isEmpty() || bodyString == "null" || !bodyString.startsWith("{")) {
                    return@withContext Pair(false, "Device ID ($deviceId) is NOT activated.")
                }

                val json = JSONObject(bodyString)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (key.equals(deviceId, ignoreCase = true)) {
                        val deviceObj = json.optJSONObject(key)
                        if (deviceObj != null) {
                            return@withContext parseDeviceData(deviceObj.toString(), deviceId)
                        }
                    }
                }

                return@withContext Pair(false, "Device ID ($deviceId) is NOT approved by Admin.")
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Device ID ($deviceId) is NOT activated.")
        }
    }

    private fun parseDeviceData(jsonStr: String, deviceId: String): Pair<Boolean, String> {
        return try {
            val json = JSONObject(jsonStr)

            val status = json.optString("status", "approved").lowercase(Locale.ROOT)
            val userName = json.optString("userName", "User")
            val expiryTimestamp = json.optLong("expiryTimestamp", 0L)

            // Check Banned status
            if (status == "banned" || status == "ban") {
                return Pair(false, "Device BANNED ($userName - $deviceId). Contact Admin!")
            }

            // Check Approved status
            if (status != "approved" && status != "active" && status != "ok") {
                return Pair(false, "Device Status: $status ($deviceId)")
            }

            // Check Expiration
            if (expiryTimestamp > 0L) {
                val now = System.currentTimeMillis()
                if (now > expiryTimestamp) {
                    val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault()).format(Date(expiryTimestamp))
                    return Pair(false, "Subscription EXPIRED on $format ($userName).")
                }
            }

            Pair(true, "Device Activated ($userName)")
        } catch (e: Exception) {
            Pair(false, "Invalid device license format.")
        }
    }
}
