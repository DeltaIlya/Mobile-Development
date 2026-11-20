package com.example.lr12_cosmetics

data class Item(
    val id: Int,
    val imageUri: String,       // URI картинки из галереи (или имя drawable)
    val title: String,
    val desc: String,
    val text: String,
    val price: Int,
    val createdAtMillis: Long,  // Дата создания товара (для расчёта "Годен до")
    val shelfLifeDays: Int      // Специфическое поле
)