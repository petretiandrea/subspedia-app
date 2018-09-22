package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class FavouriteSerie {

    @PrimaryKey
    private int mSerieID;

    public int getSerieID() {
        return mSerieID;
    }

    public void setSerieID(int serieID) {
        mSerieID = serieID;
    }
}
