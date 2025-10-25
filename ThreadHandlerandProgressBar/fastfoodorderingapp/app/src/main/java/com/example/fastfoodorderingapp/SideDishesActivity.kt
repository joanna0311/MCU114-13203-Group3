package com.example.fastfoodorderingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fastfoodorderingapp.databinding.ActivitySideDishesBinding

class SideDishesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySideDishesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySideDishesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectSideDish.setOnClickListener {
            val selectedId = binding.rgSideDish.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "請選擇一個副餐", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = findViewById<RadioButton>(selectedId)
                val selection = selectedRadioButton.text.toString()

                val resultIntent = Intent().apply {
                    putExtra("SELECTION", selection)
                    putExtra("TYPE", "SIDE_DISH") // 標記返回類型
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}