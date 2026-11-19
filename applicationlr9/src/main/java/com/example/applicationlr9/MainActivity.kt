package com.example.applicationlr9

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity()
{

    private lateinit var textViewData: TextView
    private lateinit var buttonFetch: Button

    // Замените эти значения на ваши из Google Таблицы и Google Cloud Console
    private val spreadsheetId = "1B5m_rCr2g1mxE6g75zLkAwZA30SMuirWr7oeH47GGjU"
    private val apiKey = "AIzaSyD3M-50TPfeB6dPpuHmXs15pI0S1kn9rdQ"
    private val range = "A1:C4"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sheets.googleapis.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val sheetsApi = retrofit.create(SheetsApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        {
                v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewData = findViewById<TextView>(R.id.textViewData)
        buttonFetch = findViewById<Button>(R.id.buttonFetch)

        buttonFetch.setOnClickListener()
        {
            fetchDataFromSheets()
        }
    }

    private fun fetchDataFromSheets()
    {
        textViewData.text = "Загрузка данных..."

        CoroutineScope(Dispatchers.Main).launch()
        {
            try
            {
                // Выполняем сетевой запрос в фоновом потоке IO
                val response = withContext(Dispatchers.IO)
                {
                    sheetsApi.getSheetData(spreadsheetId, range, apiKey)
                }

                // Отображаем полученные строки
                val rows = response.values
                if (!rows.isNullOrEmpty())
                {
                    val stringBuilder = StringBuilder()
                    for (row in rows)
                        stringBuilder.append(row.joinToString(separator = " | ")).append("\n")
                    textViewData.text = stringBuilder.toString()
                }
                else
                    textViewData.text = "Таблица пуста или нет доступа к диапазону."
            }
            catch (e: Exception)
            {
                textViewData.text = "Ошибка загрузки данных"
                Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}