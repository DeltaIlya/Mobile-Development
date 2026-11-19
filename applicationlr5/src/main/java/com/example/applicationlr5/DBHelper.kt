package com.example.applicationlr5

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "UserDB", null, 1)
{
    override fun onCreate(db: SQLiteDatabase?)
    {
        val queryCreate = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, password TEXT)"
        db?.execSQL(queryCreate)
        // Автоматически создаём учётную запись администратора при первом запуске
        val queryInsertAdmin = "INSERT INTO users (login, password) VALUES ('admin', '${"admin123".toMD5()}')"
        db?.execSQL(queryInsertAdmin)
    }

    // Полный сброс базы данных с пересозданием таблицы и учетки админа
    fun resetDatabase() {
        val db = this.writableDatabase
        onUpgrade(db, 1, 1)
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
        contentValues.put("password", password.toMD5())
        val result = db.insert("users", null, contentValues)
        return result != -1L
    }

    // Метод проверки существования логина
    fun checkLogin(login: String): Boolean
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE login = ?",
            arrayOf(login)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Метод сопоставления логина и пароля пользователя
    fun checkUserPassword(login: String, password: String): Boolean
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE login = ? AND password = ?",
            arrayOf(login, password.toMD5())
        )
        val isCorrect = cursor.count > 0
        cursor.close()
        return isCorrect
    }

    // Получить список всех пользователей, кроме администратора
    fun getAllUsersForDisplay(): MutableList<String>
    {
        val userList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT login FROM users WHERE login != 'admin'",
            null
        )
        var position = 1
        if (cursor.moveToFirst())
        {
            do
            {
                val login = cursor.getString(0)
                userList.add("$position: $login")
                position++
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    // 2. Метод ИСКЛЮЧИТЕЛЬНО для логики кода (сохраняет реальные ID из базы данных)
    fun getAllUsersRaw(): MutableList<String>
    {
        val userList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, login FROM users WHERE login != 'admin'",
            null
        )
        if (cursor.moveToFirst())
            {
                do
                {
                    val id = cursor.getInt(0)
                    val login = cursor.getString(1)
                    userList.add("$id:$login") // соединяем через двоеточие без пробелов
                }
                while (cursor.moveToNext())
            }
        cursor.close()
        return userList
    }

    // Получить список всех пользователей, кроме администратора
    fun getAllUsers(): MutableList<String>
    {
        val userList = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, login FROM users WHERE login != 'admin'",
            null)
        var position = 1 // Создаем счетчик для красивой нумерации на экране
        if (cursor.moveToFirst())
        {
            do
            {
                val id = cursor.getInt(0)
                val login = cursor.getString(1)
                // В список на экран добавляем красивый номер (position), а не системный id
                userList.add("$position: $login")
                position++ // Увеличиваем счетчик для следующего пользователя
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }


    // Удалить пользователя по его уникальному ID
    fun deleteUser(id: Int): Boolean
    {
        val db = this.writableDatabase
        val result = db.delete(
            "users", "id = ?",
            arrayOf(id.toString())
        )
        return result > 0
    }

    // Изменить логин и пароль пользователя
    fun updateUser(id: Int, new_login: String, new_password: String): Boolean
    {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("login", new_login)
        contentValues.put("password", new_password.toMD5())
        val result = db.update(
            "users", contentValues, "id = ?",
            arrayOf(id.toString())
        )
        return result > 0
    }
}
