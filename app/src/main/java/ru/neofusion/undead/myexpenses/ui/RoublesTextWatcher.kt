package ru.neofusion.undead.myexpenses.ui

import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.widget.EditText
import java.lang.Exception
import java.util.*
import android.text.SpannableStringBuilder

class RoublesTextWatcher(private val etAmount: EditText) : TextWatcher {
    inner class MoneyValueFilter(private val digits: Int) : DigitsKeyListener(false, true) {
        override fun filter(
            inSource: CharSequence,
            inStart: Int,
            inEnd: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val out = super.filter(inSource, inStart, inEnd, dest, dstart, dend)
            var source = inSource
            var start = inStart
            var end = inEnd

            // if changed, replace the inSource
            if (out != null) {
                source = out
                start = 0
                end = out.length
            }

            val len = end - start

            // if deleting, inSource is empty
            // and deleting can't break anything
            if (len == 0) {
                return source
            }

            val dlen = dest.length

            // Find the position of the decimal .
            for (i in 0 until dstart) {
                if (dest[i] == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString(
                        if (dlen - (i + 1) + len > digits) "" else SpannableStringBuilder(
                            inSource,
                            start,
                            end
                        ).toString()
                    )
                }
            }

            for (i in start until end) {
                if (source[i] == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    return if (dlen - dend + (end - (i + 1)) > digits)
                        ""
                    else
                        break
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(SpannableStringBuilder(source, start, end).toString())
        }

    }

    override fun afterTextChanged(s: Editable?) {
        val cursorPosition = etAmount.selectionEnd
        val originalStr = etAmount.text.toString()

        etAmount.filters = arrayOf(MoneyValueFilter(2))

        try {
            etAmount.removeTextChangedListener(this)
            val value = etAmount.text.toString()

            if (value.isNotEmpty()) {
                if (value.startsWith(".")) {
                    etAmount.setText("0.")
                }
                if (value.startsWith("0") && !value.startsWith("0.")) {
                    etAmount.setText("")
                }
                val str = etAmount.text.toString().replace(" ", "")
                if (value != "") {
                    etAmount.setText(getDecimalFormattedString(str))
                }
                val diff = etAmount.text.toString().length - originalStr.length
                etAmount.setSelection(cursorPosition + diff)
            }
            etAmount.addTextChangedListener(this)
        } catch (ex: Exception) {
            etAmount.addTextChangedListener(this)
        }
    }

    private fun getDecimalFormattedString(value: String): String {
        if (value.isNotEmpty()) {
            val lst = StringTokenizer(value, ".")
            var str1 = value
            var str2 = ""
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken()
                str2 = lst.nextToken()
            }
            var str3 = ""
            var i = 0
            var j = -1 + str1.length
            if (str1.get(-1 + str1.length) == '.') {
                j--
                str3 = "."
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.isNotEmpty())
                        str3 = "$str3.$str2"
                    return str3
                }
                if (i == 3) {
                    str3 = " $str3"
                    i = 0
                }
                str3 = str1[k] + str3
                i++
                k--
            }
        }
        return ""
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}