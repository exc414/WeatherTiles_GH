package io.bluephoenix.weathertiles.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

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

        //if(LeakCanary.isInAnalyzerProcess(this)) { return; }
        //LeakCanary.install(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .assetFile("db/weather_compacted.realm")
                .name("weather.realm")
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);

        RealmInspectorModulesProvider inspector = RealmInspectorModulesProvider.builder(this)
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .build();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(inspector)
                .build());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        //Create job scheduler
        JobManager.create(this).addJobCreator(new SyncJobCreator());
    }
}