package com.example.lr12_cosmetics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddItemActivity : AppCompatActivity()
{
    private lateinit var imageViewPreview: ImageView
    private var selectedImageUri: Uri? = null

    // ActivityResult API для выбора картинки
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    )
    { uri: Uri? ->
        uri?.let()
        {
            // Берём persistable permission, чтобы URI оставался доступным
            try
            {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            catch (e: SecurityException)
            {
                // Если провайдер не поддерживает persistable — не страшно
            }

            selectedImageUri = it

            imageViewPreview.setImageURI(it)
            imageViewPreview.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_item)

        // Обработка WindowInsets
        val rootView = findViewById<View>(R.id.main)
        val initialPaddingLeft = rootView.paddingLeft
        val initialPaddingTop = rootView.paddingTop
        val initialPaddingRight = rootView.paddingRight
        val initialPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(
                initialPaddingLeft + systemBars.left,
                initialPaddingTop + systemBars.top,
                initialPaddingRight + systemBars.right,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        val db = DBHelper(this)
        imageViewPreview = findViewById(R.id.imageViewPreview)
        val buttonChooseImage = findViewById<Button>(R.id.buttonChooseImage)
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextDesc = findViewById<EditText>(R.id.editTextDesc)
        val editTextFullText = findViewById<EditText>(R.id.editTextFullText)
        val editTextPrice = findViewById<EditText>(R.id.editTextPrice)
        val editTextShelfLife = findViewById<EditText>(R.id.editTextShelfLife)
        val buttonSaveItem = findViewById<Button>(R.id.buttonSaveItem)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Кнопка выбора изображения
        buttonChooseImage.setOnClickListener()
        {
            pickImageLauncher.launch("image/*")
        }

        // Кнопка Назад
        buttonBack.setOnClickListener()
        {
            finish()
        }

        // Кнопка Сохранить товар
        buttonSaveItem.setOnClickListener()
        {
            val title = editTextTitle.text.toString().trim()
            val desc = editTextDesc.text.toString().trim()
            val fullText = editTextFullText.text.toString().trim()
            val priceStr = editTextPrice.text.toString().trim()
            val shelfLifeStr = editTextShelfLife.text.toString().trim()

            // Валидация
            if (
                title.isEmpty() || desc.isEmpty() || fullText.isEmpty() ||
                priceStr.isEmpty() || shelfLifeStr.isEmpty()
                )
            {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null)
            {
                Toast.makeText(this, "Выберите изображение товара!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toIntOrNull()
            val shelfLifeDays = shelfLifeStr.toIntOrNull()

            if (price == null || price < 0)
            {
                Toast.makeText(this, "Введите корректную цену!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (shelfLifeDays == null || shelfLifeDays <= 0)
            {
                Toast.makeText(this, "Срок годности должен быть больше 0 дней!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Создаём объект товара
            val newItem = Item(
                id = 0,
                imageUri = selectedImageUri.toString(),
                title = title,
                desc = desc,
                text = fullText,
                price = price,
                createdAtMillis = System.currentTimeMillis(),
                shelfLifeDays = shelfLifeDays
            )

            val success = db.insertItem(newItem)
            if (success)
            {
                Toast.makeText(this, "Товар добавлен!", Toast.LENGTH_SHORT).show()
                finish()  // Возврат в каталог
            }
            else
                Toast.makeText(this, "Ошибка сохранения в БД", Toast.LENGTH_SHORT).show()
        }
    }
}