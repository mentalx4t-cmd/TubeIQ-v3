package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("creator_auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean(KEY_IS_LOGGED_IN, false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun getGeminiApiKey(): String {
        val savedKey = prefs.getString(KEY_GEMINI_API_KEY, "") ?: ""
        if (savedKey.isNotEmpty()) return savedKey
        // Fallback to BuildConfig if empty
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun getYouTubeApiKey(): String {
        return prefs.getString(KEY_YOUTUBE_API_KEY, "") ?: ""
    }

    fun getCreatorNiche(): String {
        return prefs.getString(KEY_CREATOR_NICHE, "AI & Software Development") ?: "AI & Software Development"
    }

    fun getChannelName(): String {
        return prefs.getString(KEY_CHANNEL_NAME, "AI Coding Ninja") ?: "AI Coding Ninja"
    }

    fun getChannelId(): String {
        return prefs.getString(KEY_CHANNEL_ID, "UCxxxxxxxxxxxxxxxx") ?: "UCxxxxxxxxxxxxxxxx"
    }

    fun login(youtubeKey: String, geminiKey: String, niche: String, channelName: String, channelId: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_YOUTUBE_API_KEY, youtubeKey)
            .putString(KEY_GEMINI_API_KEY, geminiKey)
            .putString(KEY_CREATOR_NICHE, niche)
            .putString(KEY_CHANNEL_NAME, channelName)
            .putString(KEY_CHANNEL_ID, channelId)
            .apply()
        _isLoggedIn.value = true
    }

    fun updateNiche(niche: String) {
        prefs.edit().putString(KEY_CREATOR_NICHE, niche).apply()
    }

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_YOUTUBE_API_KEY, "")
            .putString(KEY_GEMINI_API_KEY, "")
            .putString(KEY_CREATOR_NICHE, "AI & Software Development")
            .putString(KEY_CHANNEL_NAME, "AI Coding Ninja")
            .putString(KEY_CHANNEL_ID, "UCxxxxxxxxxxxxxxxx")
            .apply()
        _isLoggedIn.value = false
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_YOUTUBE_API_KEY = "youtube_api_key"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_CREATOR_NICHE = "creator_niche"
        private const val KEY_CHANNEL_NAME = "channel_name"
        private const val KEY_CHANNEL_ID = "channel_id"
    }
}
