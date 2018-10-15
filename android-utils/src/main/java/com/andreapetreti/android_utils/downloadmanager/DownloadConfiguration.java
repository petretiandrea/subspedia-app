package com.andreapetreti.android_utils.downloadmanager;

import android.graphics.Bitmap;

import com.annimon.stream.Optional;

public class DownloadConfiguration {

    private int mSmallIcon;
    private Optional<Bitmap> mLargeIcon;
    private Optional<Integer> mColor;

    public DownloadConfiguration(int smallIcon) {
        mSmallIcon = smallIcon;
        mLargeIcon = Optional.empty();
        mColor = Optional.empty();
    }

    public DownloadConfiguration setLargeIcon(Bitmap bitmap) {
        mLargeIcon = Optional.of(bitmap);
        return this;
    }

    public DownloadConfiguration setColor(int color) {
        mColor = Optional.of(color);
        return this;
    }

    public int getSmallIcon() {
        return mSmallIcon;
    }

    public Optional<Bitmap> getLargeIcon() {
        return mLargeIcon;
    }

    public Optional<Integer> getColor() {
        return mColor;
    }
}
