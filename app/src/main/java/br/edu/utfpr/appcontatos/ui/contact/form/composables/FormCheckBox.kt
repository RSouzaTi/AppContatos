package br.edu.utfpr.appcontatos.ui.contact.form.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme

@Composable
fun FormCheckBox (
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    enabled: Boolean = true
    ) {
    Row(
        modifier = modifier
            .clickable{ onCheckChanged(!checked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChanged,
            enabled = enabled
        )
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
private fun FormCheckBoxPreview() {
    AppContatosTheme {
        var  checked by remember { mutableStateOf(false) }
        FormCheckBox(
            label = "Favorito",
            checked = checked,
            onCheckChanged = { newValue ->
                checked = newValue
            }
        )
    }
}