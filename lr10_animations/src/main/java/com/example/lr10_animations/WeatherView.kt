package com.example.lr10_animations

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class WeatherView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Particle(
        var x: Float = 0f,
        var y: Float = 0f,
        var speed: Float = 0f,
        var size: Float = 100f,
        var angle: Float = 0f,
        var rotationSpeed: Float = 0f,
        val baseHorizontalSpeed: Float = Random.nextFloat() * 2.5f + 1.0f,
        val swingFrequency: Float = Random.nextFloat() * 0.015f + 0.005f,
        val swingPhase: Float = Random.nextFloat() * 100f,
        var isGrounded: Boolean = false,
        var isDisappearing: Boolean = false,
        var vx: Float = 0f,
        var vy: Float = 0f,
        var alpha: Int = 255,
        var color: Int = Color.WHITE
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()
    private var globalTime = 0f
    private var heightMap = FloatArray(0)
    private var activeStrategy: SeasonStrategy = AutumnStrategy(context)
    private var lastSpawnTime = 0L
    private val spawnDelayMs = 1000L

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0) heightMap = FloatArray(w)
    }

    fun changeSeason(strategy: SeasonStrategy) {
        activeStrategy = strategy
        clearParticles()
    }

    fun clearParticles() {
        particles.clear()
        heightMap.fill(0f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        globalTime += 0.02f

        val flyingCount = particles.count { !it.isGrounded && !it.isDisappearing }
        val currentTime = System.currentTimeMillis()

        if (flyingCount < activeStrategy.getMaxParticlesCount() && width > 0) {
            if (currentTime - lastSpawnTime >= spawnDelayMs && Random.nextFloat() < activeStrategy.getSpawnChance()) {
                createNewParticle()
                lastSpawnTime = currentTime
            }
        }

        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            if (particle.alpha <= 0) {
                iterator.remove()
                continue
            }

            activeStrategy.drawParticle(canvas, particle, paint, matrix)

            if (particle.isDisappearing) {
                particle.x += particle.vx
                particle.y += particle.vy
                particle.angle += particle.rotationSpeed * 2
                particle.alpha -= 8
            } else if (!particle.isGrounded) {
                activeStrategy.updateParticle(particle, globalTime, height, 0f)

                if (particle.x < 0) particle.x = 0f
                if (particle.x >= width) particle.x = (width - 1).toFloat()

                val currentX = particle.x.toInt().coerceIn(0, width - 1)
                val currentGroundLevel = height.toFloat() - heightMap[currentX]

                if (particle.y >= currentGroundLevel && particle.y > 0) {
                    particle.y = currentGroundLevel
                    particle.isGrounded = true

                    val halfWidth = 15
                    val startX = (currentX - halfWidth).coerceIn(0, width - 1)
                    val endX = (currentX + halfWidth).coerceIn(0, width - 1)
                    val thickness = activeStrategy.getThickness(particle)

                    for (x in startX..endX) {
                        if (heightMap[x] < height) heightMap[x] += thickness
                    }
                }
            }
        }
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val touchX = event.x
                val touchY = event.y
                val activeBitmap = getCurrentBitmap()
                val dynamicBaseRadius = (activeBitmap.width + activeBitmap.height) / 3.5f
                val currentRadius = dynamicBaseRadius + 60f

                for (i in particles.indices.reversed()) {
                    val leaf = particles[i]
                    if (leaf.isDisappearing) continue

                    val distance = sqrt((touchX - leaf.x).pow(2) + (touchY - leaf.y).pow(2))
                    if (distance <= currentRadius) {
                        if (leaf.isGrounded) {
                            val currentX = leaf.x.toInt().coerceIn(0, width - 1)
                            val halfWidth = (activeBitmap.width / 2f * leaf.size).toInt().coerceAtLeast(1)
                            val startX = (currentX - halfWidth).coerceIn(0, width - 1)
                            val endX = (currentX + halfWidth).coerceIn(0, width - 1)
                            val leafThickness = activeStrategy.getThickness(leaf)
                            for (x in startX..endX) {
                                heightMap[x] = (heightMap[x] - leafThickness).coerceAtLeast(0f)
                            }
                        }
                        leaf.isGrounded = false
                        leaf.isDisappearing = true
                        leaf.vx = Random.nextFloat() * 26f - 13f
                        leaf.vy = Random.nextFloat() * -20f - 10f
                    }
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun createNewParticle() {
        val activeBitmap = getCurrentBitmap()
        val offsetY = activeBitmap.height / 2f
        val p = Particle(x = Random.nextFloat() * width, y = -offsetY * 2)
        activeStrategy.setupNewParticle(p, width, offsetY)
        particles.add(p)
    }

    private fun getCurrentBitmap(): Bitmap {
        return when (activeStrategy) {
            is AutumnStrategy -> BitmapFactory.decodeResource(resources, context.resources.getIdentifier("ic_leaf", "drawable", context.packageName)) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            is WinterStrategy -> BitmapFactory.decodeResource(resources, context.resources.getIdentifier("ic_snowflake", "drawable", context.packageName)) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            is SummerStrategy -> BitmapFactory.decodeResource(resources, context.resources.getIdentifier("ic_raindrop", "drawable", context.packageName)) ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            else -> Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }
}