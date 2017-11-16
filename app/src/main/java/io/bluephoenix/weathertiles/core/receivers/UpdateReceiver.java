package io.bluephoenix.weathertiles.core.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.bluephoenix.weathertiles.core.services.UpdateService;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class UpdateReceiver extends BroadcastReceiver
{
    /**
     * Activates the update service when an alarm notification is
     * received. Note that all processing should be done in the service
     * as this has a 10 second maximum time allotted to it after which is
     * considered ANR.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context, UpdateService.class));
    }
}
