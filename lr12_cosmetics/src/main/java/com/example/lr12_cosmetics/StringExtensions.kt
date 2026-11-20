package com.example.lr12_cosmetics

import java.security.MessageDigest

// Extension-функция для хеширования строки алгоритмом MD5
// Используется для "безопасного" хранения паролей в БД
fun String.toMD5(): String
{
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray(Charsets.UTF_8))
    return digest.joinToString("")
    {
        "%02x".format(it)
    }
}