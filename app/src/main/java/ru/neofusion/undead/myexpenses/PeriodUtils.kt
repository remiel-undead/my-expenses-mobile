package ru.neofusion.undead.myexpenses

import ru.neofusion.undead.myexpenses.domain.Period
import java.util.*

object PeriodUtils {
    fun Period.getDates(): Pair<Date, Date>? {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        return when (this) {
            Period.TODAY -> {
                calendarStart.time to calendarEnd.time
            }
            Period.YESTERDAY -> {
                calendarStart.add(Calendar.DAY_OF_YEAR, -1)
                calendarEnd.add(Calendar.DAY_OF_YEAR, -1)
                calendarStart.time to calendarEnd.time
            }
            Period.THIS_WEEK -> {
                calendarStart.set(Calendar.DAY_OF_WEEK, calendarStart.firstDayOfWeek)
                calendarEnd.set(
                    Calendar.DAY_OF_WEEK,
                    calendarEnd.getActualMaximum(Calendar.DAY_OF_WEEK)
                )
                calendarStart.time to calendarEnd.time
            }
            Period.THIS_MONTH -> {
                calendarStart.set(Calendar.DAY_OF_MONTH, 1)
                calendarEnd.set(
                    Calendar.DAY_OF_MONTH,
                    calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                calendarStart.time to calendarEnd.time
            }
            Period.LAST_MONTH -> {
                calendarStart.set(Calendar.DAY_OF_MONTH, 1)
                calendarStart.add(Calendar.MONTH, -1)
                calendarEnd.set(
                    Calendar.DAY_OF_MONTH,
                    calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
                )
                calendarEnd.add(Calendar.MONTH, -1)
                calendarStart.time to calendarEnd.time
            }
            Period.THIS_YEAR -> {
                calendarStart.set(Calendar.DAY_OF_YEAR, 1)
                calendarEnd.set(
                    Calendar.DAY_OF_YEAR,
                    calendarEnd.getActualMaximum(Calendar.DAY_OF_YEAR)
                )
                calendarStart.time to calendarEnd.time
            }
            Period.CUSTOM -> {
                null
            }
        }
    }
}