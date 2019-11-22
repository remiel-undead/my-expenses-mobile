package ru.neofusion.undead.myexpenses.domain

import java.math.BigDecimal
import java.util.*
import ru.neofusion.undead.myexpenses.DateUtils.formatToString
import ru.neofusion.undead.myexpenses.BigDecimalUtils.toStringRoubles

class Payment(
    val id: Int,
    val category: String,
    val categoryId: Int,
    val date: Date,
    val description: String?,
    val seller: String?,
    val cost: BigDecimal
) {
    fun getDateFormatted(): String = date.formatToString()

    fun getCostToString(): String = cost.toStringRoubles()

}