package com.example.applicationlr5

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1)
{
    override fun onCreate(db: SQLiteDatabase?)
    {
        val query = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, password TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // Метод добавления пользователя
    fun insertData(login: String, password: String): Boolean
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("login", login)
        contentValues.put("password", password)
        val result = db.insert("users", null, contentValues)
        return result != -1L
    }

    // Метод проверки существования логина
    fun checkLogin(login: String): Boolean
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE login = ?", arrayOf(login))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Метод сопастовления логина и пароля пользователя
    fun checkUserPassword(login: String, password: String): Boolean
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE login = ? AND password = ?",
            arrayOf(login, password)
        )
        val isCorrect = cursor.count > 0
        cursor.close()
        return isCorrect
    }
}
