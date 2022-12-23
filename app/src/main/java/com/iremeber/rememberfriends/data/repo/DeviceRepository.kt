package com.iremeber.rememberfriends.data.repo

import com.iremeber.rememberfriends.data.local.AllContacts
import com.iremeber.rememberfriends.data.local.AllRingtones
import com.iremeber.rememberfriends.data.models.AllContactModel
import com.iremeber.rememberfriends.data.models.AllRingtonesModel
import javax.inject.Inject

class DeviceRepository @Inject constructor(
    private val fetchContact: AllContacts,
    private val fetchRingtones: AllRingtones
    )  {
    fun getContactFromDevice(): ArrayList<AllContactModel> {
        return fetchContact.getPhoneContacts()
    }

    fun getAllRingtonesFromDevice(): ArrayList<AllRingtonesModel> {
        return fetchRingtones.getRingtonesList()
    }
}