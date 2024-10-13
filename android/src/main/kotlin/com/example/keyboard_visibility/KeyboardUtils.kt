package com.example.keyboard_visibility

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowInsets
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat


interface KeyboardHeightListener {
    fun onKeyboardChanged(isVisible: Boolean, height: Double, duration: Int)
}

class KeyboardUtils : OnGlobalLayoutListener {
    private var mainView: View? = null
    private var activity: Activity? = null
    private var listener: KeyboardHeightListener? = null
    private var keyboardHeight: Int = 0
    private var isVisible: Boolean? = null
    private var duration: Int? = null

    fun registerListener(activity: Activity, listener: KeyboardHeightListener) {
        this.activity = activity
        this.listener = listener
        mainView = activity.findViewById(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            invokeAbove31()
        } else {
            invokeBelow31()
        }
    }

    fun unregisterLister() {
        mainView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        mainView = null
        activity = null
        listener = null
    }


    private fun invokeBelow31() {
        // 添加全局布局监听器
        mainView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun invokeAbove31() {
// 获取键盘高度
        mainView?.setOnApplyWindowInsetsListener { _, insets ->
            val imeInsets = insets.getInsets(WindowInsets.Type.ime())
            val imeHeight = imeInsets.bottom
            setupKeyboardHeight(imeHeight)
            insets
        }

        // 设置 WindowInsetsAnimationCallback 以获取键盘弹出动画时间
        val listener =
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    return insets
                }

                override fun onStart(
                    animation: WindowInsetsAnimationCompat,
                    bounds: WindowInsetsAnimationCompat.BoundsCompat
                ): WindowInsetsAnimationCompat.BoundsCompat {
                    setupDuration(animation.durationMillis.toInt())
                    return super.onStart(animation, bounds)
                }
            }
        mainView?.let {
            ViewCompat.setWindowInsetsAnimationCallback(it, listener)
        }
    }

    private fun setupKeyboardHeight(height: Int) {
        isVisible = height > 0
        keyboardHeight = height
        if (duration != null) {
            listener?.onKeyboardChanged(isVisible!!, convertPixelsToDp(keyboardHeight), duration!!)
            duration = null
            isVisible = null
        }
    }

    private fun setupDuration(duration: Int) {
        this.duration = duration
        if (isVisible != null) {
            listener?.onKeyboardChanged(isVisible!!, convertPixelsToDp(keyboardHeight), duration)
            this.duration = null
            isVisible = null
        }
    }


    override fun onGlobalLayout() {
        // 创建一个矩形来保存屏幕可见区域的尺寸
        val rect = Rect()
        mainView?.getWindowVisibleDisplayFrame(rect)

        // 获取屏幕的整体高度
        val screenHeight: Int = mainView?.rootView?.height ?: 0

        // 计算不可见区域的高度（键盘高度）
        val keypadHeight = screenHeight - rect.bottom
        val keyboardAnimationTime: Int =
            activity?.resources?.getInteger(android.R.integer.config_shortAnimTime) ?: 0
        // 如果键盘高度超过屏幕的 15%，则认为键盘弹出
        if (keypadHeight > screenHeight * 0.15) {
            // 键盘已经弹出
            if (keypadHeight != keyboardHeight) {
                keyboardHeight = keypadHeight
                listener?.onKeyboardChanged(true, convertPixelsToDp(keyboardHeight), keyboardAnimationTime)
            }
        } else {
            // 键盘已经隐藏
            if (keyboardHeight != 0) {
                keyboardHeight = 0
                listener?.onKeyboardChanged(false, 0.0, keyboardAnimationTime)
            }
        }
    }

    fun convertPixelsToDp(px: Int): Double {
        val density = activity?.resources?.displayMetrics?.density ?: return 0.0
        return (px / density).toDouble()
    }
}