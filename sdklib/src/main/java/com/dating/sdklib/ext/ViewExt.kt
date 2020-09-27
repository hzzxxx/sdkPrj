package com.dating.sdklib.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dating.sdklib.app.BaseApplication
import com.dating.sdklib.util.DisplayUtils

/**
 * Created by luyao
 * on 2019/7/9 9:45
 */

/**
 * Set view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Set view invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Set view gone
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * Reverse the view's visibility
 */
fun View.reverseVisibility(needGone: Boolean = true) {
    if (isVisible) {
        if (needGone) gone() else invisible()
    } else visible()
}

fun View.changeVisible(visible: Boolean, needGone: Boolean = true) {
    when {
        visible -> visible()
        needGone -> gone()
        else -> invisible()
    }
}

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) = if (value) visible() else gone()

var View.isInvisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) = if (value) invisible() else visible()

var View.isGone: Boolean
    get() = visibility == View.GONE
    set(value) = if (value) gone() else visible()

/**
 * Set padding
 * @param size top, bottom, left, right padding are same
 */
fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

/**
 * Causes the Runnable which contains action() to be added to the message queue, to be run
 * after the specified amount of time elapses.
 * The runnable will be run on the user interface thread
 *
 * @param action Will be invoked in the Runnable
 * @param delayInMillis The delay (in milliseconds) until the action() will be invoked
 */
inline fun View.postDelayed(delayInMillis: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    postDelayed(runnable, delayInMillis)
    return runnable
}

@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely((width * scale).toInt(), (height * scale).toInt(), config, 1)
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }

}

/**
 * Register a callback to be invoked when the global layout state or the visibility of views
 * within the view tree changes
 *
 * @param callback The callback() to be invoked
 */
inline fun View.onGlobalLayout(crossinline callback: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

/**
 * Register a callback to be invoked after the view is measured
 *
 * @param callback The callback() to be invoked
 */
inline fun View.afterMeasured(crossinline callback: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

var clickCount = 0
var lastClickTime = 0L

/**
 * Invoke the [action] after click [count] times.
 * The interval between two clicks is less than [interval] mills
 */
fun View.clickN(count: Int = 1, interval: Long = 1000, action: () -> Unit) {

    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (lastClickTime != 0L && (currentTime - lastClickTime > interval)) {
            clickCount = 1
            lastClickTime = currentTime
            return@setOnClickListener
        }

        ++clickCount
        lastClickTime = currentTime

        if (clickCount == count) {
            clickCount = 0
            lastClickTime = 0L
            action()
        }
    }
}

fun View.setMarginLeft(pxMargin: Int) {
    this.setMargin(left = pxMargin)
}

fun View.setMarginTop(pxMargin: Int) {
    this.setMargin(top = pxMargin)
}

fun View.setMarginRight(pxMargin: Int) {
    this.setMargin(right = pxMargin)
}

fun View.setMarginBottom(pxMargin: Int) {
    this.setMargin(bottom = pxMargin)
}

fun View.setMarginLeft(dpMargin: Float) {
    this.setMargin(left = dpMargin.toPx())
}

fun View.setMarginTop(dpMargin: Float) {
    this.setMargin(top = dpMargin.toPx())
}

fun View.setMarginRight(dpMargin: Float) {
    this.setMargin(right = dpMargin.toPx())
}

fun View.setMarginBottom(dpMargin: Float) {
    this.setMargin(bottom = dpMargin.toPx())
}

fun View.setMargin(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val marginLayoutParam = layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParam.apply {
            left?.run { leftMargin = left }
            top?.run { topMargin = top }
            right?.run { rightMargin = right }
            bottom?.run { bottomMargin = bottom }
        }
        layoutParams = marginLayoutParam
    }
}
fun View.setSize(w: Int? = null, h: Int? = null) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val marginLayoutParam = layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParam.apply {
            w?.run { width = w }
            h?.run { height = h}
        }
        layoutParams = marginLayoutParam
    }
}

/**
 * 是否滚动底部
 */
fun RecyclerView.isScrollBottom(): Boolean {
    val layoutManager = layoutManager
    if (layoutManager is LinearLayoutManager) {
        val linearLayoutManager = getLayoutManager() as LinearLayoutManager
        //屏幕中最后一个可见子项的position
        //屏幕中最后一个可见子项的position
        val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        //当前屏幕所看到的子项个数
        //当前屏幕所看到的子项个数
        val visibleItemCount = linearLayoutManager.childCount
        //当前RecyclerView的所有子项个数
        //当前RecyclerView的所有子项个数
        val totalItemCount = linearLayoutManager.itemCount
        //RecyclerView的滑动状态
        //RecyclerView的滑动状态
        val state: Int = scrollState
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == RecyclerView.SCROLL_STATE_IDLE) {
            return true
        }
    }
    return false
}

fun Float.toPx(): Int = DisplayUtils.dip2px(BaseApplication.mInstance, this)
fun Int.toPx(): Int = DisplayUtils.dip2px(BaseApplication.mInstance, this.toFloat())