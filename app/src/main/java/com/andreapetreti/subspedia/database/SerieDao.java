package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.Serie;

import java.util.List;

@Dao
public interface SerieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Serie serie);

    @Query("SELECT * FROM serie, (SELECT * FROM favouriteserie WHERE mSerieID = :serieID) WHERE mIdSerie = :serieID")
    LiveData<Serie> getSerie(int serieID);

    @Query("SELECT * FROM serie ORDER BY mName ASC")
    LiveData<List<Serie>> getAllSeries();
}
