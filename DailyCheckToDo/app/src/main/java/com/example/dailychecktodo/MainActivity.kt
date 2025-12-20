package com.example.dailychecktodo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailychecktodo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // We can handle the result here if needed, e.g., show a toast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()
        scheduleDailyReset()

        // 設定 ViewPager2 Adapter
        binding.viewPager.adapter = ViewPagerAdapter(this)

        // 設定淡入淡出動畫 (可選)
        binding.viewPager.setPageTransformer(FadeOutPageTransformer())

        // 將 TabLayout 和 ViewPager2 連結
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "主畫面"
                1 -> "聯絡人"
                2 -> "設定"
                else -> null
            }
            tab.icon = when (position) {
                0 -> AppCompatResources.getDrawable(this, R.drawable.ic_home)
                1 -> AppCompatResources.getDrawable(this, R.drawable.ic_contacts)
                2 -> AppCompatResources.getDrawable(this, R.drawable.ic_settings)
                else -> null
            }
        }.attach()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun scheduleDailyReset() {
        val workManager = WorkManager.getInstance(applicationContext)

        val dailyResetWorkRequest = PeriodicWorkRequestBuilder<ResetHabitsWorker>(1, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_habit_reset",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyResetWorkRequest
        )
    }
}
