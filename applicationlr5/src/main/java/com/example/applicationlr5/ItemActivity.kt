package com.example.applicationlr5

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ItemActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)

        // Обработка WindowInsets
        val rootView = findViewById<android.widget.ScrollView>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageView = findViewById<ImageView>(R.id.itemImageDetail)
        val titleView = findViewById<TextView>(R.id.itemTitle)
        val priceView = findViewById<TextView>(R.id.itemPrice)
        val textView = findViewById<TextView>(R.id.itemText)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Получаем данные из Intent
        titleView.text = intent.getStringExtra("itemTitle") ?: ""
        textView.text = intent.getStringExtra("itemText") ?: ""
        val price = intent.getIntExtra("itemPrice", 0)
        priceView.text = "$price ₽"

        // Загружаем картинку по имени из drawable
        val imageName = intent.getStringExtra("itemImage") ?: ""
        val imageId = resources.getIdentifier(imageName, "drawable", packageName)
        if (imageId != 0)
            imageView.setImageResource(imageId)
        else
            imageView.setImageResource(R.drawable.ic_launcher_foreground)

        // Кнопка Назад
        buttonBack.setOnClickListener()
        {
            finish()
        }
    }
}