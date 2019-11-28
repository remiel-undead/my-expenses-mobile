package ru.neofusion.undead.myexpenses

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val DATE_PATTERN = "yyyy-MM-dd"
    fun Date.plus(type: Int, amount: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = this.time
        cal.add(type, amount)
        return cal.time
    }

    fun Date.formatToString(): String =
        SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(this)

    fun String.formatToDate(): Date? =
        SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).parse(this)
}