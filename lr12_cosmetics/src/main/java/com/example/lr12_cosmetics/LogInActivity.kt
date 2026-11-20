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

class LogInActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

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
        val editTextLogin = findViewById<EditText>(R.id.editTextLogin)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val textViewToRegister = findViewById<TextView>(R.id.textViewToRegister)
        val buttonBack = findViewById<Button>(R.id.buttonBack)

        buttonLogin.setOnClickListener()
        {
            val username = editTextLogin.text.toString().trim()
            val password = editTextPassword.text.toString()

            if (username.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (db.checkUserPassword(username, password))
            {
                Toast.makeText(this, "Добро пожаловать, $username!", Toast.LENGTH_SHORT).show()

                val intent: Intent
                if (username == "admin")
                    intent = Intent(this, AdminActivity::class.java)
                else
                    intent = Intent(this, HomeActivity::class.java)

                intent.putExtra("USER_LOGIN", username)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else
                Toast.makeText(this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show()
        }

        textViewToRegister.setOnClickListener()
        {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonBack.setOnClickListener()
        {
            finish()
        }
    }
}