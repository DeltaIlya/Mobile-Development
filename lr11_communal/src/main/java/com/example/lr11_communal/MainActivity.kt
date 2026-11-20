package com.example.lr11_communal

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

class MainActivity : AppCompatActivity()
{
    private lateinit var inputField: EditText
    private lateinit var resultText: TextView
    private lateinit var buttonCalculate: Button
    private lateinit var buttonClear: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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

        inputField = findViewById(R.id.editTextMonthlyBill)
        resultText = findViewById(R.id.textViewResult)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        buttonClear = findViewById(R.id.buttonClear)

        // Кнопка "Рассчитать"
        buttonCalculate.setOnClickListener()
        {
            calculateYearlyBill()
        }

        // Кнопка "Очистить"
        buttonClear.setOnClickListener()
        {
            clearAll()
        }
    }

    // Функция расчета годовых коммунальных платежей
    // Итог = M * 12
    private fun calculateYearlyBill() {
        val inputString = inputField.text.toString().trim()

        // Валидация на пустое поле
        if (inputString.isEmpty())
        {
            Toast.makeText(
                this,
                getString(R.string.error_empty),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Преобразование в число с защитой от некорректного ввода
        val monthlyBill: Double = try
        {
            inputString.toDouble()
        }
        catch (e: NumberFormatException)
        {
            Toast.makeText(
                this,
                getString(R.string.error_invalid),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Проверка на отрицательные значения
        if (monthlyBill < 0)
        {
            Toast.makeText(
                this,
                getString(R.string.error_invalid),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Расчет по формуле: Итог = M * 12
        val yearlyTotal = monthlyBill * 12

        // Вывод результата на экран
        resultText.text = getString(R.string.result_formatted, yearlyTotal)

        // Дополнительное условие: если итог > 80000 - вывести сообщение
        if (yearlyTotal > 80000)
            Toast.makeText(this, getString(R.string.warning_high), Toast.LENGTH_LONG).show()
    }

    // Сброс всех полей в исходное состояние.
    private fun clearAll() {
        inputField.text.clear()
        resultText.text = getString(R.string.result_default)
    }
}