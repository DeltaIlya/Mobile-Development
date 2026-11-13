package com.example.applicationlr5
import java.security.MessageDigest

fun String.toMD5(): String {
    // Получаем экземпляр MessageDigest для алгоритма MD5
    val md = MessageDigest.getInstance("MD5")

    // Вычисляем хеш в виде массива байтов
    val digest = md.digest(this.toByteArray(Charsets.UTF_8))

    // Преобразуем массив байтов в 16-ричную строку (hex)
    return digest.joinToString("") { "%02x".format(it) }
}