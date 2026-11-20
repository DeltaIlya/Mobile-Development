package com.example.lr13_compass

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.min

/**
 * Кастомный View — анимированный компас с физикой затухающих колебаний.
 *
 * Физическая модель:
 *   acceleration = (-stiffness * angle - friction * angularVelocity) / inertia
 *   angularVelocity += acceleration * dt
 *   angle += angularVelocity * dt
 *
 * При касании пальцем стрелка следует за ним и запоминает скорость.
 * При отпускании — начинается затухание к положению "север" (0°).
 */
class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ==================== ФИЗИКА ====================
    private var angle = 0f                    // Текущий угол отклонения от севера (°)
    private var angularVelocity = 0f          // Угловая скорость (°/сек)
    private var friction = 2.0f               // Коэффициент трения (демпфирование)
    private var inertia = 1.5f                // Инерционность
    private val stiffness = 2.0f              // Жёсткость возврата к северу (константа)

    // ==================== СТИЛЬ ====================
    var dialStyle: DialStyle = DialStyle.CLASSIC
        set(value) {
            field = value
            updatePaintColors()
            invalidate()
        }

    // ==================== КАСАНИЕ ====================
    private var isDragging = false
    private var lastTouchAngle = 0f
    private var lastTouchTime = 0L

    // ==================== ГРАФИКА (Paint) ====================
    private val paintDialFace = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintDialBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }
    private val paintTick = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 48f
        isFakeBoldText = true
    }
    private val paintNeedleNorth = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintNeedleSouth = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCenter = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCenterHole = Paint(Paint.ANTI_ALIAS_FLAG)

    // Путь для стрелки (переиспользуем для производительности)
    private val needlePath = Path()

    // ==================== АНИМАЦИЯ ====================
    private var animator: ValueAnimator? = null
    private var lastFrameTime = 0L

    init {
        updatePaintColors()
        startAnimationLoop()
    }

    // ==================== ПУБЛИЧНЫЙ API (вызывается из MainActivity) ====================

    fun setFriction(value: Float) {
        friction = value.coerceIn(0.1f, 10f)
    }

    fun setInertia(value: Float) {
        inertia = value.coerceIn(0.1f, 10f)
    }

    fun resetCompass() {
        angle = 0f
        angularVelocity = 0f
    }

    // ==================== ИНИЦИАЛИЗАЦИЯ ЦВЕТОВ ====================

    private fun updatePaintColors() {
        when (dialStyle) {
            DialStyle.CLASSIC, DialStyle.MINIMAL -> {
                paintDialFace.color = ContextCompat.getColor(context, R.color.colorDialFace)
                paintDialBorder.color = ContextCompat.getColor(context, R.color.colorDialBorder)
                paintTick.color = ContextCompat.getColor(context, R.color.colorDialTick)
                paintText.color = ContextCompat.getColor(context, R.color.colorDialLabel)
            }
            DialStyle.AVIATION -> {
                paintDialFace.color = ContextCompat.getColor(context, R.color.colorAviationBg)
                paintDialBorder.color = ContextCompat.getColor(context, R.color.colorAviationTick)
                paintTick.color = ContextCompat.getColor(context, R.color.colorAviationTick)
                paintText.color = ContextCompat.getColor(context, R.color.colorAviationLabel)
            }
        }
        paintNeedleNorth.color = ContextCompat.getColor(context, R.color.colorNeedleNorth)
        paintNeedleSouth.color = ContextCompat.getColor(context, R.color.colorNeedleSouth)
        paintCenter.color = ContextCompat.getColor(context, R.color.colorCenterDot)
        paintCenterHole.color = ContextCompat.getColor(context, R.color.colorCard)
    }

    // ==================== ОТРИСОВКА ====================

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) - 24f  // отступ от края

        // 1. Фон циферблата
        drawDialFace(canvas, cx, cy, radius)

        // 2. Деления шкалы и буквы
        drawTicksAndLabels(canvas, cx, cy, radius)

        // 3. Стрелка (с поворотом на текущий угол)
        drawNeedle(canvas, cx, cy, radius)

        // 4. Центральная точка
        drawCenterDot(canvas, cx, cy)
    }

    private fun drawDialFace(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        // Внешний круг (фон)
        canvas.drawCircle(cx, cy, radius, paintDialFace)
        // Рамка
        canvas.drawCircle(cx, cy, radius, paintDialBorder)
    }

    private fun drawTicksAndLabels(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val tickLengthOuter = radius * 0.08f
        val tickLengthMajor = radius * 0.12f
        val labelRadius = radius * 0.78f

        // Определяем шаг делений в зависимости от стиля
        val minorStep = when (dialStyle) {
            DialStyle.CLASSIC -> 10       // каждые 10°
            DialStyle.AVIATION -> 30      // каждые 30°
            DialStyle.MINIMAL -> 90       // только 4 основных направления
        }

        for (deg in 0 until 360 step minorStep) {
            val isMajor = deg % 90 == 0
            val isMedium = deg % 30 == 0

            val tickLength = when {
                isMajor -> tickLengthMajor
                isMedium -> tickLengthOuter * 0.7f
                else -> tickLengthOuter * 0.5f
            }

            val angleRad = Math.toRadians(deg.toDouble())
            val cos = Math.cos(angleRad).toFloat()
            val sin = Math.sin(angleRad).toFloat()

            // Координаты начала и конца деления (сверху, у края циферблата)
            val x1 = cx + (radius - 4f) * sin
            val y1 = cy - (radius - 4f) * cos
            val x2 = cx + (radius - 4f - tickLength) * sin
            val y2 = cy - (radius - 4f - tickLength) * cos

            // Толщина деления
            paintTick.strokeWidth = when {
                isMajor -> 4f
                isMedium -> 2.5f
                else -> 1.5f
            }

            canvas.drawLine(x1, y1, x2, y2, paintTick)

            // Буквы N/S/E/W только на основных направлениях
            if (isMajor && dialStyle != DialStyle.MINIMAL || dialStyle == DialStyle.MINIMAL) {
                val label = when (deg) {
                    0 -> "N"
                    90 -> "E"
                    180 -> "S"
                    270 -> "W"
                    else -> null
                }
                if (label != null) {
                    val lx = cx + labelRadius * sin
                    val ly = cy - labelRadius * cos
                    // Для N — красный цвет
                    val savedColor = paintText.color
                    if (label == "N") {
                        paintText.color = ContextCompat.getColor(context, R.color.colorNeedleNorth)
                    }
                    canvas.drawText(label, lx, ly + paintText.textSize / 3f, paintText)
                    paintText.color = savedColor
                }
            }
        }
    }

    private fun drawNeedle(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        // Сохраняем состояние canvas и поворачиваем на текущий угол
        canvas.save()
        canvas.rotate(angle, cx, cy)

        val needleLengthNorth = radius * 0.70f
        val needleLengthSouth = radius * 0.70f
        val needleWidth = radius * 0.08f

        // Рисуем северную часть (красный треугольник вверх)
        needlePath.reset()
        needlePath.moveTo(cx, cy - needleLengthNorth)          // острие (север)
        needlePath.lineTo(cx - needleWidth, cy)                // левый край у центра
        needlePath.lineTo(cx + needleWidth, cy)                // правый край у центра
        needlePath.close()
        canvas.drawPath(needlePath, paintNeedleNorth)

        // Рисуем южную часть (тёмный треугольник вниз)
        needlePath.reset()
        needlePath.moveTo(cx, cy + needleLengthSouth)          // острие (юг)
        needlePath.lineTo(cx - needleWidth, cy)
        needlePath.lineTo(cx + needleWidth, cy)
        needlePath.close()
        canvas.drawPath(needlePath, paintNeedleSouth)

        canvas.restore()
    }

    private fun drawCenterDot(canvas: Canvas, cx: Float, cy: Float) {
        val dotRadius = 14f
        canvas.drawCircle(cx, cy, dotRadius, paintCenter)
        canvas.drawCircle(cx, cy, dotRadius * 0.5f, paintCenterHole)
    }

    // ==================== ОБРАБОТКА КАСАНИЙ ====================

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                lastTouchAngle = getAngleFromTouch(event)
                lastTouchTime = System.currentTimeMillis()
                angularVelocity = 0f
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isDragging) return true
                val newAngle = getAngleFromTouch(event)
                val now = System.currentTimeMillis()
                val dt = (now - lastTouchTime) / 1000f

                if (dt > 0.001f) {
                    // Вычисляем разницу углов с учётом перехода через ±180°
                    var delta = newAngle - lastTouchAngle
                    if (delta > 180f) delta -= 360f
                    if (delta < -180f) delta += 360f

                    angularVelocity = delta / dt
                    angle += delta
                    // Нормализация
                    normalizeAngle()
                }

                lastTouchAngle = newAngle
                lastTouchTime = now
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                // Ограничиваем максимальную скорость (чтобы не было "безумного" вращения)
                angularVelocity = angularVelocity.coerceIn(-720f, 720f)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * Вычисляет угол касания относительно центра View.
     * 0° = север (вверх), по часовой стрелке — положительные значения.
     */
    private fun getAngleFromTouch(event: MotionEvent): Float {
        val cx = width / 2f
        val cy = height / 2f
        val dx = event.x - cx
        val dy = event.y - cy
        // atan2(dx, -dy) даёт угол от оси Y (север), по часовой стрелке
        return toDegrees(atan2(dx.toDouble(), -dy.toDouble())).toFloat()
    }
    private fun toDegrees(x: Double): Double
    {
        return x * 180 / Math.acos((-1).toDouble())
    }

    private fun normalizeAngle() {
        while (angle > 180f) angle -= 360f
        while (angle < -180f) angle += 360f
    }

    // ==================== АНИМАЦИОННЫЙ ЦИКЛ (60 FPS) ====================

    private fun startAnimationLoop() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16  // ~60 FPS (1000/60 ≈ 16мс)
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val now = System.currentTimeMillis()
                val dt = if (lastFrameTime == 0L) 0.016f else (now - lastFrameTime) / 1000f
                lastFrameTime = now

                updatePhysics(dt)
                invalidate()  // запрашиваем перерисовку
            }
            start()
        }
    }

    /**
     * Шаг физической симуляции.
     * dt — время с последнего кадра в секундах.
     */
    private fun updatePhysics(dt: Float) {
        if (isDragging) return  // пока тащим пальцем — физика не работает

        // Уравнение затухающих колебаний:
        // a = (-k * x - b * v) / m
        val acceleration = (-stiffness * angle - friction * angularVelocity) / inertia

        angularVelocity += acceleration * dt
        angle += angularVelocity * dt

        normalizeAngle()

        // Если скорость и отклонение очень малы — останавливаем (экономия CPU)
        if (kotlin.math.abs(angularVelocity) < 0.01f && kotlin.math.abs(angle) < 0.01f) {
            angularVelocity = 0f
            angle = 0f
        }
    }

    // ==================== ЖИЗНЕННЫЙ ЦИКЛ ====================

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
        animator = null
    }
}