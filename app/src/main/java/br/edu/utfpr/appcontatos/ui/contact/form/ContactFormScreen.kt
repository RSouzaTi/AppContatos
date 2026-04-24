package br.edu.utfpr.appcontatos.ui.contact.form

import android.R.attr.value
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormCheckBox
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormDatePicker
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormFiledRow
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormRadioButton
import br.edu.utfpr.appcontatos.ui.contact.form.composables.FormTextField
import br.edu.utfpr.appcontatos.ui.shared.composables.ContactAvatar
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultErrorState
import br.edu.utfpr.appcontatos.ui.shared.composables.DefaultLoadingState
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme


@Composable
fun ContactFormScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    viewModel: ContactFormViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onContactSaved: () -> Unit = {}

) {
    LaunchedEffect(snackbarHostState, viewModel.uiState.contactSaved) {
        if (viewModel.uiState.contactSaved) {
            onContactSaved()
        }
    }
    LaunchedEffect(snackbarHostState, viewModel.uiState.hasErrorSaving) {
        if (viewModel.uiState.hasErrorSaving) {
            snackbarHostState.showSnackbar(
                message = "Ocorreu um erro ao salvar. Aguarde um momento e tente novamente."
            )
        }
    }
    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.uiState.isLoading) {
        DefaultLoadingState(
            modifier = contentModifier
        )
    } else if (viewModel.uiState.hasErrorLoading) {
        DefaultErrorState(
            modifier = contentModifier,
            onTryAgainPressed = { viewModel.loadContact() }
        )
    }
    Scaffold(
        modifier = contentModifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar(
                isNewContact = true,
                onBackPressed = onBackPressed,
                isSaving = viewModel.uiState.isSaving,
                onSavePressed = viewModel::save
            )
        }
    ) { paddingValues ->
        FormContent(
            modifier = Modifier.padding(paddingValues),
            onFormEvent = viewModel::onFormEvent,
            isSaving = viewModel.uiState.isSaving

        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    isNewContact: Boolean,
    onBackPressed: () -> Unit,
    isSaving: Boolean,
    onSavePressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(if (isNewContact) "Novo contato" else "Editar contato")
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar"
                )
            }
        },
        actions = {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onSavePressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Salvar"
                    )
                }
            }
        }
    )
}

private class BooleanParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)

}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview(
    @PreviewParameter(BooleanParameterProvider::class) isNewContact: Boolean
) {
    AppContatosTheme {
        AppBar(
            isNewContact = isNewContact,
            onBackPressed = {},
            isSaving = false,
            onSavePressed = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AppBarPreviewSaving(
@PreviewParameter(BooleanParameterProvider::class) isSaving: Boolean
) {
    AppContatosTheme {
        AppBar(
            isNewContact = true,
            onBackPressed = {},
            isSaving = isSaving,
            onSavePressed = {}
        )
    }
}


@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    formState: FormState<ContactTypeEnum> = FormState(),
    onFormEvent: (FormEvent) -> Unit,
    isSaving: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val formTextFieldModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ContactAvatar(
            modifier = Modifier.padding(bottom = 16.dp),
            firstName = formState.firstName.value,
            lastName = formState.lastName.value,
            size = 150.dp,
            textStyles = MaterialTheme.typography.displayLarge
        )
        FormFiledRow(
            label = "Nome",
            imageVector = Icons.Filled.Person
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Nome",
                value = formState.firstName.value,
                onValueChange = {`newValue` ->
                    onFormEvent(FormEvent.UpdateFirstName(newValue))
                },
                keyboardCapitalization = KeyboardCapitalization.Words,
                enabled = !isSaving



            )
        }
        FormFiledRow (
            label = "Sobrenome"
        ){
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Sobrenome",
                value = formState.lastName.value,
                errorMessage = formState.lastName.errorMessage,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdateLastName(newValue))
                },
                keyboardCapitalization = KeyboardCapitalization.Words,
                enabled = !isSaving
            )
        }
        FormFiledRow(
            label = "Telefone",
            imageVector = Icons.Filled.Phone
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Telefone",
                value = formState.phoneNumber.value,
                errorMessage = formState.phoneNumber.errorMessage,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdatePhoneNumber(newValue))
                },
                keyboardType = KeyboardType.Phone,
                enabled = !isSaving

            )

        }
        FormFiledRow(
            label = "E-mail",
            imageVector = Icons.Filled.Mail
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "E-mail",
                value = formState.email.value,
                errorMessage = formState.email.errorMessage,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdateEmail(newValue))
                },
                keyboardType = KeyboardType.Email,
                enabled = !isSaving

            )
        }
        FormFiledRow(
            label = "Data de aniversário"
        ) {
            FormDatePicker(
                modifier = formTextFieldModifier,
                label = "Data de aniversário",
                value = formState.birthDate.value,
                errorMessage = formState.birthDate.errorMessage,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdateBirthDate(newValue))
                },
                enabled = !isSaving
            )
        }
        FormFiledRow(
            label = "Valor patrimonial",
            imageVector = Icons.Filled.AttachMoney
        ) {
            FormTextField(
                modifier = formTextFieldModifier,
                label = "Valor patrimonial",
                value = formState.assetsValue.toString(),
                errorMessage = formState.assetsValue.errorMessage,
                onValueChange = { newValue ->
                    onFormEvent(FormEvent.UpdateAssetsValue(newValue))
                },
                keyboardType = KeyboardType.Number,
                enabled = !isSaving
            )
        }
        val choiceOptionsModifier = Modifier.padding(8.dp)
        FormFiledRow(
            label = "Favorito"
        ) {
            FormCheckBox(
                modifier = choiceOptionsModifier,
                label = "Favorito",
                checked = formState.isFavorite.value,
                onCheckChanged = {newValue ->
                    onFormEvent(FormEvent.UpdateIsFavorite(newValue))
                },
                enabled = !isSaving
            )
        }
        FormFiledRow(
            label = "Tipo"
        ) {
            FormRadioButton(
                modifier = choiceOptionsModifier,
                label = "Pessoal",
                value = ContactTypeEnum.PERSONAL,
                groupValue = formState.type.value,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdateType(newValue))
                },
                enabled = !isSaving
            )

            FormRadioButton(
                modifier = choiceOptionsModifier,
                label = "Profissional",
                value = ContactTypeEnum.PROFESSIONAL,
                groupValue = formState.type.value,
                onValueChange = {newValue ->
                    onFormEvent(FormEvent.UpdateType(newValue))
                },
                enabled = !isSaving
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormContentPreview() {
    AppContatosTheme {
        FormContent(
            formState = FormState(),
            onFormEvent = {},
            isSaving = false
        )
    }
}

