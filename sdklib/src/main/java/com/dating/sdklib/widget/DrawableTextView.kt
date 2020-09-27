package com.dating.sdklib.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.dating.sdklib.R
import com.dating.sdklib.util.DisplayUtils
import com.dating.sdklib.widget.shapeview.ShapeTextView

/**
 *
 * TextView的setCompoundDrawables()可以在其四周设置图片，但是有个众所周知的问题，即无法设置drawable大小。
 * 这就导致在实际的使用中有很大的局限性，必须用代码去控制，就略显麻烦了。
 *
 * 这个自定义控件虽然简单，也非常不起眼，但是用处还真不少：
 * 1.解决了主要矛盾，无法在布局里设置TextView图片大小问题，使用更加简单。
 * 除了设置图片大小，其它的TextView可以的事情这个一样也都可以
 *
 * 2.图片加文字的简单组合非常常见，原本为了适配图片大小不得不用一个xxxLayout＋ImageView+TextView
 * 才能搞定的事，现在用一个控件即可搞定。
 * 在方便、高效使用的同时，也有效的减少了布局层。===》千万不要瞧不上这点苍蝇肉
 *
 * 原理真的简单到只有两句话：
 * 1.通过Drawable的setBound()设置显示区域，也就是图片大小
 * 2.通过TextView的setCompoundDrawables()设置要显示的图片
 *
 * 高能预警：
 * 在网上看到有一个版本，在控件的onMeasure()设置Drawable.setBound(), 在onDraw()里设置    setCompoundDrawables()。
 * 看setCompoundDrawables()源码可以知道，这个方法最终会调用invalide()和requestLayout()，会导致严重的后果就是，
 * onMeasure()和onDraw()会无限循环的互调下去，有点浪费
 */

@SuppressLint("AppCompatCustomView")
class DrawableTextView : ShapeTextView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        var ta: TypedArray? = context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView)
        val width = ta!!.getDimensionPixelOffset(R.styleable.DrawableTextView_drawable_width, -1)
        val height = ta.getDimensionPixelOffset(R.styleable.DrawableTextView_drawable_height, -1)

        val sizeWrap = SizeWrap()
        val startDrawable = ta.getDrawable(R.styleable.DrawableTextView_start_drawable)
        if (startDrawable != null) {
            val lwidth =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_startdrawable_width, -1)
            val lheight =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_startdrawable_height, -1)
            if (sizeWrap.checkWidthAndHeight(width, height, lwidth, lheight)) {
                startDrawable.setBounds(0, 0, sizeWrap.width, sizeWrap.height)
            } else {
                throw IllegalArgumentException("error start drawable size setting")
            }
        }

        val endDrawable = ta.getDrawable(R.styleable.DrawableTextView_end_drawable)
        if (endDrawable != null) {
            val rwidth =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_enddrawable_width, -1)
            val rheight =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_enddrawable_height, -1)
            if (sizeWrap.checkWidthAndHeight(width, height, rwidth, rheight)) {
                endDrawable.setBounds(0, 0, sizeWrap.width, sizeWrap.height)
            } else {
                throw IllegalArgumentException("error end drawable size setting")
            }
        }

        val topDrawable = ta.getDrawable(R.styleable.DrawableTextView_top_drawable)
        if (topDrawable != null) {
            val twidth =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_topdrawable_width, -1)
            val theight =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_topdrawable_height, -1)
            if (sizeWrap.checkWidthAndHeight(width, height, twidth, theight)) {
                topDrawable.setBounds(0, 0, sizeWrap.width, sizeWrap.height)
            } else {
                throw IllegalArgumentException("error top drawable size setting")
            }
        }

        val bottomDrawable = ta.getDrawable(R.styleable.DrawableTextView_bottom_drawable)
        if (bottomDrawable != null) {
            val bwidth =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_bottomdrawable_width, -1)
            val bheight =
                ta.getDimensionPixelOffset(R.styleable.DrawableTextView_bottomdrawable_height, -1)
            if (sizeWrap.checkWidthAndHeight(width, height, bwidth, bheight)) {
                bottomDrawable.setBounds(0, 0, sizeWrap.width, sizeWrap.height)
            } else {
                throw IllegalArgumentException("error bottom drawable size setting")
            }
        }

        this.setCompoundDrawables(startDrawable, topDrawable, endDrawable, bottomDrawable)
        ta.recycle()
        ta = null
    }

    fun setEndDrawable(drawable: Drawable, width: Int, height: Int) {

        drawable.setBounds(
            0,
            0,
            DisplayUtils.dip2px(context, 13f),
            DisplayUtils.dip2px(context, 13f)
        )
        setCompoundDrawables(null, null, drawable, null)
    }
    fun setStartDrawable(drawable: Drawable, width: Int, height: Int) {

        drawable.setBounds(
            0,
            0,
            DisplayUtils.dip2px(context, width.toFloat()),
            DisplayUtils.dip2px(context, height.toFloat())
        )
        setCompoundDrawables(drawable, null, null, null)
    }
    /**
     *
     */
    class SizeWrap {
        internal var width: Int = 0
        internal var height: Int = 0

        fun checkWidthAndHeight(
            globalWidth: Int,
            globalHeight: Int,
            localWidth: Int,
            localHeight: Int
        ): Boolean {
            width = 0
            height = 0

            //局部的大小设置均正常的情况
            if (localWidth > 0 && localHeight > 0) {
                width = localWidth
                height = localHeight
                return true
            }

            //局部大小没设置时，看全局的大小是否正确设置
            if (localWidth == -1 && localHeight == -1) {
                if (globalWidth > 0 && globalHeight > 0) {
                    width = globalWidth
                    height = globalHeight
                    return true
                }
            }

            return false
        }
    }
}
