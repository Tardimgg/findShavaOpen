package com.example.findshava.customSpannableSpan;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;

public class RoundedBackgroundSpan extends ReplacementSpan {


    private int cornerRadius;
    private int backgroundColor;
    private int textColor;
    private int size;


    public RoundedBackgroundSpan(int backgroundColor, int textColor, int cornerRadius, int size) {
        super();
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.cornerRadius = cornerRadius;
        this.size = size;
    }

    public RoundedBackgroundSpan() {
        super();
        this.backgroundColor = Color.BLACK;
        this.textColor = Color.WHITE;
        this.cornerRadius = 8;
        this.size = 0;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        RectF rect = new RectF(x, top - size, x + measureText(paint, text, start, end), bottom + size);
        paint.setColor(this.backgroundColor);
        canvas.drawRoundRect(rect, this.cornerRadius, this.cornerRadius, paint);
        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x, y, paint);
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(paint.measureText(text, start, end));
    }


    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }
}