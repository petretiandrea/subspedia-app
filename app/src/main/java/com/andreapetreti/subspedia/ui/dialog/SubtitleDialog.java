package com.andreapetreti.subspedia.ui.dialog;

import android.app.Dialog;
import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andreapetreti.android_utils.PicassoSingleton;
import com.andreapetreti.subspedia.AppExecutor;
import com.andreapetreti.subspedia.Constants;
import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.database.SubsDatabase;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.model.SubtitleWithSerie;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;

public class SubtitleDialog extends AppCompatDialogFragment {

    private static final String KEY_SUBTITLE = "sub";

    private SubtitleWithSerie mSubtitle;

    public static SubtitleDialog newInstance(SubtitleWithSerie subtitle) {
        SubtitleDialog dialog = new SubtitleDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUBTITLE, subtitle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubtitle = Objects.requireNonNull(getArguments()).getParcelable(KEY_SUBTITLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View dialogView = View.inflate(getActivity(), R.layout.dialog_subtitle, null);

        ImageView thub = dialogView.findViewById(R.id.thub);
        PicassoSingleton.getSharedInstance(getContext()).load(mSubtitle.getSubtitle().getSubtitleImage()).into(thub);

        TextView title = dialogView.findViewById(R.id.title);
        title.setText(String.format(Locale.getDefault(),
                "%dx%d - %s",
                mSubtitle.getSubtitle().getSeasonNumber(),
                mSubtitle.getSubtitle().getEpisodeNumber(),
                mSubtitle.getSubtitle().getEpisodeTitle()));

        TextView meta = dialogView.findViewById(R.id.meta);
        meta.setText(String.format(Locale.getDefault(),
                "%s - %s",
                mSubtitle.getSerie().getName(),
                mSubtitle.getSerie().getStatus()));

        TextView viewDate = dialogView.findViewById(R.id.date);
        viewDate.setText(SubspediaUtils.formatToDefaultDate(mSubtitle.getSubtitle().getDateObj().orElse(new Date())));

        TextView description = dialogView.findViewById(R.id.description);
        description.setText(Html.fromHtml(mSubtitle.getSubtitle().getDescription()));


        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.download), (dialog, which) -> SubspediaUtils.downloadSubtitle(getActivity(), mSubtitle));

        builder.setNeutralButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.setCancelable(true);
        return builder.create();
    }
}
