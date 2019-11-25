package com.example.textwatchertherightway

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var beforeText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.et_amount)

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = p0.toString()
            }

            override fun onTextChanged(
                afterCharSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (afterCharSequence == null)
                    return

                // 1. get cursor position : p0 = start + before
                val initialCursorPosition = start + before

                //2. get digit count after cursor position : c0
                val numOfDigitsToRightOfCursor = getNumberOfDigits(
                    beforeText.substring(
                        initialCursorPosition,
                        beforeText.length
                    )
                )
                val newAmount: String

                if (isDeletingSlash(beforeText, afterCharSequence.toString()).isNotEmpty()) {
                    newAmount = isDeletingSlash(beforeText, afterCharSequence.toString())
                } else {
                    newAmount = formatAmount(afterCharSequence.toString())
                }

                editText.removeTextChangedListener(this)

                editText.setText(newAmount)
                editText.setSelection(getNewCursorPosition(numOfDigitsToRightOfCursor, newAmount))

                editText.addTextChangedListener(this)
            }

            private fun isDeletingSlash(beforeText: String, afterText: String): String {

                // 14 chars -> 13 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{5}$")) && afterText.matches(Regex("^\\d{7}/\\d{5}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 13))

                // 14 chars -> 13 chars : deleting second slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{5}")) && afterText.matches(Regex("\\d{3}/\\d{9}")))
                    return formatAmount(afterText.substring(0, 7) + afterText.substring(8, 13))

                // 13 chars -> 12 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{4}")) && afterText.matches(Regex("\\d{7}/\\d{4}")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 12))

                // 13 chars -> 12 chars : deleting second slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{4}")) && afterText.matches(Regex("\\d{3}/\\d{8}")))
                    return formatAmount(afterText.substring(0, 7) + afterText.substring(8, 12))

                // 12 chars -> 11 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{3}$")) && afterText.matches(Regex("^\\d{7}/\\d{3}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 11))

                // 12 chars -> 11 chars : deleting second slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{3}$")) && afterText.matches(Regex("^\\d{3}/\\d{7}$")))
                    return formatAmount(afterText.substring(0, 7) + afterText.substring(8, 11))

                // 11 chars -> 10 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{2}$")) && afterText.matches(Regex("^\\d{7}/\\d{2}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 10))

                // 11 chars -> 10 chars : deleting second slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d{2}$")) && afterText.matches(Regex("^\\d{3}/\\d{6}$")))
                    return formatAmount(afterText.substring(0, 7) + afterText.substring(8, 10))

                // 10 chars -> 9 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d$")) && afterText.matches(Regex("^\\d{7}/\\d$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 9))

                // 10 chars -> 9 chars : deleting second slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}/\\d$")) && afterText.matches(Regex("^\\d{3}/\\d{5}$")))
                return formatAmount(afterText.substring(0, 7) + afterText.substring(8, 9))

                // 8 chars -> 7 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{4}$")) && afterText.matches(Regex("^\\d{7}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 7))

                // 7 chars -> 6 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{3}$")) && afterText.matches(Regex("^\\d{6}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 6))

                // 6 chars -> 5 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d{2}$")) && afterText.matches(Regex("^\\d{5}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 5))

                // 5 chars -> 4 chars : deleting first slash

                if (beforeText.matches(Regex("^\\d{3}/\\d$")) && afterText.matches(Regex("^\\d{4}$")))
                    return formatAmount(afterText.substring(0, 2) + afterText.substring(3, 4))

                return ""
            }
        })

    }

    fun formatAmount(@NonNull amount: String): String {
        val result = removeNonNumeric(amount)
        return addSlashes(result)
    }

    private fun removeNonNumeric(@NonNull numberString: String): String {
        var numbers = ""
        for (i in numberString) {
            if (i.isDigit())
                numbers += i
        }
        return numbers
    }

    private fun getNewCursorPosition(digitCountToRightOfCursor: Int, numberString: String): Int {
        var position = 0
        var c = digitCountToRightOfCursor
        for (i in numberString.reversed()) {
            if (c == 0)
                break

            if (i.isDigit())
                c--
            position++


        }
        return numberString.length - position
    }

    private fun getNumberOfDigits(@NonNull text: String): Int {
        var count = 0
        for (i in text)
            if (i.isDigit())
                count++
        return count
    }

    private fun addSlashes(input: String): String {
        var input = removeFormatting(input)

        var output = input

        if (input.length in 4..7) {
            output = input.substring(0, 3) + "/" + input.substring(3)
        }

        if (input.length >= 8) {
            output =
                input.substring(0, 3) + "/" + input.substring(3, 7) + "/" + input.substring(7)
        }

        return output
    }

    private fun removeFormatting(input: String): String {
        return input.replace("\\D".toRegex(), "")
    }


}
