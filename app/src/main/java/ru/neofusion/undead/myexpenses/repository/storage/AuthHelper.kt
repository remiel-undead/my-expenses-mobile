package ru.neofusion.undead.myexpenses.repository.storage

import android.content.Context
import android.util.Log

object AuthHelper {
    private val TAG = AuthHelper::class.java.simpleName
    private const val PREFERENCE_KEY_AUTH_KEY = "auth_key"

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    @JvmStatic
    fun isLogined(context: Context): Boolean = getKey(context) != null

    @JvmStatic
    fun getKey(context: Context) =
        getSharedPreferences(context)?.getString(PREFERENCE_KEY_AUTH_KEY, null)

    @JvmStatic
    fun login(context: Context, authKey: String?) {
        val sharedPref = getSharedPreferences(context) ?: return
        with(sharedPref.edit()) {
            putString(PREFERENCE_KEY_AUTH_KEY, authKey)
            apply()
        }
        Log.d(TAG, "login")
    }

    @JvmStatic
    fun logout(context: Context) {
        val sharedPref = getSharedPreferences(context) ?: return
        with(sharedPref.edit()) {
            remove(PREFERENCE_KEY_AUTH_KEY)
            apply()
        }
        Log.d(TAG, "logout")
    }
}