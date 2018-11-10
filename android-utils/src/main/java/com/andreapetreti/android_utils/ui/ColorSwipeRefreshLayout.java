package com.andreapetreti.android_utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.andreapetreti.android_utils.R;

public class ColorSwipeRefreshLayout extends SwipeRefreshLayout {

    public ColorSwipeRefreshLayout(@NonNull Context context) {
        super(context);
    }

    public ColorSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorSwipeRefreshLayout, 0, 0);


        int color1 = typedArray.getColor(R.styleable.ColorSwipeRefreshLayout_color1, -1);
        int color2 = typedArray.getColor(R.styleable.ColorSwipeRefreshLayout_color2, -1);
        int color3 = typedArray.getColor(R.styleable.ColorSwipeRefreshLayout_color3, -1);

        if(typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color1))
            setColorSchemeColors(color1);

        if(typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color1) &&
                typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color2)) {
            setColorSchemeColors(color1, color2);
        }

        if(typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color1) &&
                typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color2) &&
                typedArray.hasValue(R.styleable.ColorSwipeRefreshLayout_color3)) {
            setColorSchemeColors(color1, color2, color3);
        }

        typedArray.recycle();
    }


}
