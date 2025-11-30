package com.example.dailychecktodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.dailychecktodo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
