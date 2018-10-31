package com.andreapetreti.subspedia.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class BackgroundTarget implements Target {

    private ImageView mImageView;

    public static BackgroundTarget of(ImageView imageView) {
        return new BackgroundTarget(imageView);
    }

    private BackgroundTarget(ImageView imageView) {
        mImageView = imageView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        mImageView.setBackground(new BitmapDrawable(mImageView.getContext().getResources(), bitmap));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        mImageView.setBackground(errorDrawable);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        mImageView.setBackground(placeHolderDrawable);
    }
}
