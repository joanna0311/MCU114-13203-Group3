package com.example.dailychecktodo

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class FadeOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.alpha = 1 - Math.abs(position)
    }
}
