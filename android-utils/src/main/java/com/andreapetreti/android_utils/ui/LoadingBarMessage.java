package com.andreapetreti.android_utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andreapetreti.android_utils.R;

public class LoadingBarMessage extends LinearLayout {

    private TextView mMessageView;
    private ProgressBar mProgressBar;

    private String mText;

    public LoadingBarMessage(Context context) {
        super(context);
        initView();
    }

    public LoadingBarMessage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingBarMessage, 0, 0);

        mText = a.getString(R.styleable.LoadingBarMessage_text);

        a.recycle();
    }

    public LoadingBarMessage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingBarMessage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.loading_with_message, this);
    }

    public TextView getMessageView() {
        return mMessageView;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mProgressBar = findViewById(R.id.progress);

        mMessageView = findViewById(R.id.message);
        mMessageView.setText(mText);
    }
}
