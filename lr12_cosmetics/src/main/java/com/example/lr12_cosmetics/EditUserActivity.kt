package com.example.lr12_cosmetics

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditUserActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_user)

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

        val db = DBHelper(this)
        val editTextLogin = findViewById<EditText>(R.id.editTextEditLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextEditPassword)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        val userId = intent.getIntExtra("USER_ID", -1)
        val currentLogin = intent.getStringExtra("USER_LOGIN")

        editTextLogin.setText(currentLogin)

        buttonSave.setOnClickListener()
        {
            val newLogin = editTextLogin.text.toString().trim()
            val newPassword = editTextPassword.text.toString().trim()

            if (newLogin.isEmpty() || newPassword.isEmpty())
            {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != -1)
            {
                if (newLogin != currentLogin && db.checkLogin(newLogin))
                {
                    Toast.makeText(this, getString(R.string.error_login_taken), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val success = db.updateUser(userId, newLogin, newPassword)
                if (success)
                {
                    Toast.makeText(this, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                    Toast.makeText(this, "Ошибка обновления БД", Toast.LENGTH_SHORT).show()
            }
        }
    }
}