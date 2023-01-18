package com.iremeber.rememberfriends.domain

import com.iremeber.rememberfriends.data.models.device_entities.AllContactModel
import com.iremeber.rememberfriends.data.repo.DeviceRepository
import javax.inject.Inject

class GetContactFromDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    operator fun invoke(): ArrayList<AllContactModel> {
        return deviceRepository.getContactFromDevice()
    }
}