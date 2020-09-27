package com.dating.sdklib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.dating.sdklib.R;

/*
 *用来显示不规则图片，
 * 上面两个是圆角，下面两个是直角
 * */
public class OvalImageView extends AppCompatImageView {
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    //此处可根据自己需要修改大小

    private TypedArray typedArray;
    private float[] rids;


    public OvalImageView(Context context) {
        //super(context);
        this(context, null);
    }


    public OvalImageView(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context, attrs, 0);
    }


    public OvalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.typedArray = context.obtainStyledAttributes(attrs, R.styleable.OvalImageView);
        init();
    }

    private void init() {
        if (typedArray != null) {

            int OvalRadiusBotoomLeft = typedArray.getDimensionPixelSize(R.styleable.OvalImageView_OvalRadiusBotoomLeft, 0);
            int OvalRadiusBotoomRight = typedArray.getDimensionPixelSize(R.styleable.OvalImageView_OvalRadiusBotoomRight, 0);
            float OvalRadiusTopLeft = typedArray.getDimensionPixelSize(R.styleable.OvalImageView_OvalRadiusTopLeft, 0);
            int OvalRadiusTopRight = typedArray.getDimensionPixelSize(R.styleable.OvalImageView_OvalRadiusTopRight, 0);
            int radius = typedArray.getDimensionPixelOffset(R.styleable.OvalImageView_OvalRadius, 0);
            if (radius > 0) {
                OvalRadiusBotoomLeft = radius;
                OvalRadiusBotoomRight = radius;
                OvalRadiusTopLeft = radius;
                OvalRadiusTopRight = radius;
            }
            typedArray.recycle();
            /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
            rids = new float[]{OvalRadiusTopLeft, OvalRadiusTopLeft, OvalRadiusTopRight, OvalRadiusTopRight, OvalRadiusBotoomRight, OvalRadiusBotoomRight, OvalRadiusBotoomLeft, OvalRadiusBotoomLeft,};
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 画图
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth() - getPaddingStart() - getPaddingEnd();
        int h = this.getHeight() - getPaddingTop() - getPaddingBottom();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(new RectF(getPaddingStart() + getPaddingEnd(), getPaddingTop() + getPaddingBottom(), w, h), rids, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);	//关闭硬件加速
        super.onDraw(canvas);
//        Log.i("OvalImageView","onDraw");
    }
}
