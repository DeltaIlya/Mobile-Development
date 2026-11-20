package com.example.lr10_animations

import android.content.Context
import android.graphics.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class AutumnStrategy(context: Context) : SeasonStrategy
{
    private val bitmap: Bitmap? = loadBitmap(context, context.resources.getIdentifier("ic_leaf", "drawable", context.packageName))
    private val centerX = bitmap?.width?.div(2f) ?: 0f
    private val centerY = bitmap?.height?.div(2f) ?: 0f

    private val leafPath = Path().apply()
    {
        moveTo(0f, -15f); quadTo(15f, 0f, 0f, 15f); quadTo(-15f, 0f, 0f, -15f); close()
    }
    private val autumnColors = intArrayOf(
        Color.parseColor("#E65100"),
        Color.parseColor("#FBC02D"),
        Color.parseColor("#D84315"))

    override fun getSpawnChance() = 0.30f
    override fun getMaxParticlesCount() = 60

    override fun setupNewParticle(particle: WeatherView.Particle, width: Int, centerY: Float)
    {
        particle.speed = Random.nextFloat() * 7.5f + 1.8f
        particle.angle = Random.nextFloat() * 30f
        particle.rotationSpeed = Random.nextFloat() * 2.5f - 1.25f
        particle.size = if (bitmap != null) Random.nextFloat() * 0.25f + 0.1f else Random.nextFloat() * 6.25f + 3.1f
        particle.color = autumnColors[Random.nextInt(autumnColors.size)]
    }

    override fun updateParticle(particle: WeatherView.Particle, globalTime: Float, height: Int, currentGroundLevel: Float)
    {
        val swing = sin(particle.y * particle.swingFrequency + particle.swingPhase) * particle.baseHorizontalSpeed
        particle.x += swing + cos(globalTime) * 1.2f
        particle.y += particle.speed * (1f + sin(globalTime + particle.swingPhase) * 0.2f)
        particle.angle += particle.rotationSpeed + (swing * 0.5f)
    }

    override fun drawParticle(canvas: Canvas, particle: WeatherView.Particle, paint: Paint, matrix: Matrix) {
        paint.alpha = particle.alpha
        if (bitmap != null)
        {
            matrix.reset()
            matrix.postScale(particle.size, particle.size, centerX, centerY)
            matrix.postRotate(particle.angle, centerX, centerY)
            matrix.postTranslate(particle.x - centerX, particle.y - centerY)
            canvas.drawBitmap(bitmap, matrix, paint)
        }
        else
        {
            paint.color = particle.color
            canvas.save()
            canvas.translate(particle.x, particle.y)
            canvas.rotate(particle.angle)
            canvas.scale(particle.size, particle.size)
            canvas.drawPath(leafPath, paint)
            canvas.restore()
        }
    }

    override fun getThickness(particle: WeatherView.Particle) = 60f * particle.size
}