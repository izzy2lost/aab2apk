package com.aab2apk.local

import android.content.Context
import android.content.SharedPreferences

class FileStorageHelper(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("aab2apk_prefs", Context.MODE_PRIVATE)

    fun save(key: String, value: Any) {
        when (value) {
            is String -> prefs.edit().putString(key, value).apply()
            is Int -> prefs.edit().putInt(key, value).apply()
            is Boolean -> prefs.edit().putBoolean(key, value).apply()
            is Float -> prefs.edit().putFloat(key, value).apply()
            is Long -> prefs.edit().putLong(key, value).apply()
            else -> prefs.edit().putString(key, value.toString()).apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> read(key: String, defaultValue: T? = null): T? {
        return when (defaultValue) {
            is String -> prefs.getString(key, defaultValue) as T?
            is Int -> prefs.getInt(key, defaultValue) as T?
            is Boolean -> prefs.getBoolean(key, defaultValue) as T?
            is Float -> prefs.getFloat(key, defaultValue) as T?
            is Long -> prefs.getLong(key, defaultValue) as T?
            null -> when {
                prefs.contains(key) -> prefs.getString(key, null) as T?
                else -> null
            }
            else -> prefs.getString(key, defaultValue.toString()) as T?
        }
    }

    fun delete(key: String): Boolean {
        return if (prefs.contains(key)) {
            prefs.edit().remove(key).apply()
            true
        } else {
            false
        }
    }
}