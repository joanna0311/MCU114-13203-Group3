package com.example.majorcitytemp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // 1. 資料類別定義
    data class WeatherResponse(val main: MainData, val name: String)
    data class MainData(val temp: Double, val humidity: Int)

    // 2. 建立台灣城市列表
    private val cityMap = mapOf(
        "新北市" to "New Taipei City",
        "桃園市" to "Taoyuan",
        "新竹市" to "Hsinchu City",
        "苗栗縣" to "Miaoli",
        "台中市" to "Taichung",
        "彰化縣" to "Changhua",
        "南投縣" to "Nantou",
        "雲林縣" to "Yunlin",
        "嘉義市" to "Chiayi City",
        "台南市" to "Tainan",
        "高雄市" to "Kaohsiung",
        "屏東縣" to "Pingtung",
        "宜蘭縣" to "Yilan",
        "花蓮縣" to "Hualien",
        "台東縣" to "Taitung",
        "基隆市" to "Keelung",
        "澎湖縣" to "Penghu",
        "金門縣" to "Kinmen",
        "連江縣" to "Lienchiang" // 注意: OpenWeatherMap 可能找不到連江 (南竿) 的精確資料
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 呼叫函式來動態建立所有城市按鈕
        setupCityButtons()
    }

    // 動態建立所有城市按鈕的函式
    private fun setupCityButtons() {
        val layout = findViewById<LinearLayout>(R.id.cityButtonsLayout)

        // 遍歷城市地圖，為每個城市創建一個按鈕
        for ((displayName, apiName) in cityMap) {
            val button = Button(this).apply {
                text = displayName // 按鈕上顯示中文城市名
                // 這裡我們直接設定 LayoutParams 並在其中設定邊距
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // 寬度填滿
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 16, 0, 16) // 設定按鈕之間的垂直間距
                layoutParams = params

                // 設定點擊事件監聽器
                setOnClickListener {
                    // 點擊按鈕時，使用對應的英文名稱查詢天氣
                    fetchWeather(apiName)
                }
            }
            layout.addView(button) // 將建立好的按鈕加入到 LinearLayout 中
        }
    }

    // 修改 fetchWeather 函式，使其可以接收一個 `city` 名稱參數
    // 修改 fetchWeather 函式，使其可以接收一個 `city` 名稱參數
    private fun fetchWeather(city: String) {
        // *** 重要：請務必換成你自己的 OpenWeatherMap API Key ***
        val apiKey = "ad32bda3bc8edd7e256ded3548175da0"
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"

        val client = OkHttpClient()

        // 網路操作必須在背景執行緒 (Dispatchers.IO) 上運行
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        val gson = Gson()
                        val weatherData = gson.fromJson(responseData, WeatherResponse::class.java)

                        // 切換回主執行緒以更新 UI (顯示對話框)
                        withContext(Dispatchers.Main) {
                            showAlertDialog(
                                "目前天氣", // [修改] 標題改為中文
                                "城市: ${weatherData.name}\n" +
                                        "溫度: ${weatherData.main.temp}°C\n" +
                                        "濕度: ${weatherData.main.humidity}%"
                            )
                        }
                    }
                } else {
                    // 如果 API 回應失敗 (例如 404 找不到城市)，顯示中文錯誤訊息
                    withContext(Dispatchers.Main) {
                        showAlertDialog("查詢錯誤", "無法取得資料。\n城市: $city\n回應碼: ${response.code}")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // 顯示中文網路錯誤訊息
                withContext(Dispatchers.Main) {
                    showAlertDialog("網路錯誤", "無法連線，請檢查網路狀態。\n錯誤訊息: ${e.message}")
                }
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("確定") { dialog, _ -> // [修改] 按鈕文字改為中文
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

