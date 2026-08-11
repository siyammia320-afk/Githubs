package com.example.data

import android.content.Context

class PreferencesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("fb_creator_prefs", Context.MODE_PRIVATE)

    var savedPassword: String
        get() = prefs.getString("saved_password", "") ?: ""
        set(value) = prefs.edit().putString("saved_password", value).apply()

    var isTelegramJoined: Boolean
        get() = prefs.getBoolean("is_telegram_joined", false)
        set(value) = prefs.edit().putBoolean("is_telegram_joined", value).apply()

    var selectedCountryCode: String
        get() = prefs.getString("selected_country_code", "BD") ?: "BD"
        set(value) = prefs.edit().putString("selected_country_code", value).apply()

    var proxyServer: String
        get() = prefs.getString("proxy_server", "") ?: ""
        set(value) = prefs.edit().putString("proxy_server", value).apply()

    var proxyPort: String
        get() = prefs.getString("proxy_port", "") ?: ""
        set(value) = prefs.edit().putString("proxy_port", value).apply()

    var proxyUsername: String
        get() = prefs.getString("proxy_username", "") ?: ""
        set(value) = prefs.edit().putString("proxy_username", value).apply()

    var proxyPassword: String
        get() = prefs.getString("proxy_password", "") ?: ""
        set(value) = prefs.edit().putString("proxy_password", value).apply()

    var isProxyEnabled: Boolean
        get() = prefs.getBoolean("is_proxy_enabled", true)
        set(value) = prefs.edit().putBoolean("is_proxy_enabled", value).apply()

    var isCustomUserAgentEnabled: Boolean
        get() = prefs.getBoolean("is_custom_user_agent_enabled", false)
        set(value) = prefs.edit().putBoolean("is_custom_user_agent_enabled", value).apply()

    var customUserAgent: String
        get() = prefs.getString("custom_user_agent", "") ?: ""
        set(value) = prefs.edit().putString("custom_user_agent", value).apply()

    var activeNumbersJson: String
        get() = prefs.getString("active_numbers_json", "") ?: ""
        set(value) = prefs.edit().putString("active_numbers_json", value).apply()
}
