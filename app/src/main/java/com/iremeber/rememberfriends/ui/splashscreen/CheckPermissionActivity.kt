package com.iremeber.rememberfriends.ui.splashscreen

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.databinding.ActivityCheckPermissionBinding
import com.iremeber.rememberfriends.ui.main_activity.MainActivity
import com.iremeber.rememberfriends.utils.util.Constants
import com.iremeber.rememberfriends.utils.util.ContactsUtility
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class CheckPermissionActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityCheckPermissionBinding
    private lateinit var contactsUtility: ContactsUtility
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.check_permission_background_color)
        binding = ActivityCheckPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactsUtility = ContactsUtility(this)
        binding.allowPermission.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (contactsUtility.hasSmsPermission()) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_for_permission),
            Constants.REQUEST_CONTACT_PERMISSION_CODE,
            Manifest.permission.READ_CONTACTS,
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}