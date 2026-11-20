package com.example.lr12_cosmetics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity()
{
    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsList: MutableList<Item>
    private lateinit var adapter: ItemsAdapter
    private lateinit var textViewCount: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Обработка WindowInsets с сохранением исходных padding'ов
        val rootView = findViewById<View>(R.id.main)
        val initialPaddingLeft = rootView.paddingLeft
        val initialPaddingTop = rootView.paddingTop
        val initialPaddingRight = rootView.paddingRight
        val initialPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) {
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
        recyclerView = findViewById(R.id.itemsList)
        textViewCount = findViewById(R.id.textViewCount)
        val fabAddItem = findViewById<FloatingActionButton>(R.id.fabAddItem)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Персональный заголовок
        val userLogin = intent.getStringExtra("USER_LOGIN")
        findViewById<TextView>(R.id.catalogTitle).text = "Каталог для $userLogin"

        // Кнопка Назад
        buttonBack.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Кнопка добавления товара
        fabAddItem.setOnClickListener()
        {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadItems()
    }

    // Перезагружаем список при возврате на экран (после добавления товара)
    override fun onResume()
    {
        super.onResume()
        if (::recyclerView.isInitialized)
            loadItems()
    }

    private fun loadItems()
    {
        itemsList = db.getAllItems()
        adapter = ItemsAdapter(itemsList, this)
        recyclerView.adapter = adapter

        // Обновляем счётчик
        textViewCount.text = "Всего позиций: ${itemsList.size}"
    }
}