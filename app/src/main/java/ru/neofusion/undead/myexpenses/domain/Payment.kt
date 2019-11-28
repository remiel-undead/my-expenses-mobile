package ru.neofusion.undead.myexpenses.domain

import java.util.*
import ru.neofusion.undead.myexpenses.DateUtils.formatToString

class Payment(
    val id: Int,
    val category: String,
    val categoryId: Int,
    val date: Date,
    val description: String?,
    val seller: String?,
    val cost: String
) {
    fun getDateFormatted(): String = date.formatToString()
}