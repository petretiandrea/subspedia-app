package com.andreapetreti.android_utils;

import android.view.View;

import com.annimon.stream.function.Supplier;

public class ViewVisibility {

    public static int of(Supplier<Boolean> supplier) {
        return supplier.get() ? View.VISIBLE : View.GONE;
    }

}
