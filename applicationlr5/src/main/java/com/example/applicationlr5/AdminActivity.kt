package com.example.applicationlr5

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class AdminActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var listView: ListView
    private lateinit var usersList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        listView = findViewById(R.id.usersListView)
        val button_back = findViewById<Button>(R.id.button_Back)
        val button_reset = findViewById<Button>(R.id.button_ResetDb)

        button_reset.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Сброс базы данных")
                .setMessage("Все зарегистрированные пользователи будут удалены, а счетчик ID обнулится. Продолжить?")
                .setPositiveButton("Сбросить") { _, _ ->
                    db.resetDatabase()
                    loadUserData()
                    Toast.makeText(this, "База данных успешно сброшена", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }


        button_back.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUserData()

        // 1. Обычный клик — Изменение данных
        listView.setOnItemClickListener()
        {
            _, _, position, _ ->
            // ИСПОЛЬЗУЕМ КОРРЕКТНЫЙ RAW МЕТОД: берем точные ID из базы данных по индексу клика
            val realUserListFromDb = db.getAllUsersRaw()
            val rawItem = realUserListFromDb[position]
            val parts = rawItem.split(":")
            val userId = parts[0].trim().toInt() // Здесь гарантированно будет реальный ID (например, 3 или 5)
            val userLogin = parts[1].trim()
            val intent = Intent(this, EditUserActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }

        // 2. Долгое удержание — Удаление
        listView.setOnItemLongClickListener()
        {
            _, _, position, _ ->
            // ИСПОЛЬЗУЕМ КОРРЕТНЫЙ RAW МЕТОД
            val realUserListFromDb = db.getAllUsersRaw()
            val rawItem = realUserListFromDb[position]
            val parts = rawItem.split(":")
            val userId = parts[0].trim().toInt()
            val userLogin = parts[1].trim()
            AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Вы уверены, что хотите безвозвратно удалить аккаунт $userLogin?")
            .setPositiveButton("Удалить")
            {
                _, _ ->
                if (db.deleteUser(userId))
                {
                    Toast.makeText(this, "Пользователь успешно удален", Toast.LENGTH_SHORT).show()
                    loadUserData() // Перерисовываем экран, нумерация обновится красиво
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        // Для отображения на экране загружаем красивый список без пропусков в ID
                usersList = db.getAllUsersForDisplay()
        adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, usersList)
        listView.adapter = adapter
    }
}