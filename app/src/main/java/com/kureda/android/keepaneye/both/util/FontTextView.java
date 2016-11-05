package com.kureda.android.keepaneye.both.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kureda.android.keepaneye.R;

/**
 * Created by Sergei Kureda
 */

public class FontTextView extends TextView {

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Context context = getContext();
        String font = context.getString(R.string.font_family);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), font);
        setTypeface(tf, 1);
    }
}
