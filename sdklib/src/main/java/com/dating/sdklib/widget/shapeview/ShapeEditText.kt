package com.dating.sdklib.widget.shapeview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.R.attr
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dating.sdklib.R


open class ShapeEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

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
            val rtvBgColor = attributes.getColor(R.styleable.ShapeView_svBgColor, ContextCompat.getColor(context, android.R.color.transparent))
            attributes.recycle()

            val gd = GradientDrawable()//创建drawable
            gd.setColor(rtvBgColor)
            gd.cornerRadius = rtvRadius
            if (rtvBorderWidth > 0) {
                gd.setStroke(rtvBorderWidth, rtvBorderColor)
            }

            this.background = gd
        }
    }
}
