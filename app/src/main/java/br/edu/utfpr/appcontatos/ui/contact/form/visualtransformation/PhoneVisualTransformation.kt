package br.edu.utfpr.appcontatos.ui.contact.form.visualtransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation : VisualTransformation {
    //(000) 0000-0000
    //(000) 00000-0000
    override fun filter(text: AnnotatedString): TransformedText {
        val inputText = text.text
        val formattedPhone = StringBuilder()

        for (i in inputText.indices) {
            if (i == 0) formattedPhone.append("(")
            formattedPhone.append(inputText[i])
            if (i == 2) formattedPhone.append(") ")
            if (i == 6 && inputText.length <= 10) formattedPhone.append("-")
            if (i == 7 && inputText.length > 10) formattedPhone.append("-")
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset > 7 && inputText.length > 10 -> offset + 4
                    offset > 6 && inputText.length <= 10 -> offset + 4
                    offset > 2 -> offset + 3
                    offset > 0 -> offset + 1
                    else -> offset
                }.coerceAtMost(formattedPhone.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset > 11 && inputText.length > 10 -> offset - 4
                    offset > 10 && inputText.length <= 10 -> offset - 4
                    offset > 5 -> offset - 3
                    offset > 0 -> offset - 1
                    else -> offset
                }.coerceAtMost(inputText.length)
            }
        }
        return TransformedText(
            AnnotatedString(formattedPhone.toString()),
            offsetMapping
        )
    }
}
