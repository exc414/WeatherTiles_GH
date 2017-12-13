package io.bluephoenix.weathertiles.core.jobs;

import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import io.bluephoenix.weathertiles.app.App;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;
import io.bluephoenix.weathertiles.core.data.repository.WeatherRepository;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.RefreshData;

/**
 * @author Carlos A. Perez
 */
public class DailySyncJob extends DailyJob
{
    public DailySyncJob()
    {

        schedule();
    }

    private void schedule()
    {
        //Job already scheduled, nothing to do
        if(!JobManager.instance().getAllJobRequestsForTag(Constant.TAG).isEmpty())
        { return; }

        JobRequest.Builder jobBuilder = new JobRequest.Builder(Constant.TAG);
        //Schedule between 12:00 AM and 01:00 AM
        DailyJob.schedule(jobBuilder,
                TimeUnit.HOURS.toMillis(0), TimeUnit.HOURS.toMillis(1));
    }

    /**
     * This method is invoked from a background thread. You should run your desired task here.
     * This method is thread safe. Each time a job starts, a new instance of your {@link Job}
     * is instantiated and executed. You can identify your {@link Job} with the passed
     * {@code params}.
     * <p>
     * You should call {@link #isCanceled()} frequently for long running jobs and stop your
     * task if necessary.
     * <p>
     * A {@link PowerManager.WakeLock} is acquired for 3 minutes for each {@link Job}.
     * If your task needs more time, then you need to create an extra
     * {@link PowerManager.WakeLock}.
     *
     * @param params The parameters for this concrete job.
     * @return The result of this {@link DailyJob}.
     */
    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                App.getInstance().getApplicationContext());
        IRepository.Preferences preferences = new PreferencesRepository(sharedPreferences);
        boolean finished = RefreshData.refresh(new WeatherRepository(), preferences);
        Log.i(Constant.TAG, "Job ran (SUCCESS) True (FAILED) False : " + finished);
        return (finished) ? DailyJobResult.SUCCESS : DailyJobResult.CANCEL;
    }
}
