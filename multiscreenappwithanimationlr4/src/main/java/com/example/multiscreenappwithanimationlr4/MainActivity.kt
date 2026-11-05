package com.example.multiscreenappwithanimationlr4

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var inputEditText: TextInputEditText
    private lateinit var button1: AnimatedButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputEditText = findViewById(R.id.inputEditText)
        button1 = findViewById(R.id.openActivityButton)

        button1.onSequenceComplete = {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("USER_TEXT", inputEditText.text.toString())

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                Pair(button1 as android.view.View, ViewCompat.getTransitionName(button1)!!),
                Pair(inputEditText as android.view.View, ViewCompat.getTransitionName(inputEditText)!!)
            )
            startActivity(intent, options.toBundle())
        }
    }

    override fun onResume() {
        super.onResume()
        button1.resetTransform()
        button1.text = "Перейти"
        button1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6200EE"))
        button1.setTextColor(Color.WHITE)
        button1.alpha = 1f
        button1.scaleX = 1f
        button1.scaleY = 1f
        button1.translationX = 0f
    }
}