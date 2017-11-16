package io.bluephoenix.weathertiles.core.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;
import io.bluephoenix.weathertiles.core.data.repository.WeatherRepository;
import io.bluephoenix.weathertiles.util.RefreshData;
import java8.util.concurrent.CompletableFuture;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class UpdateService extends Service
{
    private boolean isRunning;
    private IRepository.Weather weatherRepository;
    private IRepository.Preferences preferences;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate()
    {
        isRunning = false;
        weatherRepository = new WeatherRepository();
        preferences = new PreferencesRepository(
                PreferenceManager.getDefaultSharedPreferences(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!isRunning)
        {
            isRunning = true;
            CompletableFuture.runAsync(() ->
            {
                RefreshData.refresh(weatherRepository, preferences);
                stopSelf();
            });
        }

        //START_STICKY is used for services that are explicitly
        //started and stopped as needed
        return START_STICKY;
    }

    @Override
    public void onDestroy() { this.isRunning = false; }
}
