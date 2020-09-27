package com.dating.sdklib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.dating.sdklib.R;

/**
 * author: hzz
 * date:   on 2019/7/4.
 * 渐变 TextView
 */
public class GradientTextView extends AppCompatTextView {
    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private int color1, color2;
    private int direction = 0;
    private Rect mTextBound = new Rect();

    public GradientTextView(Context context) {
        this(context, null);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GradientTextView);
        color1 = ta.getColor(R.styleable.GradientTextView_startColor, Color.parseColor("#363636"));
        color2 = ta.getColor(R.styleable.GradientTextView_endColor, Color.parseColor("#363636"));
        direction = ta.getInt(R.styleable.GradientTextView_gradientDirection, 0);
        ta.recycle();

        if (direction == 0) {
            mLinearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                    new int[]{color1, color2},
                    null, Shader.TileMode.REPEAT);
        } else {
            mLinearGradient = new LinearGradient(0, 0, 0, getMeasuredHeight(),
                    new int[]{color1, color2},
                    null, Shader.TileMode.REPEAT);
        }

        mPaint = getPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String mTipText = getText().toString();
        mPaint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);

        mPaint.setShader(mLinearGradient);
        canvas.drawText(mTipText, getMeasuredWidth() / 2 - mTextBound.width() / 2, getMeasuredHeight() / 2 + mTextBound.height() / 2, mPaint);
    }

    public void setGradientColor(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
        invalidate();
    }

    public void setGradientDirection(int direction) {
        this.direction = direction;
        invalidate();
    }
}
