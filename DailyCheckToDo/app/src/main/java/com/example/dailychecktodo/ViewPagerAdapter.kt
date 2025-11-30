package com.example.dailychecktodo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dailychecktodo.ContactsFragment
import com.example.dailychecktodo.HomeFragment
import com.example.dailychecktodo.SettingsFragment

// 這個 Adapter 負責管理 ViewPager2 中的所有 Fragment
class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // 告訴 ViewPager2 總共有幾個頁面
    override fun getItemCount(): Int = 3

    // 根據位置 (position) 回傳對應的 Fragment
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()       // 第 0 個位置是主畫面
            1 -> ContactsFragment()   // 第 1 個位置是聯絡人
            2 -> SettingsFragment()   // 第 2 個位置是設定
            else -> throw IllegalStateException("無效的位置 $position")
        }
    }
}