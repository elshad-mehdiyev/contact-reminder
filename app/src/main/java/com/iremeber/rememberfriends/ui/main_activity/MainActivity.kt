package com.iremeber.rememberfriends.ui.main_activity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.iremeber.rememberfriends.R
import com.iremeber.rememberfriends.databinding.ActivityMainBinding
import com.iremeber.rememberfriends.di.HiltAndroidApp
import com.iremeber.rememberfriends.utils.alarmmanager.AlarmManagerImpl
import com.iremeber.rememberfriends.utils.alarmmanager.BootCompletedReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.save()
        val exactAlarmManagerImpl = AlarmManagerImpl(this)
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
        (applicationContext as HiltAndroidApp).apply {
            alarmRingtoneState.value?.stop()
            alarmRingtoneState.value = null
        }
    }

}