package com.example

import android.util.Base64
import kotlin.system.exitProcess

object ZConfig {
    // Base64 encoded link: https://pastebin.com/raw/8viERybm
    private const val B64_URL_PRIMARY = "aHR0cHM6Ly9wYXN0ZWJpbi5jb20vcmF3Lzh2aUVSeWJt"
    private const val B64_URL_SECONDARY = "aHR0cHM6Ly9wYXN0ZWJpbi5jb20vcmF3Lzh2aUVSeWJt"

    fun getRawUrl(): String {
        return try {
            val url1 = String(Base64.decode(B64_URL_PRIMARY, Base64.DEFAULT), Charsets.UTF_8).trim()
            val url2 = String(Base64.decode(B64_URL_SECONDARY, Base64.DEFAULT), Charsets.UTF_8).trim()

            // Strict Tamper Check: if primary and secondary do not match or link is modified
            if (url1 != url2 || !url1.startsWith("https://pastebin.com/raw/")) {
                exitProcess(0)
            }
            url1
        } catch (e: Exception) {
            exitProcess(0)
        }
    }
}
