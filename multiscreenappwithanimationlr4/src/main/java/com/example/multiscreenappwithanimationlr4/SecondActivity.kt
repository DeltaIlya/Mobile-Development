package com.example.multiscreenappwithanimationlr4

import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        val backButton = findViewById<AnimatedButton>(R.id.backButton)

        resultTextView.text = intent.getStringExtra("USER_TEXT") ?: ""

        backButton.onSequenceComplete = {
            finishAfterTransition()
        }
    }
}