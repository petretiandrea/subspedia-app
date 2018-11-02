package com.andreapetreti.subspedia.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import com.andreapetreti.subspedia.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Target for picasso, automatically add a specific gradient shape over the image.
 * It works for every Android API.
 */
public class TargetImageGradient implements Target {

    private ImageView mImageView;
    private int mGradienteResource;

    public static TargetImageGradient of(ImageView imageView, int gradientResource) {
        return new TargetImageGradient(imageView, gradientResource);
    }

    private TargetImageGradient(ImageView imageView, int gradientResource) {
        mImageView = imageView;
        mGradienteResource = gradientResource;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mImageView.setForeground(mImageView.getContext().getDrawable(mGradienteResource));
            mImageView.setImageBitmap(bitmap);
        } else {
            mImageView.setImageResource(mGradienteResource);
            mImageView.setBackground(new BitmapDrawable(mImageView.getContext().getResources(), bitmap));
        }
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
