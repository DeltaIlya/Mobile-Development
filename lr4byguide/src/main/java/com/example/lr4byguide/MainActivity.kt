package com.example.lr4byguide

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val button1 = findViewById<Button>(R.id.openActivityButton)

        button1.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.scale_up)
            button1.startAnimation(animation)

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    val intent = Intent(this@MainActivity, SecondActivity::class.java)
                    val textToSend = inputEditText.text.toString()
                    intent.putExtra("USER_TEXT", textToSend)
                    startActivity(intent)
                }
            })
        }
    }
}