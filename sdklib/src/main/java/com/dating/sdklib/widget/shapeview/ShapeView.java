package com.dating.sdklib.widget.shapeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dating.sdklib.R;

public class ShapeView extends View {
    public ShapeView(Context context) {
       this(context,null);

    }

    public ShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setShape(context, attrs, defStyleAttr);
    }



    public void setShape(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShapeView, defStyleAttr, 0);

        if (attributes != null) {

            int rtvBorderWidth = attributes.getDimensionPixelSize(R.styleable.ShapeView_svBorderWidth, 0);
            int rtvBorderColor = attributes.getColor(R.styleable.ShapeView_svBorderColor, Color.BLACK);
            float rtvRadius = attributes.getDimension(R.styleable.ShapeView_svRadius, 0);
            int rtvBgColor = attributes.getColor(R.styleable.ShapeView_svBgColor, Color.WHITE);
            attributes.recycle();

            GradientDrawable gd = new GradientDrawable();//创建drawable
            gd.setColor(rtvBgColor);
            gd.setCornerRadius(rtvRadius);
            if (rtvBorderWidth > 0) {
                gd.setStroke(rtvBorderWidth, rtvBorderColor);
            }

            this.setBackground(gd);
        }
    }
}
