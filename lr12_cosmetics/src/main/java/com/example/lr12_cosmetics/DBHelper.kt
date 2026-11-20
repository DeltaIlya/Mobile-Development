package com.example.lr12_cosmetics

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "CosmeticsDB", null, 1)
{
    private val appContext: Context = context.applicationContext
    override fun onCreate(db: SQLiteDatabase?)
    {
        // Таблица пользователей
        db?.execSQL(
            "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, password TEXT)"
        )
        // Системный администратор
        db?.execSQL(
            "INSERT INTO users (login, password) VALUES ('admin', '${"admin123".toMD5()}')"
        )

        // Таблица товаров
        db?.execSQL(
            """
            CREATE TABLE items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT,
                full_text TEXT,
                price INTEGER,
                image_uri TEXT,
                shelf_life_days INTEGER,
                created_at INTEGER
            )
            """.trimIndent()
        )

        if (db != null)
            seedItems(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
    {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS items")
        onCreate(db)
    }

    // Заполняет таблицу items товарами из ресурсов (data_strings.xml)
    private fun seedItems(db: SQLiteDatabase)
    {
        val now = System.currentTimeMillis()
        val dayMillis = 24L * 60 * 60 * 1000

        val images = appContext.resources.getStringArray(R.array.item_images)
        val titles = appContext.resources.getStringArray(R.array.item_titles)
        val descs = appContext.resources.getStringArray(R.array.item_descs)
        val texts = appContext.resources.getStringArray(R.array.item_texts)
        val prices = appContext.resources.getIntArray(R.array.item_prices)
        val shelfLives = appContext.resources.getIntArray(R.array.item_shelf_life)
        val offsets = appContext.resources.getIntArray(R.array.item_created_offset_days)

        // Проверка: все массивы должны быть одинаковой длины
        val count = images.size
        if (titles.size != count || descs.size != count || texts.size != count ||
            prices.size != count || shelfLives.size != count || offsets.size != count)
        {
            throw IllegalStateException("Массивы в data_strings.xml имеют разную длину!")
        }

        // Собираем товары по индексу
        for (i in 0 until count)
        {
            val item = Item(
                id = 0,
                imageUri = images[i],
                title = titles[i],
                desc = descs[i],
                text = texts[i],
                price = prices[i],
                createdAtMillis = now + offsets[i] * dayMillis,
                shelfLifeDays = shelfLives[i]
            )

            val cv = ContentValues().apply()
            {
                put("title", item.title)
                put("description", item.desc)
                put("full_text", item.text)
                put("price", item.price)
                put("image_uri", item.imageUri)
                put("shelf_life_days", item.shelfLifeDays)
                put("created_at", item.createdAtMillis)
            }
            db.insert("items", null, cv)
        }
    }

    // ==================== МЕТОДЫ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ ====================

    fun insertUser(login: String, password: String): Boolean
    {
        val db = this.writableDatabase
        val cv = ContentValues().apply()
        {
            put("login", login)
            put("password", password.toMD5())
        }
        return db.insert("users", null, cv) != -1L
    }

    fun checkLogin(login: String): Boolean
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE login = ?",
            arrayOf(login)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

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

    fun getAllUsersForDisplay(): MutableList<String>
    {
        val list = mutableListOf<String>()
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
                list.add("$position: ${cursor.getString(0)}")
                position++
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getAllUsersRaw(): MutableList<String>
    {
        val list = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, login FROM users WHERE login != 'admin'",
            null
        )
        if (cursor.moveToFirst())
        {
            do
            {
                list.add("${cursor.getInt(0)}:${cursor.getString(1)}")
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteUser(id: Int): Boolean
    {
        val db = this.writableDatabase
        return db.delete("users", "id = ?", arrayOf(id.toString())) > 0
    }

    fun updateUser(id: Int, newLogin: String, newPassword: String): Boolean
    {
        val db = this.writableDatabase
        val cv = ContentValues().apply()
        {
            put("login", newLogin)
            put("password", newPassword.toMD5())
        }
        return db.update("users", cv, "id = ?", arrayOf(id.toString())) > 0
    }

    // ==================== МЕТОДЫ ДЛЯ ТОВАРОВ ====================

    fun insertItem(item: Item): Boolean
    {
        val db = this.writableDatabase
        val cv = ContentValues().apply()
        {
            put("title", item.title)
            put("description", item.desc)
            put("full_text", item.text)
            put("price", item.price)
            put("image_uri", item.imageUri)
            put("shelf_life_days", item.shelfLifeDays)
            put("created_at", item.createdAtMillis)
        }
        return db.insert("items", null, cv) != -1L
    }

    @SuppressLint("Range")
    fun getAllItems(): MutableList<Item>
    {
        val list = mutableListOf<Item>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM items ORDER BY id DESC",
            null
        )
        if (cursor.moveToFirst())
        {
            do
            {
                list.add(
                    Item(
                        id = cursor.getInt(cursor.getColumnIndex("id")),
                        title = cursor.getString(cursor.getColumnIndex("title")) ?: "",
                        desc = cursor.getString(cursor.getColumnIndex("description")) ?: "",
                        text = cursor.getString(cursor.getColumnIndex("full_text")) ?: "",
                        price = cursor.getInt(cursor.getColumnIndex("price")),
                        imageUri = cursor.getString(cursor.getColumnIndex("image_uri")) ?: "",
                        shelfLifeDays = cursor.getInt(cursor.getColumnIndex("shelf_life_days")),
                        createdAtMillis = cursor.getLong(cursor.getColumnIndex("created_at"))
                    )
                )
            }
            while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    @SuppressLint("Range")
    fun getItemById(id: Int): Item?
    {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM items WHERE id = ?",
            arrayOf(id.toString())
        )
        var item: Item? = null
        if (cursor.moveToFirst())
        {
            item = Item(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                title = cursor.getString(cursor.getColumnIndex("title")) ?: "",
                desc = cursor.getString(cursor.getColumnIndex("description")) ?: "",
                text = cursor.getString(cursor.getColumnIndex("full_text")) ?: "",
                price = cursor.getInt(cursor.getColumnIndex("price")),
                imageUri = cursor.getString(cursor.getColumnIndex("image_uri")) ?: "",
                shelfLifeDays = cursor.getInt(cursor.getColumnIndex("shelf_life_days")),
                createdAtMillis = cursor.getLong(cursor.getColumnIndex("created_at"))
            )
        }
        cursor.close()
        return item
    }
}