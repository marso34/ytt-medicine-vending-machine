package com.wonchihyeon.ytt_android.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

// sharedPreference로 키값 저장
object TokenManager {

    private const val PREFS_NAME = "secure_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"

    private fun getPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    // Access Token 저장
    fun saveAccessToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    // Refresh Token 저장
    fun saveRefreshToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    // Access Token 가져오기
    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null)
    }

    // Refresh Token 가져오기
    fun getRefreshToken(context: Context): String? {
        return getPrefs(context).getString(KEY_REFRESH_TOKEN, null)
    }

    // 모든 토큰 삭제 (로그아웃 시 사용)
    fun clearTokens(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
