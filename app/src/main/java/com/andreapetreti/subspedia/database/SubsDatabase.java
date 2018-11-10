package com.andreapetreti.subspedia.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleId;

@Database(entities = {Serie.class, SerieTranslating.class, Subtitle.class, SubtitleId.class}, version = 15)
public abstract class SubsDatabase extends RoomDatabase {

    private static final String TAG = SubsDatabase.class.getName();

    /* Dao */
    public abstract SerieDao serieDao();
    public abstract SerieTranslatingDao serieTranslatingDao();
    public abstract SubtitlesDao subtitlesDao();
    public abstract SubtitleIdDao subtitleIDsDao();

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
