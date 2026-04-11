package br.edu.utfpr.appcontatos.data

import br.edu.utfpr.appcontatos.data.utils.generateContacts

// Certifique-se de que a função generateContacts() existe no seu projeto
// ou use uma lista vazia se for inicializar do zero.

class ContactDatasource private constructor() {
    companion object {
        val instance: ContactDatasource by lazy {
            ContactDatasource()
        }
    }

    private val contacts: MutableList<Contact> = mutableListOf()

    init {
       contacts.addAll(generateContacts())
    }

    fun findAll(): List<Contact> = contacts.toList()

    fun findById(id: Int): Contact? = contacts.firstOrNull { it.id == id }

    fun save(contact: Contact): Contact {
        // Se o id for maior que 0, tentamos atualizar
        return if (contact.id != null && contact.id > 0) {
            val index = contacts.indexOfFirst { it.id == contact.id }
            if (index != -1) {
                contacts[index] = contact
                contact
            } else {
                insertNewContact(contact)
            }
        } else {
            insertNewContact(contact)
        }
    }

    private fun insertNewContact(contact: Contact): Contact {
        // Pega o maior ID atual ou 0 se a lista estiver vazia
        val maxId = contacts.maxOfOrNull { it.id ?: 0 } ?: 0
        val newContact = contact.copy(id = maxId + 1)
        contacts.add(newContact)
        return newContact
    }

    fun delete(contact: Contact) {
        if (contact.id != null && contact.id > 0) {
            contacts.removeIf { it.id == contact.id }
        }
    }
}