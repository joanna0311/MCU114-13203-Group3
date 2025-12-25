package com.example.lab15

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(
    context: Context,
    name: String = DB_NAME,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = VERSION
) : SQLiteOpenHelper(context, name, factory, version) {
    companion object {
        private const val DB_NAME = "myDatabase" // 資料庫名稱
        private const val VERSION = 2 // <--- 版本已更新
        private const val TABLE_NAME = "cars" // 資料表名稱
        private const val COL_ID = "id" // id 欄位
        private const val COL_BRAND = "brand" // 廠牌欄位
        private const val COL_YEAR = "year" // 年份欄位
        private const val COL_PRICE = "price" // 價格欄位
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_BRAND TEXT NOT NULL, $COL_YEAR INTEGER NOT NULL, $COL_PRICE INTEGER NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 升級資料庫版本時，刪除舊資料表，並重新執行 onCreate()，建立新資料表
        db.execSQL("DROP TABLE IF EXISTS myTable")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addCar(car: Car): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_BRAND, car.brand)
            put(COL_YEAR, car.year)
            put(COL_PRICE, car.price)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun deleteCar(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))
    }

    fun updateCar(id: Int, brand: String, year: Int, price: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_BRAND, brand)
            put(COL_YEAR, year)
            put(COL_PRICE, price)
        }
        return db.update(TABLE_NAME, values, "$COL_ID=?", arrayOf(id.toString()))
    }
}