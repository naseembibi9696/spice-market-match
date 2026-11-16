package com.spicemarket.match.data.local

import android.content.Context
import android.content.SharedPreferences

class SpicePreferences(context: Context) {

    private val store: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = store.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        store.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = store.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        store.edit().putBoolean(key, value).apply()
    }

    fun readText(key: String, fallback: String): String = store.getString(key, fallback) ?: fallback

    fun writeText(key: String, value: String) {
        store.edit().putString(key, value).apply()
    }

    fun wipe() {
        store.edit().clear().apply()
    }

    private companion object {
        const val STORE_NAME = "spice_market_match"
    }
}
