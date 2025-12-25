package com.example.dailychecktodo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailychecktodo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), LoginFragment.OnLoginSuccessListener {

    private lateinit var binding: ActivityMainBinding
    private val habitViewModel: HabitViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // We can handle the result here if needed, e.g., show a toast
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_auth", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            val currentUserEmail = sharedPreferences.getString("current_user_email", null)
            if (currentUserEmail != null) {
                // If already logged in, directly initialize and show main content
                onLoginSuccess(currentUserEmail)
            } else {
                showLoginScreen()
            }
        } else {
            showLoginScreen()
        }
    }

    override fun onLoginSuccess(email: String) {
        // The formal "handover ceremony"
        // 1. Initialize the ViewModel for the current user.
        habitViewModel.initForUser(applicationContext, email)
        // 2. Show the main content of the app.
        showMainContent()
    }

    private fun showLoginScreen() {
        binding.tabLayout.visibility = View.GONE
        binding.viewPager.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, LoginFragment())
            .commit()
    }

    private fun showMainContent() {
        val loginFragment = supportFragmentManager.findFragmentById(R.id.main_container)
        if (loginFragment != null && loginFragment is LoginFragment) {
            supportFragmentManager.beginTransaction().remove(loginFragment).commitNow()
        }

        binding.tabLayout.visibility = View.VISIBLE
        binding.viewPager.visibility = View.VISIBLE

        requestNotificationPermission()
        scheduleDailyReset()
        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() {
        if (binding.viewPager.adapter != null) return

        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.viewPager.setPageTransformer(FadeOutPageTransformer())

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
