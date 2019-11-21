package ru.neofusion.undead.myexpenses.domain

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class Payment(
    val id: Int,
    val category: String,
    val categoryId: Int,
    val date: Date,
    val description: String?,
    val seller: String?,
    val cost: BigDecimal
) {
    fun getDateFormatted(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)

    fun getCostToString(): String = DecimalFormat("# ###.00").format(cost)
}