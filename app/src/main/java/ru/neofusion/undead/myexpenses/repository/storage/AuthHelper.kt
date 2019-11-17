package ru.neofusion.undead.myexpenses.repository.storage

import android.content.Context

object AuthHelper {
    private const val PREFERENCE_KEY_AUTH_KEY = "auth_key"

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    @JvmStatic
    fun isLogined(context: Context): Boolean =
        getSharedPreferences(context)?.getString(PREFERENCE_KEY_AUTH_KEY, null) != null

    @JvmStatic
    fun login(context: Context, authKey: String?) {
        val sharedPref = getSharedPreferences(context) ?: return
        with(sharedPref.edit()) {
            putString(PREFERENCE_KEY_AUTH_KEY, authKey)
            apply()
        }
    }
}