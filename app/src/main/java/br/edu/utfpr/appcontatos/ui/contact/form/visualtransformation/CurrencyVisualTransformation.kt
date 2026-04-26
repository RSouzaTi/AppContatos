package br.edu.utfpr.appcontatos.ui.contact.form.visualtransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CurrencyVisualTransformation: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val prefix: String = "$ "
        val inputText = text.text
        val symbol : DecimalFormatSymbols = DecimalFormatSymbols (Locale.forLanguageTag("pt-BR"))
        val formatted : DecimalFormat = DecimalFormat("#,##0.00", symbol)

        val number: BigDecimal = if (inputText.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(inputText).movePointLeft(2)
        }
        val formattedNumber = formatted.format(number)

        val finalString = "$prefix$formattedNumber"

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return finalString.length
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= prefix.length) return 0

                return inputText.length
            }
        }
        return TransformedText(
            AnnotatedString(finalString),
            offsetMapping
        )
    }
}
