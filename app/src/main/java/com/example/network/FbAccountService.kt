package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class AccountResult(
    val success: Boolean,
    val uid: String = "",
    val name: String = "",
    val cookies: String = "",
    val password: String = "",
    val phone: String = "",
    val error: String = ""
)

object FbAccountService {

    private val FRENCH_NAMES = listOf(
        Pair("Jean", "Dupont"), Pair("Marie", "Martin"),
        Pair("Pierre", "Durand"), Pair("Sophie", "Lefèvre"),
        Pair("Lucas", "Moreau"), Pair("Emma", "Petit"),
        Pair("Louis", "Roux"), Pair("Chloé", "Richard"),
        Pair("Hugo", "Simon"), Pair("Inès", "Laurent")
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun generateDatrCookie(): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-"
        return (1..24)
            .map { allowedChars.random() }
            .joinToString("")
    }

    suspend fun createAccount(
        phoneInput: String,
        passwordInput: String
    ): AccountResult = withContext(Dispatchers.IO) {
        val datrCookie = generateDatrCookie()
        val (fname, lname) = FRENCH_NAMES.random()
        val day = Random.nextInt(1, 29)
        val month = Random.nextInt(1, 13)
        val year = Random.nextInt(1980, 2006)
        val phone = phoneInput.replace(Regex("[^0-9]"), "")

        val userAgent = "Mozilla/5.0 (Linux; Android 12; itel S665L Build/SP1A.210812.016) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.7827.91 Mobile Safari/537.36"

        val formBodyBuilder = FormBody.Builder()
            .add("ccp", "2")
            .add("reg_instance", datrCookie)
            .add("submission_request", "true")
            .add("helper", "")
            .add("reg_impression_id", UUID.randomUUID().toString())
            .add("ns", "1")
            .add("zero_header_af_client", "")
            .add("app_id", "103")
            .add("logger_id", UUID.randomUUID().toString())
            .add("field_names[0]", "firstname")
            .add("firstname", fname)
            .add("lastname", lname)
            .add("field_names[1]", "birthday_wrapper")
            .add("birthday_day", day.toString())
            .add("birthday_month", month.toString())
            .add("birthday_year", year.toString())
            .add("age_step_input", "")
            .add("did_use_age", "false")
            .add("field_names[2]", "reg_email__")
            .add("reg_email__", phone)
            .add("field_names[3]", "sex")
            .add("sex", "2")
            .add("preferred_pronoun", "")
            .add("custom_gender", "")
            .add("reg_passwd__", passwordInput)
            .add("name_suggest_elig", "false")
            .add("was_shown_name_suggestions", "false")
            .add("did_use_suggested_name", "false")
            .add("use_custom_gender", "false")
            .add("guid", "")
            .add("pre_form_step", "")
            .add("submit", "Sign up")
            .add("fb_dtsg", "NAfx5UxG44eai86HC1iwiixBs1mUDFhn3ccN1fj3-SJJc64TeUsEAEg:0:0")
            .add("jazoest", "24748")
            .add("lsd", "AdRCh7SdER7Za5PotUuics5fFt0")
            .add("__dyn", "1Z3pawlEnwm8_Bg9ppoW5UdE4a2i5U4e0C86u7E39x60zU3ex608ewk9E4W0pKq0FE6S0x81vohw73wGwcq1GwqU2YwbK0oi0zE1jU1soG0hi0Lo6-0Co1kU1UU3jwea")
            .add("__csr", "")
            .add("__hsdp", "")
            .add("__hblp", "")
            .add("__sjsp", "")
            .add("__req", "g")
            .add("__fmt", "1")
            .add("__a", "AYzJ_41FhHOHmeaJtz_y-NZ41BrpCkk8MZbenM7ATpRLY9c4d3QLNQW9sph6SN5jNJBH5tH1yvE_P-EybRqM6tZ_nqLEaV4b3ZU")
            .add("__user", "0")

        val url = "https://limited.facebook.com/reg/submit/?privacy_mutation_token=eyJ0eXBlIjowLCJjcmVhdGlvbl90aW1lIjoxNzgyMTQ5MzY4LCJjYWxsc2l0ZV9pZCI6OTA3OTI0NDAyOTQ4MDU4fQ%3D%3D&app_id=103&multi_step_form=1&skip_suma=0&shouldForceMTouch=1"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "fr-FR,fr;q=0.9,en;q=0.8")
            .header("Accept-Encoding", "gzip, deflate, br, zstd")
            .header("Connection", "keep-alive")
            .header("Upgrade-Insecure-Requests", "1")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("sec-ch-ua-platform", "\"Android\"")
            .header("sec-ch-ua", "\"Android WebView\";v=\"149\", \"Chromium\";v=\"149\", \"Not)A;Brand\";v=\"24\"")
            .header("x-response-format", "JSONStream")
            .header("sec-ch-ua-mobile", "?1")
            .header("x-asbd-id", "359341")
            .header("x-fb-lsd", "AdRCh7SdER7Za5PotUuics5fFt0")
            .header("x-requested-with", "XMLHttpRequest")
            .header("origin", "https://limited.facebook.com")
            .header("sec-fetch-site", "same-origin")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-dest", "empty")
            .header("referer", "https://limited.facebook.com/reg/?is_two_steps_login=0&cid=103&refsrc=deprecated&soft=hjk")
            .header("priority", "u=1, i")
            .header("Cookie", "datr=$datrCookie")
            .post(formBodyBuilder.build())
            .build()

        try {
            val startTime = System.currentTimeMillis()
            val response = client.newCall(request).execute()
            val elapsedTime = System.currentTimeMillis() - startTime

            if (response.isSuccessful) {
                val rawCookies = response.headers.values("Set-Cookie")
                val cookieMap = mutableMapOf<String, String>()

                for (header in rawCookies) {
                    val parts = header.split(";")[0].split("=", limit = 2)
                    if (parts.size == 2) {
                        cookieMap[parts[0].trim()] = parts[1].trim()
                    }
                }

                val cUser = cookieMap["c_user"]
                if (!cUser.isNullOrEmpty()) {
                    val requiredKeys = listOf("datr", "sb", "ps_l", "ps_n", "m_pixel_ratio", "wd", "c_user", "fr", "xs")
                    val cookieParts = mutableListOf<String>()
                    for (k in requiredKeys) {
                        if (cookieMap.containsKey(k)) {
                            cookieParts.add("$k=${cookieMap[k]?.replace(" ", "")}")
                        } else if (k == "datr") {
                            cookieParts.add("datr=$datrCookie")
                        }
                    }
                    val cookieString = cookieParts.joinToString("; ")
                    AccountResult(
                        success = true,
                        uid = cUser,
                        name = "$fname $lname",
                        cookies = cookieString,
                        password = passwordInput,
                        phone = phone
                    )
                } else {
                    AccountResult(
                        success = false,
                        error = "No c_user found in cookies. Registration response did not assign a UID."
                    )
                }
            } else {
                AccountResult(
                    success = false,
                    error = "HTTP error ${response.code}: ${response.message}"
                )
            }
        } catch (e: Exception) {
            AccountResult(
                success = false,
                error = e.localizedMessage ?: "Network connection error"
            )
        }
    }
}
