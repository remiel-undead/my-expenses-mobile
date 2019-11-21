package ru.neofusion.undead.myexpenses.domain

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import ru.neofusion.undead.myexpenses.DateUtils.formatToString

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

    fun getCostToString(): String =
        DecimalFormat().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
            isGroupingUsed = true
        }.format(cost.setScale(2, BigDecimal.ROUND_DOWN))
}