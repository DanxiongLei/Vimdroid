package com.damonlei.vimdroid.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author damonlei
 * @time 2017/5/5
 * @email danxionglei@foxmail.com
 */
public class SelectedNodeBorderDrawable extends Drawable {

    private Paint paint = new Paint();

    private RectF rectF = new RectF();

    private int strokeWidth = 3;

    {
        paint.setColor(0xff296fcc);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int count = 5;
        for (int i = 0; i < count; i++) {
            float gap = strokeWidth * (i * .9f + .5f);
            rectF.set(gap, gap, canvas.getWidth() - gap, canvas.getHeight() - gap);
            paint.setAlpha((int) (20 + i * (255 - 20f) / (count - 1)));
            canvas.drawRoundRect(rectF, 15 - i * 1.5f, 15 - i * 1.5f, paint);
        }

    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        paint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
