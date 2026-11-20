package com.example.lr12_cosmetics

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ItemActivity : AppCompatActivity()
{
    private var blinkAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)

        // Обработка WindowInsets
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

        // Получаем ID товара из Intent
        val itemId = intent.getIntExtra("ITEM_ID", -1)
        if (itemId == -1)
        {
            Toast.makeText(this, "Ошибка: товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Загружаем товар из БД
        val db = DBHelper(this)
        val item = db.getItemById(itemId)

        if (item == null)
        {
            Toast.makeText(this, "Товар не найден в БД", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Связываем элементы интерфейса
        val imageView = findViewById<ImageView>(R.id.itemImageDetail)
        val titleView = findViewById<TextView>(R.id.itemTitle)
        val priceView = findViewById<TextView>(R.id.itemPrice)
        val descView = findViewById<TextView>(R.id.itemDescription)
        val fullTextView = findViewById<TextView>(R.id.itemFullText)
        val shelfLifeView = findViewById<TextView>(R.id.textViewShelfLife)
        val daysLeftView = findViewById<TextView>(R.id.textViewDaysLeft)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        // Заполняем данные
        titleView.text = item.title
        priceView.text = getString(R.string.text_price_formatted, item.price)
        descView.text = item.desc
        fullTextView.text = item.text

        // Загрузка картинки: поддержка и URI (галерея), и drawable (seed-товары)
        if (item.imageUri.isNotEmpty())
        {
            var imageLoaded = false

            // 1. Пробуем как URI (для товаров, добавленных через галерею)
            if (item.imageUri.startsWith("content://") || item.imageUri.startsWith("file://"))
            {
                try
                {
                    imageView.setImageURI(Uri.parse(item.imageUri))
                    imageLoaded = true
                }
                catch (e: Exception)
                {
                    // Не URI — пробуем дальше
                }
            }

            // 2. Пробуем как имя drawable-ресурса (для seed-товаров из data_strings.xml)
            if (!imageLoaded)
            {
                val imageId = resources.getIdentifier(
                    item.imageUri,
                    "drawable",
                    packageName
                )
                if (imageId != 0)
                {
                    imageView.setImageResource(imageId)
                    imageLoaded = true
                }
            }

            // 3. Фоллбэк на иконку-заглушку
            if (!imageLoaded)
                imageView.setImageResource(R.drawable.ic_cosmetics)
        }
        else
        {
            imageView.setImageResource(R.drawable.ic_cosmetics)
        }

        // БИЗНЕС-ЛОГИКА ВАРИАНТА 27
        calculateShelfLife(item, shelfLifeView, daysLeftView)

        // Кнопка Назад
        buttonBack.setOnClickListener()
        {
            stopBlinkAnimation()
            finish()
        }
    }

    // Основная бизнес-логика варианта 27:
    // Вычисляет дату окончания срока годности от даты создания товара
    // и отображает "Годен до: ДД.ММ.ГГГГ".
    // Если осталось < 5 дней — текст становится оранжевым и мигает.
    private fun calculateShelfLife(
        item: Item,
        shelfLifeView: TextView,
        daysLeftView: TextView
    )
    {
        // 1. Вычисляем дату окончания = дата создания + shelfLifeDays дней
        val shelfLifeMillis = TimeUnit.DAYS.toMillis(item.shelfLifeDays.toLong())
        val expirationDateMillis = item.createdAtMillis + shelfLifeMillis
        val expirationDate = Date(expirationDateMillis)

        // 2. Форматируем дату в "ДД.ММ.ГГГГ"
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(expirationDate)

        // 3. Выводим "Годен до: ДД.ММ.ГГГГ" ЖИРНЫМ шрифтом (уже задано в XML)
        shelfLifeView.text = "Годен до: $formattedDate"

        // 4. Считаем, сколько дней осталось
        val nowMillis = System.currentTimeMillis()
        val diffMillis = expirationDateMillis - nowMillis
        val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis)

        // 5. Выводим информацию об остатке дней
        when
        {
            daysLeft < 0 ->
            {
                daysLeftView.text = "Срок годности истёк (${(-daysLeft)} дн. назад)"
                daysLeftView.setTextColor(ContextCompat.getColor(this, R.color.colorOutday))
                shelfLifeView.setTextColor(ContextCompat.getColor(this, R.color.colorOutday))
            }
            daysLeft == 0L ->
            {
                daysLeftView.text = "Срок годности истекает СЕГОДНЯ!"
                daysLeftView.setTextColor(ContextCompat.getColor(this, R.color.colorWarning))
                startBlinkAnimation(shelfLifeView, R.color.colorWarning)
            }
            daysLeft < 5 ->
            {
                // Условие: осталось меньше 5 дней
                daysLeftView.text = "Осталось дней: $daysLeft"
                daysLeftView.setTextColor(ContextCompat.getColor(this, R.color.colorWarning))

                // Окрашиваем текст срока годности в оранжевый
                shelfLifeView.setTextColor(ContextCompat.getColor(this, R.color.colorWarning))

                // Запускаем анимацию мигания
                startBlinkAnimation(shelfLifeView, R.color.colorWarning)

                // Показываем Toast-предупреждение
                Toast.makeText(
                    this,
                    getString(R.string.warning_expiring_soon),
                    Toast.LENGTH_LONG
                ).show()
            }
            else ->
            {
                daysLeftView.text = "Осталось дней: $daysLeft"
                daysLeftView.setTextColor(ContextCompat.getColor(this, R.color.colorTextSecondary))
                shelfLifeView.setTextColor(ContextCompat.getColor(this, R.color.colorTextDark))
            }
        }
    }

    // Анимация мигания текста (плавное изменение прозрачности от 1.0 до 0.2 и обратно)
    private fun startBlinkAnimation(view: TextView, colorRes: Int)
    {
        // Сначала применяем цвет
        view.setTextColor(ContextCompat.getColor(this, colorRes))

        // Создаём аниматор прозрачности
        blinkAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.2f).apply()
        {
            duration = 600                  // Пол-цикла = 600мс
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            start()
        }
    }

    // Останавливаем анимацию при выходе с экрана (чтобы не было утечки памяти)
    private fun stopBlinkAnimation()
    {
        blinkAnimator?.cancel()
        blinkAnimator = null
    }

    override fun onDestroy()
    {
        super.onDestroy()
        stopBlinkAnimation()
    }
}