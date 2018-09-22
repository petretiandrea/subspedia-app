package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Class for rappresent each Serie. Its a Room Database Entity
 */

@Entity
public class Serie implements Parcelable {

    private static final String BASE_URL_IMAGE_LOGO = "https://www.subspedia.tv/immagini/loghi_serie_tv/";
    private static final String BASE_URL_IMAGE_BANNER = "https://www.subspedia.tv/immagini/banner_serie_tv/";

    @PrimaryKey
    @SerializedName("id_serie")
    private int mIdSerie;
    @SerializedName("nome_serie")
    private String mName;
    @SerializedName("link_serie")
    private String mLink;
    @SerializedName("id_thetvdb")
    private int mIdTVDB;
    @SerializedName("stato")
    private String mStatus;
    @SerializedName("anno")
    private int mYear;

    @Ignore
    private boolean mFavorite;

    public Serie(){}

    public int getIdSerie() {
        return mIdSerie;
    }

    public void setIdSerie(int idSerie) {
        mIdSerie = idSerie;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public int getIdTVDB() {
        return mIdTVDB;
    }

    public void setIdTVDB(int idTVDB) {
        mIdTVDB = idTVDB;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public boolean isFavorite() {
        return mFavorite;
    }

    public void setFavorite(boolean favorite) {
        mFavorite = favorite;
    }

    public String getLinkImage() {
        return BASE_URL_IMAGE_LOGO + getIdSerie() + ".png";
    }

    public String getLinkBannerImage() {
        return BASE_URL_IMAGE_BANNER + getIdSerie() + ".jpg";
    }

    @Override
    public String toString() {
        return "Serie{" +
                "mIdSerie=" + mIdSerie +
                ", mName='" + mName + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mIdTVDB=" + mIdTVDB +
                ", mStatus='" + mStatus + '\'' +
                ", mYear=" + mYear +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIdSerie);
        dest.writeString(mName);
        dest.writeString(mLink);
        dest.writeString(mStatus);
        dest.writeInt(mIdTVDB);
        dest.writeInt(mYear);
    }

    public Serie(Parcel in){
        mIdSerie = in.readInt();
        mName = in.readString();
        mLink = in.readString();
        mStatus = in.readString();
        mIdTVDB = in.readInt();
        mYear = in.readInt();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Serie createFromParcel(Parcel in) {
            return new Serie(in);
        }

        @Override
        public Serie[] newArray(int size) {
            return new Serie[0];
        }
    };

}
