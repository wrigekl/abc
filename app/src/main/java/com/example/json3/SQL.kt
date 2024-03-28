package com.example.json3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import java.nio.charset.Charset

class SQL(private val  context: Context) : SQLiteOpenHelper(context ,  "SQL.db" , null , 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE c(" +
                "_country TEXT," +
                "_act INTEGER," +
                "_x REAL," +
                "_y REAL)")
        init(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS s")
        onCreate(db)
    }
    fun Act(country : String) {
        val db = writableDatabase
        val ContentValues = ContentValues().apply {
            put("_act" , 1)
        }
        db.update("c" , ContentValues , "_country = ?" , arrayOf(country))
    }
    fun UnAct(country: String) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("_act" , 0)
        }
        db.update("c" , contentValues , "_country = ?" , arrayOf(country))
    }
    fun readAct() : MutableList<String> {
        val db = writableDatabase
        val cursor = db.query("c" , arrayOf("_country") , "_act = ?" , arrayOf("1") , null , null , null)
        var matched : MutableList<String> = mutableListOf()
        if(cursor.moveToFirst()) {
            do {
                matched  += cursor.getString(cursor.getColumnIndex("_country"))
            } while (cursor.moveToNext())
        }
        return matched
    }
    fun readUnAcut() : MutableList<String> {
        val db = writableDatabase
        val cursor = db.query("c" , arrayOf("_country") , "_act = ?" , arrayOf("0") ,null , null ,null)
        var matched : MutableList<String> = mutableListOf()
        if(cursor.moveToFirst()) {
            do {
                matched += cursor.getString(cursor.getColumnIndex("_country"))
            } while (cursor.moveToNext())
        }
        return matched
    }
    fun init(db: SQLiteDatabase?) {
        val json = Json("temp.json")
        val temp = Gson().fromJson(json , country :: class.java)
        val X = temp.countries
        val b = temp.data
        for (i in X.indices) {
            val values = ContentValues().apply {
                put("_x" , b[i].latitude)
                put("_y" , b[i].longitude)
                put("_country" , X[i])
                put("_act" , 0)
            }
            db?.insert("c" , null , values)
        }
    }
    fun Json(file:String) : String? {
        val inputStream = context.assets.open(file)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer , Charset.defaultCharset())
    }
    fun readX(country: String) :Double {
        val db = writableDatabase
        val cursor = db.query("c" , arrayOf("_x") , "_country = ? " , arrayOf(country) , null , null ,null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("_x")).toDouble()
    }
    fun readY(country: String) :Double {
        val db = writableDatabase
        val cursor = db.query("c" , arrayOf("_y") , "_country = ? " , arrayOf(country) , null , null ,null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndex("_y")).toDouble()
    }
}