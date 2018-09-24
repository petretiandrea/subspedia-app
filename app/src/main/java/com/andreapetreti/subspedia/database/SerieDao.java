package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.andreapetreti.subspedia.model.Serie;

import java.util.List;

@Dao
public interface SerieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Serie serie);

    @Query("UPDATE serie SET mFavorite = :add WHERE mIdSerie = :mIdSerie")
    void setFavourite(boolean add, int mIdSerie);

    @Query("SELECT * FROM serie WHERE mIdSerie = :serieID")
    LiveData<Serie> getSerie(int serieID);

    @Query("SELECT * FROM serie ORDER BY mName ASC")
    LiveData<List<Serie>> getAllSeries();
}
