package com.example.applicationlr5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)
        val textview_title = findViewById<TextView>(R.id.textView_Title)
        val textedit_login = findViewById<EditText>(R.id.editText_Login)
        val textedit_password = findViewById<EditText>(R.id.editText_Password)
        val button_register = findViewById<Button>(R.id.button_Register)
        val button_log_in = findViewById<Button>(R.id.button_Login)
        val button_back = findViewById<Button>(R.id.button_Back)

        // Обработка нажатия на кнопку "Регистрация"
        button_log_in.setOnClickListener()
        {
            val user_name = textedit_login.text.toString().trim()
            val user_password_raw = textedit_password.text.toString()

            // Валидация на непустые поля ввода
            if (user_name.isEmpty() || user_password_raw.isEmpty())
            {
                Toast.makeText(this, "Некорректный логин или пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val user_password_hash = user_password_raw.toMD5()

            if (db.checkUserPassword(user_name, user_password_hash))
            {
                Toast.makeText(this, "Добро пожаловать, ${user_name}!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USER_LOGIN", user_name)
                // Очищаем стек экранов, чтобы нельзя было вернуться назад на экран входа
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
            }
        }

        button_register.setOnClickListener()
        {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        button_back.setOnClickListener()
        {
            finish()
        }
    }
}