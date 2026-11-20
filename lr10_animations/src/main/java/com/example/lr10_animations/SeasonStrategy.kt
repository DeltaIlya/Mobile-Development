package com.example.lr10_animations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

interface SeasonStrategy {
    // Безопасная загрузка картинки
    fun loadBitmap(context: Context, resId: Int): Bitmap? {
        return try {
            BitmapFactory.decodeResource(context.resources, resId)
        } catch (e: Exception) {
            null
        }
    }

    fun updateParticle(
        particle: WeatherView.Particle,
        globalTime: Float,
        height: Int,
        currentGroundLevel: Float
    )

    fun drawParticle(
        canvas: Canvas,
        particle: WeatherView.Particle,
        paint: Paint,
        matrix: Matrix
    )

    fun getSpawnChance(): Float
    fun getMaxParticlesCount(): Int
    fun setupNewParticle(particle: WeatherView.Particle, width: Int, centerY: Float)
    fun getThickness(particle: WeatherView.Particle): Float
}