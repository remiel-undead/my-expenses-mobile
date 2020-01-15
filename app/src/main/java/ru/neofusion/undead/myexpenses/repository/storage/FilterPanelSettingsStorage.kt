package ru.neofusion.undead.myexpenses.repository.storage

import android.content.Context
import android.util.Log
import ru.neofusion.undead.myexpenses.domain.FilterPanelSettings
import ru.neofusion.undead.myexpenses.domain.Period

object FilterPanelSettingsStorage {
    private val TAG = FilterPanelSettingsStorage::class.java.simpleName
    private const val PREFERENCE_PREFIX = "payment_filter_"
    private const val PREFERENCE_KEY_DATE_START_KEY = PREFERENCE_PREFIX + "date_start"
    private const val PREFERENCE_KEY_DATE_END_KEY = PREFERENCE_PREFIX + "date_end"
    private const val PREFERENCE_KEY_CATEGORY_KEY = PREFERENCE_PREFIX + "category"
    private const val PREFERENCE_KEY_USE_SUBCATEGORIES_KEY = PREFERENCE_PREFIX + "use_subcategories"
    private const val PREFERENCE_KEY_PERIOD_KEY = PREFERENCE_PREFIX + "period"

    @JvmStatic
    fun getSettings(context: Context): FilterPanelSettings =
        FilterPanelSettings(
            context.getSharedPreferences()?.getString(PREFERENCE_KEY_DATE_START_KEY, null),
            context.getSharedPreferences()?.getString(PREFERENCE_KEY_DATE_END_KEY, null),
            context.getSharedPreferences()?.getInt(PREFERENCE_KEY_CATEGORY_KEY, -1),
            context.getSharedPreferences()?.getBoolean(PREFERENCE_KEY_USE_SUBCATEGORIES_KEY, true),
            context.getSharedPreferences()?.getString(PREFERENCE_KEY_PERIOD_KEY, null)?.let {
                Period.valueOf(it)
            }
        )

    @JvmStatic
    fun setSettings(
        context: Context,
        dateStart: String?,
        dateEnd: String?,
        category: Int?,
        useSubcategories: Boolean?,
        period: Period?
    ) {
        val sharedPref = context.getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            putString(PREFERENCE_KEY_DATE_START_KEY, dateStart)
            putString(PREFERENCE_KEY_DATE_END_KEY, dateEnd)
            putInt(PREFERENCE_KEY_CATEGORY_KEY, category ?: -1)
            putBoolean(PREFERENCE_KEY_USE_SUBCATEGORIES_KEY, useSubcategories ?: true)
            putString(PREFERENCE_KEY_USE_SUBCATEGORIES_KEY, period?.name)
            apply()
        }
        Log.d(TAG, "setSettings")
    }
}
