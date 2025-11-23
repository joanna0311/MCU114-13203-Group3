package com.example.threadhandlerandprogressbar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StartFragment()
            1 -> ProgressFragment()
            2 -> ResultFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}