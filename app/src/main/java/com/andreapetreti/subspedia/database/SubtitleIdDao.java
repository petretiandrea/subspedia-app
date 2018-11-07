package com.andreapetreti.subspedia.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.andreapetreti.subspedia.model.SubtitleId;

import java.util.List;

@Dao
public interface SubtitleIdDao {

    @Insert
    void insert(SubtitleId subtitleId);

    @Query("SELECT * FROM subtitleid")
    List<SubtitleId> getAll();

    @Delete
    void delete(SubtitleId subtitleId);
}
