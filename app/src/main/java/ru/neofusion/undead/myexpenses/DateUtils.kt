package ru.neofusion.undead.myexpenses

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun Date.plus(type: Int, amount: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = this.time
        cal.add(type, amount)
        return cal.time
    }

    fun Date.formatToString() =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(this)
}