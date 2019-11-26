package ru.neofusion.undead.myexpenses

import java.math.BigDecimal
import java.text.DecimalFormat

object BigDecimalUtils {
    private val decimalFormat = DecimalFormat().apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        isGroupingUsed = true
    }

    fun Int.toBigDecimalRoubles(): BigDecimal = BigDecimal(this).divide(BigDecimal(100))
    fun BigDecimal.toIntRoubles(): Int = this.multiply(BigDecimal(100)).toInt()

    fun BigDecimal.toStringRoubles(): String =
        decimalFormat.format(this.setScale(2, BigDecimal.ROUND_DOWN))

    fun String.toBigDecimalRoubles(): BigDecimal {
        var value = this.replace(" ", "")
        value = value.replace(",", ".")
        return BigDecimal(value).setScale(2, BigDecimal.ROUND_DOWN)
    }
}