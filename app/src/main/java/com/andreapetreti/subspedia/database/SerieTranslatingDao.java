package com.andreapetreti.subspedia.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
