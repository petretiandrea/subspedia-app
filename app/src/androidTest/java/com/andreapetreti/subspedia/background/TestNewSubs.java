package com.andreapetreti.subspedia.background;

import android.content.Context;

import com.andreapetreti.subspedia.database.SerieDao;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.database.SubtitleIdDao;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class TestNewSubs {

    private Context mContext;

    @Before
    public void setup() {
        mContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testRetrieveOnlyNewSubs() {
        /* Initializations */
        NewSubsChecker checker = new NewSubsChecker(mContext);
        SubsDatabase database = SubsDatabase.getDatabase(mContext);
        SerieDao serieDao = database.serieDao();
        SubtitleIdDao alreadyConsideredDao = database.subtitleIDsDao();
        // create a list of favorite series.
        IntStream.range(0, 4)
                .mapToObj(value -> {
                    Serie serie = new Serie();
                    serie.setFavorite(true);
                    serie.setIdSerie(value);
                    return serie;
                }).forEach(serieDao::save);

        List<Subtitle> subs = IntStream.range(0, 2)
                .mapToObj(value -> {
                    Subtitle subtitle = new Subtitle();
                    subtitle.setIdSerie(value);
                    subtitle.setIdEpisode(value + 3);
                    return subtitle;
                }).collect(com.annimon.stream.Collectors.toList());

        /* No favorite's series subtitle */
        Subtitle subtitle = new Subtitle();
        subtitle.setIdSerie(1000);
        subtitle.setIdEpisode(100);

        subs.add(subtitle);

        /* Begin Real Test */

        // retrieve favorite series
        List<Serie> favoriteSeries = serieDao.getFavoriteSeriesSync();

        // 1. Test with already considered subtitles EMPTY.
        alreadyConsideredDao.deleteAll();
        Assert.assertTrue("No empty Already considered subtitles!", alreadyConsideredDao.getAll().isEmpty());

        // Method to be tested
        List<SubtitleWithSerie> news = checker.retrieveOnlyNewSubs(subs, favoriteSeries);

        // the result size expected is same of subs size.
        long resultSize = Stream.of(news).map(SubtitleWithSerie::getSubtitle).filter(subs::contains).count();
        Assert.assertEquals(2, resultSize);

        // 2. Test with already considered subtitles not Empty
        Assert.assertTrue("Empty Alredy considered subtitles!", !alreadyConsideredDao.getAll().isEmpty());

        news = checker.retrieveOnlyNewSubs(subs, favoriteSeries);
        resultSize = Stream.of(news).map(SubtitleWithSerie::getSubtitle).filter(subs::contains).count();
        Assert.assertEquals(0, resultSize);
    }

}
