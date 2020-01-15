package ru.neofusion.undead.myexpenses.repository.storage

import android.content.Context
import android.util.Log

object AuthHelper {
    private val TAG = AuthHelper::class.java.simpleName
    private const val PREFERENCE_KEY_AUTH_KEY = "auth_key"

    @JvmStatic
    fun isLogined(context: Context): Boolean = getKey(context) != null

    @JvmStatic
    fun getKey(context: Context) =
        context.getSharedPreferences()?.getString(PREFERENCE_KEY_AUTH_KEY, null)

    @JvmStatic
    fun login(context: Context, authKey: String?) {
        val sharedPref = context.getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            putString(PREFERENCE_KEY_AUTH_KEY, authKey)
            apply()
        }
        Log.d(TAG, "login")
    }

    @JvmStatic
    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            remove(PREFERENCE_KEY_AUTH_KEY)
            apply()
        }
        Log.d(TAG, "logout")
    }
}