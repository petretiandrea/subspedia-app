package com.andreapetreti.subspedia.model;

import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class NewSubtitleFavoriteTest {

    private List<Serie> mFavoriteSeries;

    @Before
    public void setup() {

        // emulate 5 favorite series.
        mFavoriteSeries = IntStream.range(1, 5).mapToObj(value -> {
            Serie s = new Serie();
            s.setIdSerie(value);
            s.setName("Serie_" + value);
            return s;
        }).collect(Collectors.toList());
    }

    @Test
    public void filterNewSubtitles() {
        String[] dates = new String[] {
                "2018-10-28 10:20:15", // expected -> deny
                "2018-10-28 22:48:00", // expected -> accept
                "2018-10-28 22:47:00", // expected -> deny
                "2018-10-28 19:14:00" }; // expected -> deny

        long threshold = 3600000; // 1 hour of threshold
        long lastCheckFromParse = SubspediaUtils.parseDateToUTC("yyyy-MM-dd HH:mm:ss",
                "2018-10-28 22:47:00").get().getTime();


        long lastCheck = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        Assert.assertEquals(lastCheckFromParse, lastCheck);

        // emulate 3 subtitles
        List<Subtitle> downloadSubs = IntStream.range(1, dates.length)
                .mapToObj(value -> new Subtitle(value, "Subtitle_" + value, dates[value - 1]))
                .toList();

        List<SubtitleWithSerie> newSubs = SubspediaUtils.filterNewSubtitles(downloadSubs,
                mFavoriteSeries,
                lastCheck,
                threshold);

        // id dei sottotitoli attessi
        List<Integer> expectedIndex = Stream.of(2).collect(Collectors.toList());

        Assert.assertThat(newSubs, new CustomTypeSafeMatcher<List<SubtitleWithSerie>>(expectedIndex.toString()) {
            @Override
            protected boolean matchesSafely(List<SubtitleWithSerie> item) {
                return Stream.of(item)
                        .map(subtitleWithSerie -> subtitleWithSerie.getSerie().getIdSerie())
                        .filter(expectedIndex::contains)
                        .count() == expectedIndex.size();
                }
            });
    }
}