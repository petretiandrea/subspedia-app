package com.andreapetreti.subspedia.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andreapetreti.subspedia.R;
import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.service.DownloadService;
import com.andreapetreti.subspedia.viewmodel.SeriesViewModel;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;

public class SubtitleDialog extends AppCompatDialogFragment {

    private static final String KEY_SUBTITLE = "sub";

    private Subtitle mSubtitle;

    public static SubtitleDialog newInstance(Subtitle subtitle) {
        SubtitleDialog dialog = new SubtitleDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUBTITLE, subtitle);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubtitle = getArguments().getParcelable(KEY_SUBTITLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        View dialogView = View.inflate(getActivity(), R.layout.dialog_subtitle, null);

        ImageView thub = dialogView.findViewById(R.id.thub);
        Picasso.get().load(mSubtitle.getSubtitleImage()).into(thub);

        TextView title = dialogView.findViewById(R.id.title);
        title.setText(mSubtitle.getEpisodeTitle());

        TextView meta = dialogView.findViewById(R.id.meta);
        meta.setText(String.format(Locale.getDefault(),
                "%dx%d - %s",
                mSubtitle.getSeasonNumber(),
                mSubtitle.getEpisodeNumber(),
                mSubtitle.getDate()));

        TextView description = dialogView.findViewById(R.id.description);
        description.setText(mSubtitle.getDescription());


        builder.setView(dialogView);
        builder.setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ProgressBar progressBar = new ProgressBar(getActivity());
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(mSubtitle.getEpisodeTitle())
                        .setMessage(getString(R.string.downloading_message))
                        .setView(progressBar)
                        .setNegativeButton(getString(R.string.cancel), (dialog1, which1) -> DownloadService.stopDownload(getActivity(), mSubtitle.getLinkFile()))
                        .setCancelable(false)
                        .create();

                alertDialog.show();

                DownloadService.startDownload(getActivity(), mSubtitle.getLinkFile(), new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        super.onReceiveResult(resultCode, resultData);
                        if(resultCode == DownloadService.UPDATE_PROGRESS) {
                            int progress = resultData.getInt("progress");
                            progressBar.setProgress(progress);
                            if(progress == 100) {
                                alertDialog.dismiss();
                                Toast.makeText(getActivity(), getString(R.string.download_complete), Toast.LENGTH_LONG).show();
                            }
                        } else if(resultCode == DownloadService.ERROR_DOWNLOAD)
                            Toast.makeText(SubtitleDialog.this.getContext(), getString(R.string.error_download), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        builder.setCancelable(true);

        return builder.create();
    }
}
