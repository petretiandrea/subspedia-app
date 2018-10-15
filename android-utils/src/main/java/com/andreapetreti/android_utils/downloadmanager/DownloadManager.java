package com.andreapetreti.android_utils.downloadmanager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.annimon.stream.Optional;

import java.io.File;

public class DownloadManager extends ContextWrapper {

    private static int CURRENT_DOWNLOAD_ID = 1;

    protected static String REQUEST = "request";
    protected static String SMALL_ICON = "small_icon";
    protected static String LARGE_ICON = "large_icon";

    private int mSmallIcon;
    private int mLargeIcon;

    public DownloadManager(Context base, int smallIcon, int largeIcon) {
        super(base);
        mSmallIcon = smallIcon;
        mLargeIcon = largeIcon;
    }

    public synchronized void enqueue(Request request) {
        Intent intent = new Intent(this, DownloadIntentService.class);
        request.setId(CURRENT_DOWNLOAD_ID++);
        intent.putExtra(REQUEST, request);
        intent.putExtra(SMALL_ICON, mSmallIcon);
        intent.putExtra(LARGE_ICON, mLargeIcon);
        startService(intent);
    }

    /**
     * Download Request
     */
    public static class Request implements Parcelable {

        private int mId;

        private String mTitle;

        private String mDescription;
        /**
         * Uri where download
         */
        private Uri mUri;
        /**
         * Path + filename where save the downloaded file.
         */
        private Uri mPath;

        public Request() {
        }

        private Request(Parcel in) {
            mId = in.readInt();
            mTitle = in.readString();
            mDescription = in.readString();
            mUri = in.readParcelable(Uri.class.getClassLoader());
            mPath = in.readParcelable(Uri.class.getClassLoader());
        }

        public static final Creator<Request> CREATOR = new Creator<Request>() {
            @Override
            public Request createFromParcel(Parcel in) {
                return new Request(in);
            }

            @Override
            public Request[] newArray(int size) {
                return new Request[size];
            }
        };

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            mId = id;
        }

        public String getTitle() {
            return mTitle;
        }

        public Request setTitle(String title) {
            mTitle = title;
            return this;
        }

        public String getDescription() {
            return mDescription;
        }

        public Request setDescription(String description) {
            mDescription = description;
            return this;
        }

        protected Uri getUri() {
            return mUri;
        }

        public Request setUri(Uri uri) {
            mUri = uri;
            return this;
        }

        protected Uri getPath() {
            return Uri.parse(mPath.toString());
        }

        public Request setDestinationInExternalPublicDir(String dirType, String subPath) {
            File file = Environment.getExternalStoragePublicDirectory(dirType);
            System.out.println(file);
            if(file == null)
                throw new IllegalStateException("Failed to get external storage public directory");
            else if(file.exists()) {
                if(!file.isDirectory()) {
                    throw new IllegalStateException(file.getAbsolutePath() +
                            " already exists and is not a directory");
                }
            } else {
                if (!file.mkdirs()) {
                    throw new IllegalStateException("Unable to create directory: "+
                            file.getAbsolutePath());
                }
            }
            mPath = Uri.withAppendedPath(Uri.fromFile(file), subPath);
            return this;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeString(mTitle);
            dest.writeString(mDescription);
            dest.writeParcelable(mUri, flags);
            dest.writeParcelable(mPath, flags);
        }
    }

    /**
     * Builder
     */
    public static class Builder {

        private Optional<Context> mContext;
        private Optional<Integer> mSmallIcon;
        private Optional<Integer> mLargeIcon;

        public Builder(Context context) {
            mContext = Optional.of(context);
            mSmallIcon = Optional.empty();
            mLargeIcon = Optional.empty();
        }

        public Builder setSmallIcon(int smallIcon) {
            mSmallIcon = Optional.of(smallIcon);
            return this;
        }

        public Builder setLargeIcon(int largeIcon) {
            mLargeIcon = Optional.of(largeIcon);
            return this;
        }

        public DownloadManager build() {
            if(mContext.isPresent() && mSmallIcon.isPresent()) {
                return new DownloadManager(mContext.get(), mSmallIcon.get(), mLargeIcon.get());
            }
            throw new IllegalArgumentException("Context or small icon not set");
        }
    }
}