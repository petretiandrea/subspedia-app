package com.andreapetreti.subspedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.andreapetreti.android_utils.TimeValue;
import com.andreapetreti.subspedia.background.SubtitleWorker;
import com.andreapetreti.subspedia.model.Subtitle;
import com.andreapetreti.subspedia.utils.SubspediaUtils;
import com.annimon.stream.Objects;

import java.sql.Time;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        final Context context = preference.getContext();
        final String stringValue = Objects.isNull(value) ? "" : value.toString();

        if(preference.getKey().equals(context.getString(R.string.key_interval_settings))) {
            ListPreference listPreference = (ListPreference) preference;
            preference.setSummary(context.getString(R.string.summary_interval_settings,
                    listPreference.getEntries()[listPreference.findIndexOfValue(value.toString())]));
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext());

        Object value;
        if(preference instanceof SwitchPreference)
            value = sharedPreferences.getBoolean(preference.getKey(), false);
        else
            value = sharedPreferences.getString(preference.getKey(), null);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new GeneralPreferenceFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // is the list of interval
        if(key.equals(getString(R.string.key_interval_settings))) {
            TimeValue interval = TimeValue.fromMinutes(Long.parseLong(sharedPreferences.getString(key, "60")));
            SubtitleWorker.changeIntervalSubtitleNotification(this, interval);
        }
        // is the notification switch
        else if(key.equals(getString(R.string.key_settings_notification))) {
            boolean enabled = sharedPreferences.getBoolean(key, true);
            if(enabled)
                SubtitleWorker.enableSubtitleNotification(this);
            else
                SubtitleWorker.disableSubtitleNotification(this);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            SwitchPreference switchPreference = (SwitchPreference) findPreference(getString(R.string.key_settings_notification));

            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_interval_settings)));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
