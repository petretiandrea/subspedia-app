package com.andreapetreti.subspedia.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
