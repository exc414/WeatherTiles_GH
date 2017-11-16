package io.bluephoenix.weathertiles.ui.activities.settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;
import io.bluephoenix.weathertiles.ui.dialogs.Dialogs;
import io.bluephoenix.weathertiles.ui.dialogs.DialogsPublish;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements DialogsPublish.Preferences
{
    private IRepository.Preferences preferences;
    private Preference maxNumberOfTilesPref;

    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted
     *                           at the {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        addPreferencesFromResource(R.xml.app_settings);

        preferences = new PreferencesRepository(PreferenceManager.getDefaultSharedPreferences(
                getActivity().getApplicationContext()));

        maxNumberOfTilesPref = findPreference("key1");
        setSummaryForMaxNumberOfTilesPref();

        SwitchPreferenceCompat blinkAnimation =
                (SwitchPreferenceCompat) findPreference("key2");
        SwitchPreferenceCompat autoScrolling =
                (SwitchPreferenceCompat) findPreference("key3");

        maxNumberOfTilesPref.setOnPreferenceClickListener(preference ->
        {
            Dialogs dialogs = new Dialogs(getActivity());
            dialogs.registerDialogPublishCallback(this);
            dialogs.inputData(R.string.preference_input_title,
                    getString(R.string.preference_max_number_of_tiles_hint),
                    String.valueOf(preferences.getMaxNumberOfTiles()));
            return true;
        });

        blinkAnimation.setOnPreferenceChangeListener((preference, newValue) ->
        {
            preferences.setEnableBlinkAnimation(
                    ((Boolean) newValue) ? Constant.ENABLE_BLINK_ANIMATION : 0);
            return true;
        });

        autoScrolling.setOnPreferenceChangeListener((preference, newValue) ->
        {
            preferences.setEnableAutoScrolling(
                    ((Boolean) newValue) ? Constant.ENABLE_AUTO_SCROLL : 0);
            return true;
        });
    }

    /**
     * Set the new value as the maximum number of tiles that the user can have in
     * the grid list.
     * @param maxNumberOfTiles An int detonating max number of tiles.
     */
    @Override
    public void setPreferences(int maxNumberOfTiles)
    {
        if(maxNumberOfTiles > 0 && maxNumberOfTiles <= Constant.DEFAULT_MAX_TILES)
        {
            preferences.setMaxNumberOfTiles(maxNumberOfTiles);
            setSummaryForMaxNumberOfTilesPref();
        }
        else
        {
            Util.toastMaker("Please input a number between 0 & "
                    + Constant.DEFAULT_MAX_TILES);
        }
    }

    private void setSummaryForMaxNumberOfTilesPref()
    {
        maxNumberOfTilesPref.setSummary(getString(R.string.preference_summary_max_tile_number,
                preferences.getMaxNumberOfTiles(), Constant.DEFAULT_MAX_TILES));
    }
}
