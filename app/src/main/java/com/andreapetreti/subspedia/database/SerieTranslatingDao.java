package com.andreapetreti.subspedia.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;

import java.util.List;

@Dao
public interface SerieTranslatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(SerieTranslating serieTranslating);

    @Query("SELECT * FROM serietranslating WHERE mIdSerie = :serieID")
    LiveData<SerieTranslating> getTranslatingSerie(int serieID);

    @Query("SELECT * FROM serietranslating ORDER BY mName ASC")
    LiveData<List<SerieTranslating>> getAllTranslatingSeries();
}
