package com.iremeber.rememberfriends.utils.util

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

class ContactsUtility(val context: Context) {
    fun hasSmsPermission() = EasyPermissions.hasPermissions(
        context,
        Manifest.permission.READ_CONTACTS,
    )
}