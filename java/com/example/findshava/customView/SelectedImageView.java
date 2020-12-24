package com.example.findshava.customView;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.findshava.R;

public class SelectedImageView extends AppCompatImageView {

    private String name;
    private boolean isSelected = false;

    public SelectedImageView(Context context) {
        super(context);
    }

    public SelectedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setImageResource(int resId, String name) {
        super.setImageResource(resId);
        this.name = name;
        setSelected(false);
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            onTouchEvent(null);
        }
        this.isSelected = selected;

    }

    public String getName() {
        return name;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.isSelected = !this.isSelected;
        if (!this.isSelected) {
            this.setColorFilter(Color.argb(0, 0, 0, 0));
            this.setBackground(null);
        } else {
            this.setColorFilter(Color.argb(100, 50, 50, 50));
            this.setBackgroundResource(R.drawable.frame_view);
        }

        return false;
    }
}
