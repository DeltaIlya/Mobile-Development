package com.example.applicationlr5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Обработка WindowInsets
        val rootView = findViewById<android.widget.LinearLayout>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val catalogTitle = findViewById<TextView>(R.id.catalogTitle)
        val countText = findViewById<TextView>(R.id.countText)
        val recyclerView = findViewById<RecyclerView>(R.id.itemsList)
        val buttonBack = findViewById<Button>(R.id.button_Back)

        // Персональный заголовок
        val userLogin = intent.getStringExtra("USER_LOGIN") ?: "Гость"
        catalogTitle.text = "Каталог складных смартфонов для $userLogin"

        // Создаём список из 5 товаров
        val itemsList = arrayListOf<Item>()

        itemsList.add(
            Item(
                id = 1,
                image = "iphone_duo",
                title = "Apple iPhone Duo 256 ГБ",
                desc = "Первый складной смартфон Apple в титановом корпусе с чипом A20 Pro.",
                text = "Складной смартфон формата «книжка» с рамкой из титана Grade 5, защитой от воды и пыли IP68 и поддержкой Dual eSIM. Оснащён основным матовым LTPO Super Retina XDR OLED-экраном 7,6\" (120 Гц, пиковая яркость 3000 нит) и внешним дисплеем 5,4\" (120 Гц). Работает на 2-нм процессоре Apple A20 Pro с 12 ГБ оперативной памяти и iOS 27. Основная двойная камера 48 + 48 Мп дополнена наружной фронтальной камерой 12 Мп и подэкранным модулем. Аккумулятор ёмкостью 4883 мА·ч поддерживает быструю проводную зарядку и MagSafe 25 Вт.",
                price = 218990
            )
        )

        itemsList.add(
            Item(
                id = 2,
                image = "z_fold8",
                title = "Samsung Galaxy Z Fold8 12/256 ГБ",
                desc = "Ультратонкий флагман-книжка весом 201 г на базе Snapdragon 8 Elite Gen 5.",
                text = "Складной смартфон Samsung в обновлённом тонком корпусе (9,7 мм в сложенном виде и 4,5 мм в раскрытом, защита IP48). Оснащён 7,6-дюймовым внутренним экраном Dynamic AMOLED 2X (QXGA+, 120 Гц, до 3000 нит) и внешним дисплеем 5,5\" (120 Гц). В основе лежит флагманский 8-ядерный чип Qualcomm Snapdragon 8 Elite Gen 5 for Galaxy с 12 ГБ ОЗУ и ОС Android 17 (One UI 9). Двойная основная камера 50 + 50 Мп работает в связке с ИИ-алгоритмами обработки кадров, а батарея на 4800 мА·ч поддерживает проводную зарядку 45 Вт.",
                price = 146990
            )
        )

        itemsList.add(
            Item(
                id = 3,
                image = "z_flip8",
                title = "Samsung Galaxy Z Flip8 12/256 ГБ",
                desc = "Компактная раскладушка толщиной 6,1 мм с безрамочным внешним экраном 4,1\".",
                text = "Лёгкий складной смартфон в форм-факторе Flip (вес 180 г, шарнир FlexHinge, защита IP48). В раскрытом виде доступен 6,9-дюймовый дисплей Foldable Dynamic LTPO AMOLED 2X (2520×1080, 120 Гц, яркость до 2600 нит), в закрытом — внешний экран FlexWindow 4,1\" для запуска виджетов. Работает на процессоре Samsung Exynos 2600 с 12 ГБ ОЗУ под управлением Android 17. Камера 50 + 12 Мп с движком ProVisual Engine. Аккумулятор — 4300 мА·ч.",
                price = 84990
            )
        )

        itemsList.add(
            Item(
                id = 4,
                image = "honor_magic_v6",
                title = "Honor Magic V6 16/512 ГБ",
                desc = "Складной смартфон с рекордной батареей 6660 мА·ч и защитой IP69.",
                text = "Флагманская «книжка» толщиной 8,75 мм в сложенном виде и весом 219 г с редким для складных устройств классом пыле- и влагозащиты IP68/IP69. Оснащён крупным внутренним экраном 7,95\" OLED (120 Гц) и внешним 6,52\" OLED-дисплеем. За производительность отвечает Qualcomm Snapdragon 8 Elite Gen 5 в паре с 16 ГБ ОЗУ LPDDR5X. Тройная камера включает модули 50 + 50 + 64 Мп с 3-кратным оптическим зумом и OIS. Аккумулятор на 6660 мА·ч поддерживает зарядку 80 Вт по проводу.",
                price = 134990
            )
        )

        itemsList.add(
            Item(
                id = 5,
                image = "motorola_razr_60_ultra",
                title = "Motorola Razr 60 Ultra 16/512 ГБ",
                desc = "Мощная раскладушка на Snapdragon 8 Elite с экранами 165 Гц.",
                text = "Складной смартфон с отделкой корпуса в расцветках Pantone и защитой IP48. Оба дисплея поддерживают частоту обновления 165 Гц: внутренний 7,0-дюймовый LTPO POLED и внешний 4,0-дюймовый P-OLED для полноценной работы с приложениями без открытия крышки. Построен на базе процессора Qualcomm Snapdragon 8 Elite с 16 ГБ оперативной памяти. Все три камеры имеют разрешение 50 Мп. Аккумулятор на 4700 мА·ч поддерживает быструю проводную зарядку 68 Вт.",
                price = 86300
            )
        )

        // Сортировка по алфавиту (контрольное задание вариант 2)
        itemsList.sortBy { it.title }

        // Счётчик позиций (контрольное задание вариант 4)
        countText.text = "Всего позиций: ${itemsList.size}"

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemsAdapter(itemsList, this)

        // Кнопка Назад
        buttonBack.setOnClickListener()
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}