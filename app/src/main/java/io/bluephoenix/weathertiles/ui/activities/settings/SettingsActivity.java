package io.bluephoenix.weathertiles.ui.activities.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.ui.activities.BaseActivity;

/**
 * @author Carlos A. Perez
 */
public class SettingsActivity extends BaseActivity
{
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setActionBarBackButton(toolbar);

        if(savedInstanceState == null)
        {
            Fragment preferenceFragment = new SettingsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.prefContainer, preferenceFragment);
            ft.commit();
        }
    }
}