package com.example.fastfoodorderingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectedMainMeal: String? = null
    private var selectedSideDish: String? = null
    private var selectedDrink: String? = null

    // 處理從選擇 Activity 返回的結果
    private val selectionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val selection = data?.getStringExtra("SELECTION")
            val type = data?.getStringExtra("TYPE")

            when (type) {
                "MAIN_MEAL" -> {
                    selectedMainMeal = selection
                    binding.tvMainMealSelection.text = "主餐：$selection"
                }
                "SIDE_DISH" -> {
                    selectedSideDish = selection
                    binding.tvSideDishSelection.text = "副餐：$selection"
                }
                "DRINK" -> {
                    selectedDrink = selection
                    binding.tvDrinkSelection.text = "飲料：$selection"
                }
            }
        }
    }

    // 處理從 ConfirmActivity 返回的結果
    private val confirmLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 提交成功，顯示訂單並重置
            binding.tvOrderStatus.text = "訂單已提交：\n主餐：$selectedMainMeal\n副餐：$selectedSideDish\n飲料：$selectedDrink"
            resetSelections()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 啟動主餐選擇
        binding.btnGoToMainMeal.setOnClickListener {
            val intent = Intent(this, MainMealActivity::class.java)
            selectionLauncher.launch(intent)
        }

        // 啟動副餐選擇
        binding.btnGoToSideDish.setOnClickListener {
            val intent = Intent(this, SideDishesActivity::class.java)
            selectionLauncher.launch(intent)
        }

        // 啟動飲料選擇
        binding.btnGoToDrink.setOnClickListener {
            val intent = Intent(this, DrinkActivity::class.java)
            selectionLauncher.launch(intent)
        }

        // 檢查並前往確認頁面
        binding.btnGoToConfirm.setOnClickListener {
            if (selectedMainMeal == null || selectedSideDish == null || selectedDrink == null) {
                //  requirement: 如果任何一項沒點, 跳出 Toast
                Toast.makeText(this, "請選擇主餐、副餐、飲料", Toast.LENGTH_SHORT).show()
            } else {
                // requirement: 如果都有選，跳轉到確認頁面 (ConfirmActivity)
                val intent = Intent(this, ConfirmActivity::class.java).apply {
                    putExtra("MAIN_MEAL", selectedMainMeal)
                    putExtra("SIDE_DISH", selectedSideDish)
                    putExtra("DRINK", selectedDrink)
                }
                confirmLauncher.launch(intent)
            }
        }
    }

    private fun resetSelections() {
        selectedMainMeal = null
        selectedSideDish = null
        selectedDrink = null
        binding.tvMainMealSelection.text = "主餐：尚未選擇"
        binding.tvSideDishSelection.text = "副餐：尚未選擇"
        binding.tvDrinkSelection.text = "飲料：尚未選擇"
    }
}