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
}
