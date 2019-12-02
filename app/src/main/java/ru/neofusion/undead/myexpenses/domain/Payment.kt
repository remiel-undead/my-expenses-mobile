package ru.neofusion.undead.myexpenses.domain

import java.util.*
import ru.neofusion.undead.myexpenses.DateUtils.formatToString

class Payment(
    val id: Int,
    val category: Category,
    val date: Date,
    val description: String?,
    val seller: String?,
    val cost: String
) {
    fun getDateFormatted(): String = date.formatToString()
    fun getViewableDescription(): String =
        when {
            description.isNullOrEmpty() -> seller.orEmpty()
            seller.isNullOrEmpty() -> description.orEmpty()
            else -> "$description / $seller"
        }
}