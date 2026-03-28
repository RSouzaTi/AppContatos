package br.edu.utfpr.appcontatos.ui.contact

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonDefaults.colors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.appcontatos.R
import br.edu.utfpr.appcontatos.data.Contact
import br.edu.utfpr.appcontatos.ui.theme.AppContatosTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ContactsListScreen(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val isInitialCompositionState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(true)
    }
    val isLoadingState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)

    }
    var isErrorState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }
    var contactsState: MutableState<List<Contact>> = rememberSaveable {
        mutableStateOf(emptyList())
    }

    val loadingContacts: () -> Unit = {
        isLoadingState.value = true
        isErrorState.value = false

        coroutineScope.launch {
            delay(2000)
            isErrorState.value = Random.nextBoolean()
            if (!isErrorState.value) {
                val isEmpty = Random.nextBoolean()
                if (!isEmpty) {
                    contactsState.value = listOf()
                } else {
                    contactsState.value = generateContacts()
                }
            }
            isLoadingState.value = false

        }
    }
    if (isInitialCompositionState.value) {
        loadingContacts()
        isInitialCompositionState.value = false
    }
    val contentModifier = modifier.fillMaxSize()
    if (isLoadingState.value) {
        LoadingState(modifier = contentModifier)
    } else if (isErrorState.value) {
        ErrorState(
            modifier = contentModifier,
            omTryAgainPresses = loadingContacts
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                AppBar(
                onRefreshPressed = loadingContacts
            )

           },
            floatingActionButton = {
                ExtendedFloatingActionButton(onClick = {
                    contactsState.value = contactsState.value.plus(
                        Contact(firstName = "Teste", lastName = "Teste")
                        )
                     }
                )
             {
                  Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Novo contato"
                    )
                 Spacer(Modifier.size(8.dp))
                 Text("Novo contato")
                }
            }


        ) { paddingValues ->
            val defaultModifier: Modifier = Modifier.padding(paddingValues)
           if (contactsState.value.isEmpty()) {
                EmptyList(modifier = defaultModifier)
            } else {
                List(
                    modifier = defaultModifier,
                    contacts = contactsState.value
                )
            }
        }
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(
        modifier: Modifier = Modifier,
        onRefreshPressed: () -> Unit
    ) {
        TopAppBar(
            modifier = modifier.fillMaxWidth(),
            title = {
                Text("Contatos")
            },
           colors = TopAppBarDefaults.topAppBarColors(
               titleContentColor = MaterialTheme.colorScheme.primary,
               actionIconContentColor = MaterialTheme.colorScheme.primary
           ),
            actions = {
                IconButton(onClick = onRefreshPressed) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Atualizar"
                    )
                }
            }
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun AppBarPreview() {
        AppContatosTheme {
            AppBar(
                onRefreshPressed = {}
            )

        }
    }

    @Composable
    fun LoadingState(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Carregando contatos...",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

    }

    @Preview(showBackground = true, heightDp = 400)
    @Composable
    fun LoadingStatePreview() {
        AppContatosTheme {
            LoadingState()
        }
    }

    @Composable
    fun ErrorState(
        modifier: Modifier = Modifier,
        omTryAgainPresses: () -> Unit
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CloudOff,
                contentDescription = "Erro ao carregar",
                tint = MaterialTheme.colorScheme.primary,
                modifier = modifier.size(80.dp)
            )
            val textPadding = PaddingValues(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp,
            )
            Text(
                modifier = modifier.padding(textPadding),
                text = "Ocorreu um erro ao carregar",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                modifier = modifier.padding(textPadding),
                text = "Aguarde um momento e tente novamente",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            ElevatedButton(
                modifier = modifier.padding(top = 16.dp),
                onClick = omTryAgainPresses
            ) {
                Text("Tentar novamente")
            }
        }
    }

    @Preview(showBackground = true, heightDp = 400)
    @Composable
    fun ErrorStatePreview() {
        AppContatosTheme {
            ErrorState(
                omTryAgainPresses = {}
            )
        }
    }

    @Composable
    fun EmptyList(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.no_data),
                contentDescription = "Nenhumpor aqui",
            )
            Text(
                text = "Nenhum por aqui",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(top = 16.dp)

            )
            Text(
                text = "Você ainda não adicionou nenhum contato." +
                        "\nAdicione um contato clicando no botão \"Novo contato\"",
                modifier = modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,

                )
        }
    }

    @Preview(showBackground = true, heightDp = 400)
    @Composable
    fun EmptyListPreview() {
        AppContatosTheme {
            EmptyList()
        }

    }

    @Composable
    fun List(
        modifier: Modifier = Modifier,
        contacts: List<Contact> = emptyList()

    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            items(contacts) { contact ->
                ContactListItem(contact = contact)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ListPreview() {
        AppContatosTheme {
            List(
                contacts = generateContacts()

            )
        }
    }

    @Composable
    fun ContactListItem(
        modifier: Modifier = Modifier,
        contact: Contact
    ) {
        val isFavoriteState: MutableState<Boolean> = rememberSaveable {
            mutableStateOf(contact.isFavorite)
        }

        ListItem(
            modifier = modifier,
            headlineContent = {
                Text(contact.fullName)
            },
            trailingContent = {
                IconButton(
                    onClick = {
                        isFavoriteState.value = !isFavoriteState.value
                    }
                ) {
                    Icon(
                        imageVector = if (isFavoriteState.value) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "Favoritar",
                        tint = if (isFavoriteState.value) {
                            Color.Red
                        } else {
                            LocalContentColor.current
                        }
                    )
                }
            }
        )
    }

    fun generateContacts(): List<Contact> {
        val firstNames = listOf(
            "João", "José", "Everton", "Marcos", "André", "Anderson", "Antônio",
            "Laura", "Ana", "Maria", "Joaquina", "Suelen"
        )
        val lastNames = listOf(
            "Do Carmo", "Oliveira", "Dos Santos", "Da Silva", "Brasil", "Pichetti",
            "Cordeiro", "Silveira", "Andrades", "Cardoso"
        )
        val contacts: MutableList<Contact> = mutableListOf()
        for (i in 0..29) {
            var generatedNewContact = false
            while (!generatedNewContact) {
                val firstNameIndex = Random.nextInt(firstNames.size)
                val lastNameIndex = Random.nextInt(lastNames.size)
                val newContact = Contact(
                    id = i + 1,
                    firstName = firstNames[firstNameIndex],
                    lastName = lastNames[lastNameIndex]
                )
                if (!contacts.any { it.fullName == newContact.fullName }) {
                    contacts.add(newContact)
                    generatedNewContact = true
                }
            }
        }
        return contacts
    }
