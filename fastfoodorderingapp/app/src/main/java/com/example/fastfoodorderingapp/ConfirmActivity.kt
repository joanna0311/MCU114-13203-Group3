package com.example.fastfoodorderingapp

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityConfirmBinding

class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 從 Intent 獲取餐點選擇
        val mainMeal = intent.getStringExtra("MAIN_MEAL")
        val sideDish = intent.getStringExtra("SIDE_DISH")
        val drink = intent.getStringExtra("DRINK")

        // 顯示餐點
        binding.tvConfirmMain.text = "主餐：$mainMeal"
        binding.tvConfirmSide.text = "副餐：$sideDish"
        binding.tvConfirmDrink.text = "飲料：$drink"

        // 點下「確認 Confirm Button」
        binding.btnConfirm.setOnClickListener {
            // requirement: 跳出 AlertDialog 裡面有所選項目和 Submit (提交) Button
            val orderDetails = "主餐：$mainMeal\n副餐：$sideDish\n飲料：$drink"

            AlertDialog.Builder(this)
                .setTitle("最終確認")
                .setMessage(orderDetails)
                .setPositiveButton("提交 (Submit)") { dialog, which ->
                    // requirement: 提交後跳回主畫面並顯示所選餐點
                    // 我們透過 setResult(RESULT_OK) 通知 MainActivity 提交成功
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}