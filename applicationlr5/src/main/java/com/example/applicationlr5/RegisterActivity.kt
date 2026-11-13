package com.example.applicationlr5

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)
        val textview_title = findViewById<TextView>(R.id.textView_Title)
        val textedit_login = findViewById<EditText>(R.id.editText_RegisterLogin)
        val textedit_password = findViewById<EditText>(R.id.editText_RegisterPassword)
        val textedit_password_confirm = findViewById<EditText>(R.id.editText_ConfirmRegisterPassword)
        val button_register = findViewById<Button>(R.id.button_Register)
        val button_log_in = findViewById<Button>(R.id.button_Login)

        // Обработка нажатия на кнопку "Регистрация"
        button_register.setOnClickListener()
        {
            val user_name = textedit_login.text.toString().trim()
            val user_password_raw = textedit_password.text.toString()
            val user_password_confirm_raw = textedit_password_confirm.text.toString()

            // Валидация на непустые поля ввода
            if (user_name.isEmpty() || user_password_raw.isEmpty())
            {
                Toast.makeText(this, "Некорректный логин или пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Валидация на совпадение паролей
            if (user_password_raw != user_password_confirm_raw)
            {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!db.checkLogin(user_name))
            {
                val user_password_hash = user_password_raw.toMD5()
                val success = db.insertData(user_name, user_password_hash)
                if (success)
                {
                    Toast.makeText(this, "Регистрация ${user_name} прошла успешно", Toast.LENGTH_SHORT).show()
                    // Очищаем поля после успеха регистрации
                    textedit_login.text.clear()
                    textedit_password.text.clear()
                    textedit_password_confirm.text.clear()

                }
                else
                {
                    Toast.makeText(this, "Ошибка заиси в базе данных", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Данный логин уже используется", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработка нажатия на кнопку "Войти в систему"
        button_log_in.setOnClickListener()
        {
            // переход на страницу входа
        }
    }
}