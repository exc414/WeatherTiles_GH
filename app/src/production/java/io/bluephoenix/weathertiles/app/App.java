package io.bluephoenix.weathertiles.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.evernote.android.job.JobManager;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.jobs.SyncJobCreator;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class App extends Application
{
    //Global context
    private static App instance;
    public static App getInstance() { return instance; }

    @Override
    public void onCreate()
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        super.onCreate();
        instance = this;

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("db/weather_compacted.realm")
                .name("weather.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        //Create job scheduler
        JobManager.create(this).addJobCreator(new SyncJobCreator());
    }
}
