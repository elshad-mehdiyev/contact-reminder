package com.iremeber.rememberfriends.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iremeber.rememberfriends.ui.main_activity.MainActivity
import com.iremeber.rememberfriends.utils.util.ContactsUtility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var contactsUtility: ContactsUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactsUtility = ContactsUtility(this)
        checkPermission()
    }
    private fun checkPermission() {
        if (contactsUtility.hasSmsPermission()) {
            Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 500)
        } else {
            startActivity(Intent(this, CheckPermissionActivity::class.java))
            finish()
        }
    }
}