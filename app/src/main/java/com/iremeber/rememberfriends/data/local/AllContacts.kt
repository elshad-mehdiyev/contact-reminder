package com.iremeber.rememberfriends.data.local

import android.app.Application
import android.provider.ContactsContract
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import javax.inject.Inject

class AllContacts @Inject constructor(private val application: Application) {
    fun getPhoneContacts(): ArrayList<AllContactModel> {
        val contactsList = ArrayList<AllContactModel>()

        val selection = "${ContactsContract.RawContacts.ACCOUNT_TYPE} <> ?"
        val selectionArgs = arrayOf("org.telegram.messenger.android.account")

        val contactsCursor = application.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
            ),
            selection,
            selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val phoneNumber = getPhoneNumber(id) // Fetch phone number separately
                if (name != null) {
                    val firstLetter = name[0].toString()
                    contactsList.add(
                        AllContactModel(
                            id = id,
                            name = name,
                            phoneNumber = phoneNumber ?: "",
                            firstLetter = firstLetter
                        )
                    )
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    private fun getPhoneNumber(contactId: String): String? {
        val phoneCursor = application.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId),
            null
        )
        var phoneNumber: String? = null
        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            phoneNumber = phoneCursor.getString(phoneCursor.run { getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) })
            phoneCursor.close()
        }
        return phoneNumber
    }
}
