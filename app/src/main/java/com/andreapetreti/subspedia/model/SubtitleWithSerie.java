package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Embedded;
import android.os.Parcel;
import android.os.Parcelable;

public class SubtitleWithSerie implements Parcelable {

    @Embedded
    private Subtitle mSubtitle;

    @Embedded(prefix = "s_")
    private Serie mSerie;

    public SubtitleWithSerie() {}

    public SubtitleWithSerie(Subtitle subtitle, Serie serie) {
        mSubtitle = subtitle;
        mSerie = serie;
    }

    protected SubtitleWithSerie(Parcel in) {
        mSubtitle = in.readParcelable(Subtitle.class.getClassLoader());
        mSerie = in.readParcelable(Serie.class.getClassLoader());
    }

    public static final Creator<SubtitleWithSerie> CREATOR = new Creator<SubtitleWithSerie>() {
        @Override
        public SubtitleWithSerie createFromParcel(Parcel in) {
            return new SubtitleWithSerie(in);
        }

        @Override
        public SubtitleWithSerie[] newArray(int size) {
            return new SubtitleWithSerie[size];
        }
    };

    public Subtitle getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(Subtitle subtitle) {
        mSubtitle = subtitle;
    }

    public Serie getSerie() {
        return mSerie;
    }

    public void setSerie(Serie serie) {
        mSerie = serie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mSubtitle, i);
        parcel.writeParcelable(mSerie, i);
    }
}