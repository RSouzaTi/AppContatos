package br.edu.utfpr.appcontatos.ui.contact.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.appcontatos.data.ContactDatasource
import br.edu.utfpr.appcontatos.data.ContactTypeEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.random.Random

class ContactFormViewModel(
    savedStateHandle : SavedStateHandle
 ) : ViewModel() {
    private val contactId: Int = savedStateHandle.get<String>("id")?.toInt() ?: 0
    var uiState: ContactFormState by mutableStateOf(
        ContactFormState(
            contactId = contactId
        )
    )

    init {
        if (!uiState.isNewContact) {
            loadContact()
        }
    }

    fun loadContact() {
        uiState = uiState.copy(
            isLoading = false,
            hasErrorLoading = false

        )
        viewModelScope.launch {
            delay(2000)
            val contact = ContactDatasource.instance.findById(contactId)
            val hasError = Random.nextBoolean()
            uiState = if (contact == null || hasError) {
                uiState.copy(
                    isLoading = false,
                    hasErrorLoading = true
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    contact = contact,
                    formState = FormState(
                        firstName = FormField(contact.firstName),
                        lastName = FormField(contact.lastName),
                        phoneNumber = FormField(contact.phoneNumber),
                        email = FormField(contact.email),
                        isFavorite = FormField(contact.isFavorite),
                        assetsValue = FormField(contact.assetsValue.toString()),
                        birthDate = FormField(contact.birthDate),
                        type = FormField(contact.type)
                    )
                )
            }
        }
    }

    fun onFormEvent(event: FormEvent) {
        when (event) {
            is FormEvent.UpdateFirstName -> onFirstNameChanged(event.newValue)
            is FormEvent.UpdateAssetsValue -> onAssetsValueChanged(event.newValue)
            is FormEvent.UpdateBirthDate -> onBirthDateChanged(event.newValue)
            is FormEvent.UpdateEmail -> onEmailChanged(event.newValue)
            is FormEvent.UpdateIsFavorite -> onIsFavoriteChanged(event.newValue)
            is FormEvent.UpdateLastName -> onLastNameChanged(event.newValue)
            is FormEvent.UpdatePhoneNumber -> onPhoneNumberChanged(event.newValue)
            is FormEvent.UpdateType -> onTypeChanged(event.newValue)
        }
    }

    fun save() {
        if (uiState.isSaving || !isValidForm()) return

        uiState = uiState.copy(
            isSaving = true,
            hasErrorSaving = false
        )
        viewModelScope.launch {
            delay(2000)
            val hasError = Random.nextBoolean()
            uiState = if (hasError) {
                uiState.copy(
                    isSaving = false,
                    hasErrorSaving = true
                )
            } else {
                val contactToSave = uiState.contact.copy(
                    firstName = uiState.formState.firstName.value,
                    lastName = uiState.formState.lastName.value,
                    phoneNumber = uiState.formState.phoneNumber.value,
                    email = uiState.formState.email.value,
                    isFavorite = uiState.formState.isFavorite.value,
                    assetsValue = uiState.formState.assetsValue.value.let {
                        if (it.isBlank()) {
                            BigDecimal.ZERO
                        } else {
                            BigDecimal(it)
                        }
                    },
                    type = uiState.formState.type.value,
                    birthDate = uiState.formState.birthDate.value
                )
                ContactDatasource.instance.save(contactToSave)
                uiState.copy(
                    isSaving = false,
                    contactSaved = true
                )
            }
        }
    }

    private fun onFirstNameChanged(newValue: String) {
        if (uiState.formState.firstName.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                firstName = FormField(
                    value = newValue,
                    errorMessage = validateFirstName(newValue)

                )
            )
        )
    }

    private fun validateFirstName(value: String): String =
        if (value.isBlank()) {
            "Campo é obrigatório"
        } else {
            ""
        }

    private fun onLastNameChanged(newValue: String) {
        if (uiState.formState.lastName.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                lastName = FormField(newValue)
            )
        )
    }

    private fun onPhoneNumberChanged(newValue: String) {
        if (uiState.formState.phoneNumber.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                phoneNumber = FormField(
                    value = newValue,
                    errorMessage = validatePhoneNumber(newValue)
                )
            )
        )
    }

    private fun validatePhoneNumber(value: String): String =
         if (value.isNotBlank()
             || (value.length in 10..11)
            && value.contains(Regex("\\D"))
        ) {
            ""

        } else {
            "informe um numero valido (apenas digitod)"
        }

    private fun onEmailChanged(newValue: String) {
        if (uiState.formState.email.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                email = FormField(
                    value = newValue,
                    errorMessage = validateEmail(newValue)
                )
            )
        )
    }

    private fun validateEmail(value: String): String =
        if (value.isNotBlank()
            && !Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matches(value)
        ) {
            "Informe um e-mail válido"
        } else {
            ""
        }

    private fun onIsFavoriteChanged(newValue: Boolean) {
        if (uiState.formState.isFavorite.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                isFavorite = FormField(newValue)
            )
        )

    }
    private fun onAssetsValueChanged(newValue: String) {
        if (uiState.formState.assetsValue.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                assetsValue = FormField(
                    value = newValue,
                    errorMessage = validateAssetsValue(newValue)
                )
            )
        )
    }
    private fun validateAssetsValue(value: String): String{
        if (value.isBlank()) return ""
        try {
            BigDecimal(value)
            return ""
        } catch (e: NumberFormatException) {
           return "Informe um valor válido"
        }
    }
    private fun onBirthDateChanged(newValue: LocalDate) {
        if (uiState.formState.birthDate.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                birthDate = FormField(newValue)
            )
        )
    }
    private fun onTypeChanged(newValue: ContactTypeEnum) {
        if (uiState.formState.type.value == newValue) return

        uiState = uiState.copy(
            formState = uiState.formState.copy(
                type = FormField(newValue)
            )
        )
    }
    private fun isValidForm(): Boolean {
        uiState = uiState.copy(
            formState = uiState.formState.copy(
                firstName = uiState.formState.firstName.copy(
                    errorMessage = validateFirstName(uiState.formState.firstName.value)
                ),
                phoneNumber = uiState.formState.phoneNumber.copy(
                    errorMessage = validatePhoneNumber(uiState.formState.phoneNumber.value)
            ),
            email = uiState.formState.email.copy(
                errorMessage = validateEmail(uiState.formState.email.value)
            ),
            assetsValue = uiState.formState.assetsValue.copy(
                errorMessage = validateAssetsValue(uiState.formState.assetsValue.value)
            )
        )
     )
        return uiState.formState.isValid
    }
}


