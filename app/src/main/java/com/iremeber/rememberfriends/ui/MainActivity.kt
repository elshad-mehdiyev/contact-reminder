package com.iremeber.rememberfriends.ui

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
import com.iremeber.rememberfriends.utils.alarmmanager.ExactAlarmBroadCastReceiver
import com.iremeber.rememberfriends.utils.alarmmanager.TimeChangedReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val exactAlarmManagerImpl = AlarmManagerImpl(this)
        setUpNavController()

        if (!exactAlarmManagerImpl.canScheduleExactAlarms()) {
            openSettings()
        }
        startBootCompletedReceiver(ComponentName(applicationContext, BootCompletedReceiver::class.java))
        startBootCompletedReceiver(ComponentName(applicationContext, ExactAlarmBroadCastReceiver::class.java))
        startBootCompletedReceiver(ComponentName(applicationContext, TimeChangedReceiver::class.java))
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
    private fun startBootCompletedReceiver(receiver: ComponentName) {
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