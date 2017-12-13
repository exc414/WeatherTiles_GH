package io.bluephoenix.weathertiles.core.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobRequest;

import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez
 */
public class SyncJobCreator implements JobCreator
{
    /**
     * Map the {@code tag} to a {@code Job}. If you return {@code null}, then other
     * {@code JobCreator} get the chance to create a {@code Job} for this tag. If no
     * job is created at all, then it's assumed that job failed. This method is called
     * on a background thread right before the job runs.
     *
     * @param tag The tag from the {@link JobRequest} which you passed in the constructor
     *            of the {@link JobRequest.Builder} class.
     * @return    A new {@link Job} instance for this tag. If you return {@code null},
     *            then the job failed and isn't rescheduled.
     * @see JobRequest.Builder#Builder(String)
     */
    @Override
    @Nullable
    public Job create(@NonNull String tag)
    {
        switch(tag)
        {
            case Constant.TAG: return new DailySyncJob();
            default: return null;
        }
    }
}
