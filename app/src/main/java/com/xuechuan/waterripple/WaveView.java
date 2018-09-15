package com.xuechuan.waterripple;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: WaterRipple
 * @Package com.xuechuan.waterripple
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/9/15 10:22
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class WaveView extends View {
    public static final String TAG = "【" + WaveView.class.getSimpleName() + "】==";
    private int imgerid;
    private boolean rise;
    private int dimension;
    private int originy;
    private int waveHeigh = 80;
    private int wavelength = 400;
    private Bitmap mbitmap;
    private Paint mPaint;
    private int width, height;
    private Path path;
    private ValueAnimator animator;
    private int dx, dy;
    private Region region;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

/*    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }*/


    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        imgerid = a.getResourceId(R.styleable.WaveView_boatBitamp, 0);
        rise = a.getBoolean(R.styleable.WaveView_rise, false);
        dimension = (int) a.getDimension(R.styleable.WaveView_duration, 2000);
        originy = (int) a.getDimension(R.styleable.WaveView_originY, 500);
        waveHeigh = (int) a.getDimension(R.styleable.WaveView_waveHeight, 200);
        wavelength = (int) a.getDimension(R.styleable.WaveView_waveLength, 400);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        if (imgerid > 0) {
            mbitmap = BitmapFactory.decodeResource(context.getResources(), imgerid, options);
            //xfrmode 加载成圆形图片
//            mbitmap = getCircleBitmap(mbitmap);
        } else {
            mbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog, options);
        }
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.water_color));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);//填充
        path = new Path();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        width = widthSize;
        height = heightSize;
        if (originy == 0) {
            originy = height;
        }
        if (wavelength >= height) {
            wavelength = height;
        }


    }

    private Bitmap getCircleBitmap(Bitmap mbitmap) {
        if (mbitmap == null) return null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, mbitmap.getWidth(), mbitmap.getHeight());
            RectF rectF = new RectF(new Rect(0, 0, mbitmap.getWidth(), mbitmap.getHeight()));
            float roundPx = 0.0f;
            if (mbitmap.getWidth() > mbitmap.getHeight()) {
                roundPx = mbitmap.getHeight() / 2.0f;
            } else {
                roundPx = mbitmap.getWidth() / 2.0f;
            }
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Rect src = new Rect(0, 0, mbitmap.getWidth(), mbitmap.getHeight());
            canvas.drawBitmap(mbitmap, src, rect, paint);

            return bitmap;
        } catch (Exception e) {
            return mbitmap;
//            e.printStackTrace();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //定义曲线
        setPathData();

        canvas.drawPath(path, mPaint);
//        PathMeasure p = new PathMeasure(path, false);
        //绘制头像
        Rect bounds = region.getBounds();
        Log.e(TAG, "onDraw: right=" + bounds.right +
                ";lift==" + bounds.left + ";botton==" + bounds.bottom + ";top==" + bounds.top);
        if (bounds.top > 0 || bounds.bottom > 0) {
            if (bounds.top < originy) {
                canvas.drawBitmap(mbitmap, bounds.right - mbitmap.getWidth() / 2,
                        bounds.top - mbitmap.getHeight(), mPaint);
            } else {
                canvas.drawBitmap(mbitmap, bounds.right - mbitmap.getWidth() / 2,
                        bounds.bottom - mbitmap.getHeight(), mPaint);
            }
        } else {
            int i = width / 2 - mbitmap.getWidth() / 2;
            canvas.drawBitmap(mbitmap, i,
                    originy - mbitmap.getHeight(), mPaint);
        }

    }

    private void setPathData() {
        path.reset();
        int halfWaveLength = wavelength / 2;
        path.moveTo(-wavelength + dx, originy - dy);
        for (int i = -wavelength; i < width + wavelength; i += wavelength) {
//            path.quadTo();
            path.rQuadTo(halfWaveLength / 2, -waveHeigh, halfWaveLength, 0);
            path.rQuadTo(halfWaveLength / 2, waveHeigh, halfWaveLength, 0);
        }

        region = new Region();
        int i = width / 2;

        Region clip = new Region((int) (i - 0.1), 0, (int) i, height * 2);
        region.setPath(path, clip);

        //封闭曲线
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();


    }

    public void startAnimationin() {
        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(dimension);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                dx = (int) (wavelength * animatedValue);
                //波纹
//                dy+=2;
                postInvalidate();
            }
        });
        animator.start();

    }
}
