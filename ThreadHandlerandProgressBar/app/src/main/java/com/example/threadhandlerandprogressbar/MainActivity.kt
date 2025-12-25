package com.example.threadhandlerandprogressbar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Start"
                1 -> "Progress"
                2 -> "Result"
                else -> null
            }
        }.attach()

        // 為了確保所有 Fragment 都被即時建立
        viewPager.offscreenPageLimit = 2

        // 禁止手動滑動 ViewPager，只能透過 Tab 點擊切換
        viewPager.isUserInputEnabled = false
    }
}