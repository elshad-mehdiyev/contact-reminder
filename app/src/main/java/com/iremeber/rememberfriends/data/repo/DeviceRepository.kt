package com.iremeber.rememberfriends.data.repo

import com.iremeber.rememberfriends.data.local.AllContacts
import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import javax.inject.Inject

class DeviceRepository @Inject constructor(
    private val fetchContact: AllContacts
    )  {
    fun getContactFromDevice(): ArrayList<AllContactModel> {
        return fetchContact.getPhoneContacts()
    }

}