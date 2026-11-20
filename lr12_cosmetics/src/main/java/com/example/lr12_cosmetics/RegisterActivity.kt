package com.example.lr12_cosmetics

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val rootView = findViewById<View>(R.id.main)
        val initialPaddingLeft = rootView.paddingLeft
        val initialPaddingTop = rootView.paddingTop
        val initialPaddingRight = rootView.paddingRight
        val initialPaddingBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView)
        {
            v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(
                initialPaddingLeft + systemBars.left,
                initialPaddingTop + systemBars.top,
                initialPaddingRight + systemBars.right,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        val db = DBHelper(this)
        val editTextLogin = findViewById<EditText>(R.id.editTextRegisterLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextRegisterPassword)
        val editTextConfirm = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val textViewToLogin = findViewById<TextView>(R.id.textViewToLogin)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        buttonRegister.setOnClickListener()
        {
            val username = editTextLogin.text.toString().trim()
            val password = editTextPassword.text.toString()
            val confirm = editTextConfirm.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty())
            {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm)
            {
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6)
            {
                Toast.makeText(this, "Пароль должен быть не менее 6 символов!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!db.checkLogin(username))
            {
                val success = db.insertUser(username, password)
                if (success)
                {
                    Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show()
                    editTextLogin.text.clear()
                    editTextPassword.text.clear()
                    editTextConfirm.text.clear()
                }
                else
                    Toast.makeText(this, "Ошибка записи в БД", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, getString(R.string.error_login_taken), Toast.LENGTH_SHORT).show()
        }

        textViewToLogin.setOnClickListener()
        {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonBack.setOnClickListener()
        {
            finish()
        }
    }
}