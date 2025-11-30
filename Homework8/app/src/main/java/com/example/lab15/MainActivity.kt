package com.example.lab15

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var items: ArrayList<String> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var db: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 取得資料庫實體
        db = MyDBHelper(this)
        // 宣告Adapter並連結ListView
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.listView).adapter = adapter
        // 設定監聽器
        setListener()
        // 查詢所有資料
        query()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close() // 關閉資料庫
    }

    // 設定監聽器
    private fun setListener() {
        val edBrand = findViewById<EditText>(R.id.edBrand)
        val edYear = findViewById<EditText>(R.id.edYear)
        val edPrice = findViewById<EditText>(R.id.edPrice)

        findViewById<Button>(R.id.btnInsert).setOnClickListener {
            // 判斷是否有填入資料
            if (edBrand.length() < 1 || edYear.length() < 1 || edPrice.length() < 1) {
                showToast("欄位請勿留空")
                return@setOnClickListener
            }

            try {
                val car = Car(0, edBrand.text.toString(), edYear.text.toString().toInt(), edPrice.text.toString().toInt())
                val id = db.addCar(car)
                showToast("新增成功，ID為$id")
                cleanEditText()
                query()
            } catch (e: Exception) {
                showToast("新增失敗:$e")
            }
        }

        findViewById<Button>(R.id.btnUpdate).setOnClickListener {
            // 判斷是否有填入資料
            if (edBrand.length() < 1 || edYear.length() < 1 || edPrice.length() < 1) {
                showToast("欄位請勿留空")
                return@setOnClickListener
            }

            db.readableDatabase.rawQuery("SELECT * FROM cars WHERE brand LIKE '${edBrand.text}'", null).use { c ->
                if (c.moveToFirst()) {
                    try {
                        val id = c.getInt(0)
                        val count = db.updateCar(id, edBrand.text.toString(), edYear.text.toString().toInt(), edPrice.text.toString().toInt())
                        showToast("更新成功，影響 $count 筆資料")
                        cleanEditText()
                        query()
                    } catch (e: Exception) {
                        showToast("更新失敗:$e")
                    }
                } else {
                    showToast("找不到符合條件的車輛")
                }
            }
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            // 判斷是否有填入廠牌
            if (edBrand.length() < 1) {
                showToast("廠牌請勿留空")
                return@setOnClickListener
            }

            db.readableDatabase.rawQuery("SELECT * FROM cars WHERE brand LIKE '${edBrand.text}'", null).use { c ->
                 if (c.moveToFirst()) {
                    try {
                        val id = c.getInt(0)
                        val count = db.deleteCar(id)
                        showToast("刪除成功，影響 $count 筆資料")
                        cleanEditText()
                        query()
                    } catch (e: Exception) {
                        showToast("刪除失敗:$e")
                    }
                } else {
                    showToast("找不到符合條件的車輛")
                }
            }
        }

        findViewById<Button>(R.id.btnQuery).setOnClickListener {
            query()
        }
    }

    private fun query() {
        val edBrand = findViewById<EditText>(R.id.edBrand)
        val queryString = if (edBrand.length() < 1)
            "SELECT * FROM cars"
        else
            "SELECT * FROM cars WHERE brand LIKE '${edBrand.text}'"

        items.clear() //清空舊資料
        db.readableDatabase.rawQuery(queryString, null).use { c ->
            if (c.count == 0) {
                 showToast("沒有資料")
            } else {
                showToast("共有${c.count}筆資料")
                if (c.moveToFirst()) {
                    do {
                        // 加入新資料
                        items.add("廠牌:${c.getString(1)}\t\t年份:${c.getInt(2)}\t\t價格:${c.getInt(3)}")
                    } while (c.moveToNext())
                }
            }
        }
        adapter.notifyDataSetChanged() // 更新列表資料
    }

    // 建立showToast方法顯示Toast訊息
    private fun showToast(text: String) =
        Toast.makeText(this,text, Toast.LENGTH_LONG).show()

    // 清空輸入的廠牌、年份與價格
    private fun cleanEditText() {
        findViewById<EditText>(R.id.edBrand).setText("")
        findViewById<EditText>(R.id.edYear).setText("")
        findViewById<EditText>(R.id.edPrice).setText("")
    }
}