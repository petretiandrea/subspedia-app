package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Relation;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"mIdSerie", "mSeasonNumber", "mEpisodeNumber"},
        indices=@Index(value="mIdSerie"))
public class Subtitle implements Parcelable {

    @SerializedName("id_serie")
    private int mIdSerie;

    @SerializedName("num_stagione")
    private int mSeasonNumber;

    @SerializedName("num_episodio")
    private int mEpisodeNumber;

    @SerializedName("ep_titolo")
    private String mEpisodeTitle;

    @SerializedName("immagine")
    private String mSubtitleImage;

    @SerializedName("link_sottotitoli")
    private String mLinkSubtitle;

    @SerializedName("link_serie")
    private String mLinkSerie;

    @SerializedName("link_file")
    private String mLinkFile;

    @SerializedName("descrizione")
    private String mDescription;

    @SerializedName("data_uscita")
    private String mDate;

    @SerializedName("grazie")
    private int mThanks;

    @ColumnInfo(name = "last_write")
    private long mCurrentMillis;

    public Subtitle() {
        mCurrentMillis = System.currentTimeMillis();
    }

    protected Subtitle(Parcel in) {
        mIdSerie = in.readInt();
        mSeasonNumber = in.readInt();
        mEpisodeNumber = in.readInt();
        mEpisodeTitle = in.readString();
        mSubtitleImage = in.readString();
        mLinkSubtitle = in.readString();
        mLinkSerie = in.readString();
        mLinkFile = in.readString();
        mDescription = in.readString();
        mDate = in.readString();
        mThanks = in.readInt();
        mCurrentMillis = in.readLong();
    }

    public static final Creator<Subtitle> CREATOR = new Creator<Subtitle>() {
        @Override
        public Subtitle createFromParcel(Parcel in) {
            return new Subtitle(in);
        }

        @Override
        public Subtitle[] newArray(int size) {
            return new Subtitle[size];
        }
    };

    public int getIdSerie() {
        return mIdSerie;
    }

    public void setIdSerie(int idSerie) {
        mIdSerie = idSerie;
    }

    public int getSeasonNumber() {
        return mSeasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        mSeasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return mEpisodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        mEpisodeNumber = episodeNumber;
    }

    public String getEpisodeTitle() {
        return mEpisodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        mEpisodeTitle = episodeTitle;
    }

    public String getSubtitleImage() {
        return mSubtitleImage;
    }

    public void setSubtitleImage(String subtitleImage) {
        mSubtitleImage = subtitleImage;
    }

    public String getLinkSubtitle() {
        return mLinkSubtitle;
    }

    public void setLinkSubtitle(String linkSubtitle) {
        mLinkSubtitle = linkSubtitle;
    }

    public String getLinkSerie() {
        return mLinkSerie;
    }

    public void setLinkSerie(String linkSerie) {
        mLinkSerie = linkSerie;
    }

    public String getLinkFile() {
        return mLinkFile;
    }

    public void setLinkFile(String linkFile) {
        mLinkFile = linkFile;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getThanks() {
        return mThanks;
    }

    public void setThanks(int thanks) {
        mThanks = thanks;
    }

    public long getCurrentMillis() {
        return mCurrentMillis;
    }

    public void setCurrentMillis(long currentMillis) {
        mCurrentMillis = currentMillis;
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "mIdSerie=" + mIdSerie +
                ", mSeasonNumber=" + mSeasonNumber +
                ", mEpisodeNumber=" + mEpisodeNumber +
                ", mEpisodeTitle='" + mEpisodeTitle + '\'' +
                ", mSubtitleImage='" + mSubtitleImage + '\'' +
                ", mLinkSubtitle='" + mLinkSubtitle + '\'' +
                ", mLinkSerie='" + mLinkSerie + '\'' +
                ", mLinkFile='" + mLinkFile + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mThanks=" + mThanks +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIdSerie);
        dest.writeInt(mSeasonNumber);
        dest.writeInt(mEpisodeNumber);
        dest.writeString(mEpisodeTitle);
        dest.writeString(mSubtitleImage);
        dest.writeString(mLinkSubtitle);
        dest.writeString(mLinkSerie);
        dest.writeString(mLinkFile);
        dest.writeString(mDescription);
        dest.writeString(mDate);
        dest.writeInt(mThanks);
        dest.writeLong(mCurrentMillis);
    }
}