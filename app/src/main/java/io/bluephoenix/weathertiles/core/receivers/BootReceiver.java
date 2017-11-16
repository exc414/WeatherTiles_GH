package io.bluephoenix.weathertiles.core.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            //Alarm has been removed. Therefore set hasAlarmBeenSet preference
            //to false. Then set getDataRefreshNeeded to true so that when the
            //application is next started it will updated itself and set a new
            //alarm.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    context.getApplicationContext());
            IRepository.Preferences preferences = new PreferencesRepository(sharedPreferences);
            preferences.setHasAlarmBeenSet(false);
            preferences.setDataRefreshNeeded(true);
        }
    }
}
