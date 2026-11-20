package com.example.lr10_animations

import android.content.Context
import android.graphics.*
import kotlin.random.Random

class SummerStrategy(context: Context) : SeasonStrategy {
    private val bitmap: Bitmap? = loadBitmap(context, context.resources.getIdentifier("ic_raindrop", "drawable", context.packageName))
    private val centerX = bitmap?.width?.div(2f) ?: 0f
    private val centerY = bitmap?.height?.div(2f) ?: 0f

    override fun getSpawnChance() = 0.50f
    override fun getMaxParticlesCount() = 100

    override fun setupNewParticle(particle: WeatherView.Particle, width: Int, centerY: Float)
    {
        particle.speed = Random.nextFloat() * 10.0f + 5.0f
        particle.angle = 0f
        particle.rotationSpeed = 0f
        particle.size = Random.nextFloat() * 5f + 1f
        particle.alpha = Random.nextInt(100, 255)
    }

    override fun updateParticle(particle: WeatherView.Particle, globalTime: Float, height: Int, currentGroundLevel: Float)
    {
        particle.y += particle.speed * 2.0f
    }

    override fun drawParticle(canvas: Canvas, particle: WeatherView.Particle, paint: Paint, matrix: Matrix)
    {
        if (bitmap != null)
        {
            paint.alpha = particle.alpha
            matrix.reset()
            matrix.postScale(particle.size, particle.size, centerX, centerY)
            matrix.postTranslate(particle.x - centerX, particle.y - centerY)
            canvas.drawBitmap(bitmap, matrix, paint)
        }
        else
        {
            paint.color = Color.parseColor("#4FC3F7")
            paint.alpha = particle.alpha
            paint.strokeWidth = 3f * particle.size
            canvas.drawLine(particle.x, particle.y, particle.x, particle.y + (25f * particle.size), paint)
        }
    }

    override fun getThickness(particle: WeatherView.Particle) = 2f * particle.size
}