package ru.neofusion.undead.myexpenses.ui

import java.util.*

object DateUtils {
    fun Date.plus(type: Int, amount: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = this.time
        cal.add(type, amount)
        return cal.time
    }
}