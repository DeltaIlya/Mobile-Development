package com.example.applicationlr5

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class EditUserActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        {
            v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DBHelper(this)
        val edit_login_field = findViewById<EditText>(R.id.editText_EditLoginField)
        val edit_password_field = findViewById<EditText>(R.id.editText_EditPasswordField)
        val button_save = findViewById<Button>(R.id.button_Save)

        // Извлекаем переданные интентом метаданные пользователя
        val user_id = intent.getIntExtra("USER_ID", -1)
        val current_login = intent.getStringExtra("USER_LOGIN")

        // Заполняем поле текущим логином для удобства
        edit_login_field.setText(current_login)

        button_save.setOnClickListener()
        {
            val new_login = edit_login_field.text.toString()
            val new_password = edit_password_field.text.toString()

            if (new_login.isEmpty() || new_password.isEmpty())
            {
                Toast.makeText(this, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (user_id != 1)
            {
                // Если логин меняется на другой, проверяем, не занят ли он кем-то еще
                if (new_login != current_login && db.checkLogin(new_login))
                {
                    Toast.makeText(this, "Этот логин уже занят", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val success = db.updateUser(user_id, new_login, new_password)
                if  (success)
                {
                    Toast.makeText(this, "Изменения успешно сохранены", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Ошибка при обновлении базы данных", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}