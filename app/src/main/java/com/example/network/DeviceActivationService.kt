package com.example.network

import android.content.Context
import android.provider.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

import com.example.ZConfig

object DeviceActivationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
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
        val rawUrl = ZConfig.getRawUrl()
        try {
            val request = Request.Builder()
                .url(rawUrl)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Pair(false, "Server HTTP ${response.code}")
                }
                val bodyString = response.body?.string() ?: ""

                val isApproved = parseAndCheckDeviceId(bodyString, deviceId)
                if (isApproved) {
                    return@withContext Pair(true, "Device Activated")
                } else {
                    return@withContext Pair(false, "Device ID ($deviceId) is not activated.")
                }
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Connection failed: ${e.localizedMessage}")
        }
    }

    private fun parseAndCheckDeviceId(rawText: String, deviceId: String): Boolean {
        if (rawText.isBlank()) return false

        // 1. Direct contains check (case insensitive)
        if (rawText.contains(deviceId, ignoreCase = true)) {
            return true
        }

        // 2. JSON array/object parsing check
        try {
            val trimmed = rawText.trim()
            if (trimmed.startsWith("[")) {
                val array = JSONArray(trimmed)
                for (i in 0 until array.length()) {
                    if (array.getString(i).equals(deviceId, ignoreCase = true)) {
                        return true
                    }
                }
            } else if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                val keys = listOf("active_devices", "devices", "allowed_ids", "ids", "data")
                for (key in keys) {
                    if (obj.has(key)) {
                        val arr = obj.optJSONArray(key)
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                if (arr.getString(i).equals(deviceId, ignoreCase = true)) {
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) { }

        return false
    }
}
