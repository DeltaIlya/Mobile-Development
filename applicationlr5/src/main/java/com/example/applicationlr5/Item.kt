package com.example.applicationlr5

/**
 * Модель товара для каталога.
 * @param id Уникальный ID товара
 * @param image Имя файла картинки в папке drawable (без расширения)
 * @param title Название товара
 * @param desc Краткое описание для карточки
 * @param text Полное описание (для детального экрана)
 * @param price Цена товара в рублях
 */
data class Item(
    val id: Int,
    val image: String,
    val title: String,
    val desc: String,
    val text: String,
    val price: Int
)