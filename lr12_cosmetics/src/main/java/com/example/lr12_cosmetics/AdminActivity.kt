package com.example.lr12_cosmetics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminActivity : AppCompatActivity()
{
    private lateinit var db: DBHelper
    private lateinit var listView: ListView
    private lateinit var usersList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        val rootView = findViewById<View>(R.id.main)
        val initialPaddingLeft = rootView.paddingLeft
        val initialPaddingTop = rootView.paddingTop
        val initialPaddingRight = rootView.paddingRight
        val initialPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                initialPaddingLeft + systemBars.left,
                initialPaddingTop + systemBars.top,
                initialPaddingRight + systemBars.right,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        db = DBHelper(this)
        listView = findViewById(R.id.usersListView)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        buttonBack.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUserData()

        // 1. Обычный клик — редактирование
        listView.setOnItemClickListener()
        {
            _, _, position, _ ->
            val realUserListFromDb = db.getAllUsersRaw()
            val rawItem = realUserListFromDb[position]
            val parts = rawItem.split(":")
            val userId = parts[0].trim().toInt()
            val userLogin = parts[1].trim()

            val intent = Intent(this, EditUserActivity::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("USER_LOGIN", userLogin)
            startActivity(intent)
        }

        // 2. Долгое нажатие — удаление
        listView.setOnItemLongClickListener()
        {
            _, _, position, _ ->
            val realUserListFromDb = db.getAllUsersRaw()
            val rawItem = realUserListFromDb[position]
            val parts = rawItem.split(":")
            val userId = parts[0].trim().toInt()
            val userLogin = parts[1].trim()

            AlertDialog.Builder(this)
                .setTitle("Удаление")
                .setMessage("Удалить аккаунт $userLogin?")
                .setPositiveButton("Удалить") { _, _ ->
                    if (db.deleteUser(userId))
                    {
                        Toast.makeText(this, "Пользователь удалён", Toast.LENGTH_SHORT).show()
                        loadUserData()
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
            true
        }
    }

    override fun onResume()
    {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData()
    {
        usersList = db.getAllUsersForDisplay()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, usersList)
        listView.adapter = adapter
    }
}