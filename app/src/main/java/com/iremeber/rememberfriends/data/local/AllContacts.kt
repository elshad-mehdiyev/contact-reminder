package com.iremeber.rememberfriends.data.local

import android.app.Application
import android.provider.ContactsContract
import com.iremeber.rememberfriends.data.models.db_entities.AllContactModel
import javax.inject.Inject

class AllContacts @Inject constructor(private val application: Application) {
    fun getPhoneContacts(): ArrayList<AllContactModel> {
        val contactsList = ArrayList<AllContactModel>()
        val contactsCursor = application.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    val firstLetter = name[0].toString()
                    contactsList.add(AllContactModel(id = id, name = name, firstLetter = firstLetter))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }
}