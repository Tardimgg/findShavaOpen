package com.example.findshava.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.findshava.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FloatingTextActionButton extends LinearLayout {

    private FloatingActionButton floatingActionButton;
    private TextView textView;

    public FloatingTextActionButton(Context context) {
        super(context);
        init(context);
    }

    public FloatingTextActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatingTextActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FloatingTextActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.floating_text_action_button, this);
        this.floatingActionButton = view.findViewById(R.id.floating_action_button);
        this.textView = view.findViewById(R.id.text_view);
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public TextView getTextView() {
        return textView;
    }
}
