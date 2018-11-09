package com.andreapetreti.subspedia.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andreapetreti.subspedia.R;

public class EmptyView extends LinearLayout {

    private ImageView mImage;
    private TextView mTitle;
    private TextView mContent;

    public EmptyView(Context context) {
        super(context);
        initView();
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.empty_list_placeholder, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImage = findViewById(R.id.emptyImageView);
        mTitle = findViewById(R.id.emptyTitle);
        mContent = findViewById(R.id.emptyContent);
    }

    public void setImage(Bitmap bitmap) {
        mImage.setImageBitmap(bitmap);
    }

    public void setImage(int res) {
        mImage.setImageResource(res);
    }

    public void setTitle(String text) {
        mTitle.setText(text);
    }

    public void setContent(String content) {
        mContent.setText(content);
    }
}
