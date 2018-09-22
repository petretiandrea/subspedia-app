package com.andreapetreti.subspedia.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.andreapetreti.subspedia.model.FavouriteSerie;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.model.Subtitle;

@Database(entities = {Serie.class, SerieTranslating.class, Subtitle.class, FavouriteSerie.class}, version = 7)
public abstract class SubsDatabase extends RoomDatabase {

    private static final String TAG = SubsDatabase.class.getName();

    /* Dao */
    public abstract SerieDao serieDao();
    public abstract SerieTranslatingDao serieTranslatingDao();
    public abstract SubtitlesDao subtitlesDao();


    /* Singleton */
    private static volatile SubsDatabase INSTANCE;
    public static SubsDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (SubsDatabase.class) {
                if(INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SubsDatabase.class, "Subs_Database").addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Log.d(TAG, " cREATE DATABASE");
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                        }
                    })
                            .fallbackToDestructiveMigration()
                            .build();
            }
        }
        return INSTANCE;
    }

}
