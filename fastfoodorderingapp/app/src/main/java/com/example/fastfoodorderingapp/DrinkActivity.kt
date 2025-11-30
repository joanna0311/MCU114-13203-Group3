package com.example.fastfoodorderingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivityDrinkBinding

class DrinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectDrink.setOnClickListener {
            val selectedId = binding.rgDrink.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "請選擇一個飲料", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val selection = selectedRadioButton.text.toString()

                val resultIntent = Intent().apply {
                    putExtra("SELECTION", selection)
                    putExtra("TYPE", "DRINK") // 標記返回類型
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}