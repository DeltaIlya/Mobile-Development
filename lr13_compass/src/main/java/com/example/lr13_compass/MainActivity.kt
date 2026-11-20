package com.example.lr13_compass

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity() {

    // UI-элементы
    private lateinit var compassView: CompassView
    private lateinit var controlPanel: CardView
    private lateinit var fabOpenPanel: FloatingActionButton
    private lateinit var buttonCollapse: View
    private lateinit var sliderFriction: Slider
    private lateinit var sliderInertia: Slider
    private lateinit var chipGroupStyle: ChipGroup
    private lateinit var buttonReset: MaterialButton

    // TextView для отображения текущих значений
    private lateinit var textFrictionValue: TextView
    private lateinit var textInertiaValue: TextView

    // Значения по умолчанию
    private val defaultFriction = 2.0f
    private val defaultInertia = 1.5f

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Обработка WindowInsets (чтобы панель не прилипала к краям)
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

        // Связываем элементы интерфейса
        compassView = findViewById(R.id.compassView)
        controlPanel = findViewById(R.id.controlPanel)
        fabOpenPanel = findViewById(R.id.fabOpenPanel)
        buttonCollapse = findViewById(R.id.buttonCollapse)
        sliderFriction = findViewById(R.id.sliderFriction)
        sliderInertia = findViewById(R.id.sliderInertia)
        chipGroupStyle = findViewById(R.id.chipGroupStyle)
        buttonReset = findViewById(R.id.buttonReset)
        textFrictionValue = findViewById(R.id.textFrictionValue)
        textInertiaValue = findViewById(R.id.textInertiaValue)

        // Слушатель слайдера трения
        sliderFriction.addOnChangeListener()
        {
            _, value, _ ->
            compassView.setFriction(value)
            textFrictionValue.text = getString(R.string.format_friction, value)
        }

        // Слушатель слайдера инерции
        sliderInertia.addOnChangeListener()
        {
            _, value, _ ->
            compassView.setInertia(value)
            textInertiaValue.text = getString(R.string.format_inertia, value)
        }

        // Слушатель ChipGroup (выбор стиля)
        chipGroupStyle.setOnCheckedChangeListener()
        {
            _, checkedId ->
            val style = when (checkedId)
            {
                R.id.chipClassic -> DialStyle.CLASSIC
                R.id.chipAviation -> DialStyle.AVIATION
                R.id.chipMinimal -> DialStyle.MINIMAL
                else -> DialStyle.CLASSIC
            }
            compassView.dialStyle = style
        }

        // Кнопка сброса параметров
        buttonReset.setOnClickListener()
        {
            sliderFriction.value = defaultFriction
            sliderInertia.value = defaultInertia
            compassView.setFriction(defaultFriction)
            compassView.setInertia(defaultInertia)
            compassView.resetCompass()

            // Сбрасываем стиль на классику
            (findViewById<Chip>(R.id.chipClassic)).isChecked = true
            compassView.dialStyle = DialStyle.CLASSIC

            // Обновляем TextView
            textFrictionValue.text = getString(R.string.format_friction, defaultFriction)
            textInertiaValue.text = getString(R.string.format_inertia, defaultInertia)
        }

        // Кнопка "Свернуть" — прячет панель, показывает FAB
        buttonCollapse.setOnClickListener {
            collapsePanel()
        }

        // FAB — разворачивает панель, прячет сам себя
        fabOpenPanel.setOnClickListener {
            expandPanel()
        }

        // Инициализация начальных значений TextView
        textFrictionValue.text = getString(R.string.format_friction, defaultFriction)
        textInertiaValue.text = getString(R.string.format_inertia, defaultInertia)
    }

    // Плавное сворачивание панели настроек
    private fun collapsePanel() {
        val animator = ObjectAnimator.ofFloat(controlPanel, "translationY", 0f, controlPanel.height.toFloat())
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

        // После анимации прячем панель и показываем FAB
        controlPanel.postDelayed({
            controlPanel.visibility = View.GONE
            fabOpenPanel.visibility = View.VISIBLE

            // Плавное появление FAB
            fabOpenPanel.alpha = 0f
            fabOpenPanel.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
        }, 300)
    }

    // Плавное разворачивание панели настроек
    private fun expandPanel() {
        // Прячем FAB
        fabOpenPanel.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                fabOpenPanel.visibility = View.GONE
            }
            .start()

        // Показываем панель и анимируем её выезд снизу
        controlPanel.visibility = View.VISIBLE
        controlPanel.translationY = controlPanel.height.toFloat()

        val animator = ObjectAnimator.ofFloat(controlPanel, "translationY", controlPanel.height.toFloat(), 0f)
        animator.duration = 300
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }

}
