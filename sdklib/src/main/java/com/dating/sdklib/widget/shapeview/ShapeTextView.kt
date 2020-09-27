package com.dating.sdklib.widget.shapeview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.dating.sdklib.R


open class ShapeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        setShape(context, attrs, defStyleAttr)
    }


    fun setShape(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ShapeView, defStyleAttr, 0)

        if (attributes != null) {


            val rtvBorderWidth =
                attributes.getDimensionPixelSize(R.styleable.ShapeView_svBorderWidth, 0)


            val rtvBorderColor =
                attributes.getColor(R.styleable.ShapeView_svBorderColor, Color.BLACK)
            val rtvRadius = attributes.getDimension(R.styleable.ShapeView_svRadius, 0f)
            val rtvBgColor = attributes.getColor(R.styleable.ShapeView_svBgColor, Color.TRANSPARENT)
            attributes.recycle()
        //    if (rtvBorderWidth>0||rtvRadius>0)



            val gd = GradientDrawable()//创建drawable
            if (rtvBgColor!=Color.TRANSPARENT){
                gd.setColor(rtvBgColor)
            }
            gd.cornerRadius = rtvRadius
            if (rtvBorderWidth > 0) {
                gd.setStroke(rtvBorderWidth, rtvBorderColor)
            }

            this.background = gd
        }
    }

    fun setBackgroungColor(@ColorInt color: Int) {
        val myGrad = background as GradientDrawable
        myGrad.setColor(color)
    }
}
