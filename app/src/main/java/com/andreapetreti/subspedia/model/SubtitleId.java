package com.andreapetreti.subspedia.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SubtitleId {

    @PrimaryKey
    private int mSubtitleId;

    public SubtitleId() {
    }

    public SubtitleId(int subtitleId) {
        mSubtitleId = subtitleId;
    }

    public int getSubtitleId() {
        return mSubtitleId;
    }

    public void setSubtitleId(int subtitleId) {
        mSubtitleId = subtitleId;
    }
}
