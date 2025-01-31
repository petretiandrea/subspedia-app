package com.andreapetreti.subspedia.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {

    private static final String TAG = ApiResponse.class.getName();

    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        code = response.code();
        if(response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            String message = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                } catch (IOException ignored) {
                    Log.e(TAG, "error while parsing response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            errorMessage = message;
            body = null;
        }
    }

    public  boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

}
