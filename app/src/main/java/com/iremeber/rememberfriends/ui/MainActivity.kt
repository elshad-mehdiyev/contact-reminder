package com.iremeber.rememberfriends.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.databinding.ActivityMainBinding
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.alarmmanager.BootCompletedReceiver
import com.iremeber.rememberfriends.utils.util.Constants.REQUEST_CONTACT_PERMISSION_CODE
import com.iremeber.rememberfriends.utils.util.ContactsUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    lateinit var binding: ActivityMainBinding
    lateinit var contactsUtility: ContactsUtility
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactsUtility = ContactsUtility(this)
        val exactAlarmManagerImpl = AlarmManagerImpl(this)
        requestPermissions()
        setUpNavController()

        if (!exactAlarmManagerImpl.canScheduleExactAlarms()) {
            openSettings()
        }
        startBootCompletedReceiver()
    }
    private fun setUpNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun openSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
    }

    private fun requestPermissions() {
        if(contactsUtility.hasSmsPermission()) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.rationale_for_permission),
            REQUEST_CONTACT_PERMISSION_CODE,
            Manifest.permission.READ_CONTACTS,
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
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
    private fun startBootCompletedReceiver() {
        val receiver = ComponentName(applicationContext, BootCompletedReceiver::class.java)

        applicationContext.packageManager?.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }
}