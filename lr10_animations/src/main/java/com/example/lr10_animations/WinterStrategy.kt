package com.example.lr10_animations

import android.content.Context
import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class WinterStrategy(context: Context) : SeasonStrategy
{
    private val bitmap: Bitmap? = loadBitmap(context, context.resources.getIdentifier("ic_snowflake", "drawable", context.packageName))
    private val centerX = bitmap?.width?.div(2f) ?: 0f
    private val centerY = bitmap?.height?.div(2f) ?: 0f

    private val roundedSnowflakePath = Path().apply()
    {
        moveTo(-3f, -12f); quadTo(0f, -15f, 3f, -12f); quadTo(22f, 1f, 3f, 13f)
        quadTo(0f, 16f, -3f, 13f); quadTo(-20f, -1f, -3f, -12f); close()
    }

    override fun getSpawnChance() = 0.35f
    override fun getMaxParticlesCount() = 120

    override fun setupNewParticle(particle: WeatherView.Particle, width: Int, centerY: Float)
    {
        particle.speed = Random.nextFloat() * 2.0f + 1.2f
        particle.angle = Random.nextFloat() * 360f
        particle.rotationSpeed = Random.nextFloat() * 1.4f - 0.7f
        particle.size = if (bitmap == null) Random.nextFloat() * 1.1f + 0.3f else Random.nextFloat() * 0.11f + 0.01f
        particle.alpha = Random.nextInt(140, 240)
    }

    override fun updateParticle(particle: WeatherView.Particle, globalTime: Float, height: Int, currentGroundLevel: Float)
    {
        val swing = sin(particle.y * 0.03f + particle.swingPhase) * 0.8f
        particle.x += swing + cos(globalTime) * 0.5f
        particle.y += particle.speed * 0.7f
        particle.angle += particle.rotationSpeed * 0.4f
    }

    override fun drawParticle(canvas: Canvas, particle: WeatherView.Particle, paint: Paint, matrix: Matrix)
    {
        if (bitmap != null)
        {
            paint.alpha = particle.alpha
            matrix.reset()
            matrix.postScale(particle.size, particle.size, centerX, centerY)
            matrix.postRotate(particle.angle, centerX, centerY)
            matrix.postTranslate(particle.x - centerX, particle.y - centerY)
            canvas.drawBitmap(bitmap, matrix, paint)
        }
        else
        {
            paint.color = Color.WHITE
            paint.alpha = particle.alpha
            paint.style = Paint.Style.FILL
            canvas.save()
            canvas.translate(particle.x, particle.y)
            canvas.rotate(particle.angle)
            canvas.scale(particle.size, particle.size)
            canvas.drawPath(roundedSnowflakePath, paint)
            canvas.restore()
        }
    }

    override fun getThickness(particle: WeatherView.Particle) = 10f * particle.size
}