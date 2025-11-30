package com.example.dailychecktodo.com.example.dailychecktodo

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

// 這個類別定義了 ViewPager2 頁面切換時的淡出動畫
class FadeOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.alpha = 1 - abs(position)
    }
}