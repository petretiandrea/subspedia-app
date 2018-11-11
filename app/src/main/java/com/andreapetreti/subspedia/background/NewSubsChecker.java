package com.andreapetreti.subspedia.background;

import android.content.Context;
import android.content.ContextWrapper;

import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.database.SubtitleIdDao;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleId;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

public class NewSubsChecker extends ContextWrapper {

    private final SubtitleIdDao mSubtitleIdDao;

    public NewSubsChecker(Context base) {
        super(base);
        mSubtitleIdDao = SubsDatabase.getDatabase(base).subtitleIDsDao();
    }

    /**
     * Starting from list of subtitles, filtering only the subtitles associate to a favorite series.
     * It exclude the already subtitles checked and add
     * this new subs to database as already checked subs.
     * @param subtitles List of subtitle
     * @param favoriteSeries List of favorite series. Used to filter the subititles.
     * @return A new collection of {@link SubtitleWithSerie}, that contains only the new subtitles
     * and associate series.
     */
    public List<SubtitleWithSerie> retrieveOnlyNewSubs(List<Subtitle> subtitles, List<Serie> favoriteSeries) {

        // Retrieve the already subtitles checked from DB.
        List<SubtitleId> alreadyChecked = mSubtitleIdDao.getAll();

        /* Filter and collect new list, that contains the new subtitle.
         * It exclude the already subtitles checked and add, using peek,
         * this new subs to database as already checked subs. */
        return Stream.of(subtitles)
                .flatMap(subtitle -> Stream.of(favoriteSeries).filter(value -> value.getIdSerie() == subtitle.getIdSerie()).map(serie -> new SubtitleWithSerie(subtitle, serie)))
                .filter(value -> !Stream.of(alreadyChecked).anyMatch(id -> value.getSubtitle().getIdEpisode() == id.getSubtitleId()))
                .peek(subtitleWithSerie -> mSubtitleIdDao.insert(new SubtitleId(subtitleWithSerie.getSubtitle().getIdEpisode())))
                .collect(Collectors.toList());
    }
}
