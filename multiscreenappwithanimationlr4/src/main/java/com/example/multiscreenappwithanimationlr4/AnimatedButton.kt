package com.example.multiscreenappwithanimationlr4

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.google.android.material.button.MaterialButton

@SuppressLint("ClickableViewAccessibility")
class AnimatedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    var transformText: CharSequence? = null
    var transformBackgroundColor: Int? = null
    var transformTextColor: Int? = null
    var onSequenceComplete: (() -> Unit)? = null

    private var isReleasing = false
    private var hasTransformed = false
    private var shakeAnimator: ObjectAnimator? = null

    private val amp by lazy { 8f * resources.displayMetrics.density }

    init {
        setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> startHold()
                MotionEvent.ACTION_UP -> {
                    val inside = event.x in 0f..v.width.toFloat() &&
                            event.y in 0f..v.height.toFloat()
                    if (inside) startRelease() else cancelHold()
                }
                MotionEvent.ACTION_CANCEL -> cancelHold()
            }
            false
        }
    }

    private fun startHold() {
        if (isReleasing) return
        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

        animate().scaleX(0.85f).scaleY(0.85f)
            .setDuration(120)
            .setInterpolator(DecelerateInterpolator())
            .start()

        shakeAnimator?.cancel()
        shakeAnimator = ObjectAnimator.ofFloat(
            this, "translationX", 0f, amp, 0f, -amp, 0f
        ).apply {
            duration = 260
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun startRelease() {
        if (isReleasing) return
        isReleasing = true

        shakeAnimator?.cancel()
        shakeAnimator = null
        translationX = 0f

        val expand = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@AnimatedButton, "scaleX", 0.85f, 1.25f).apply { duration = 90 },
                ObjectAnimator.ofFloat(this@AnimatedButton, "scaleY", 0.85f, 1.25f).apply { duration = 90 }
            )
        }

        val contract = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(this@AnimatedButton, "scaleX", 1.25f, 1f).apply { duration = 110 },
                ObjectAnimator.ofFloat(this@AnimatedButton, "scaleY", 1.25f, 1f).apply { duration = 110 }
            )
        }

        val animators = mutableListOf<Animator>(expand, contract)
        if (shouldTransform()) animators += buildTransformAnimator()

        AnimatorSet().apply {
            playSequentially(animators)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isReleasing = false
                    onSequenceComplete?.invoke()
                }
            })
            start()
        }
    }

    private fun cancelHold() {
        shakeAnimator?.cancel()
        shakeAnimator = null
        translationX = 0f
        animate().scaleX(1f).scaleY(1f).setDuration(150).start()
    }

    private fun shouldTransform(): Boolean =
        !hasTransformed && (transformText != null ||
                transformBackgroundColor != null ||
                transformTextColor != null)

    private fun buildTransformAnimator(): Animator {
        val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).apply { duration = 100 }
        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                transformText?.let { text = it }
                transformBackgroundColor?.let { backgroundTintList = ColorStateList.valueOf(it) }
                transformTextColor?.let { setTextColor(it) }
                hasTransformed = true
            }
        })
        val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).apply {
            duration = 180
            interpolator = OvershootInterpolator()
        }
        return AnimatorSet().apply { playSequentially(fadeOut, fadeIn) }
    }

    fun resetTransform() { hasTransformed = false }
}