package com.example.lr10_animations

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Обработка WindowInsets
        val rootView = findViewById<View>(R.id.mainLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mainLayout: ConstraintLayout = findViewById(R.id.mainLayout)
        val weatherView: WeatherView = findViewById(R.id.weatherView)

        val btnAutumn: Button = findViewById(R.id.btnAutumn)
        val btnWinter: Button = findViewById(R.id.btnWinter)
        val btnSummer: Button = findViewById(R.id.btnSummer)

        val autumn = AutumnStrategy(this)
        val winter = WinterStrategy(this)
        val summer = SummerStrategy(this)

        // ПО УМОЛЧАНИЮ — ОСЕНЬ
        setBackgroundOrFallback(
            mainLayout,
            getString(R.string.bg_autumn),
            getString(R.string.color_autumn_fallback)
        )
        weatherView.changeSeason(autumn)

        // осень
        btnAutumn.setOnClickListener()
        {
            setBackgroundOrFallback(mainLayout, getString(R.string.bg_autumn), getString(R.string.color_autumn_fallback))
            weatherView.changeSeason(autumn)
        }

        // зима
        btnWinter.setOnClickListener()
        {
            setBackgroundOrFallback(mainLayout, getString(R.string.bg_winter), getString(R.string.color_winter_fallback))
            weatherView.changeSeason(winter)
        }

        // лето
        btnSummer.setOnClickListener()
        {
            setBackgroundOrFallback(mainLayout, getString(R.string.bg_summer), getString(R.string.color_summer_fallback))
            weatherView.changeSeason(summer)
        }
    }

    private fun setBackgroundOrFallback(layout: ConstraintLayout, imageName: String, fallbackColor: String)
    {
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        if (resId != 0)
            layout.setBackgroundResource(resId)
        else
            layout.setBackgroundColor(Color.parseColor(fallbackColor))
    }
}