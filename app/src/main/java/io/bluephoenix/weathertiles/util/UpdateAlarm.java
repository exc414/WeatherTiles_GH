package io.bluephoenix.weathertiles.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import io.bluephoenix.weathertiles.app.App;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class UpdateAlarm
{
    private static Intent intent = new Intent("io.bluephoenix.weathertiles.UPDATE_TILE_ALARM");

    private static AlarmManager alarmManager = (AlarmManager)
            App.getInstance().getSystemService(Context.ALARM_SERVICE);

    public static void createFiveDayForecastAlarm(long alarmTime)
    {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                App.getInstance(), Constant.PENDING_INTENT_ID_1DAY, intent, 0);

        //1st - Real Time; 2nd - Time for alarm to execute; 3rd - repeating interval
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmTime, AlarmManager.INTERVAL_HOUR * 24, pendingIntent);
    }

    public static void deleteAlarm(int pendingIntentId)
    {
        AlarmManager alarmManager = (AlarmManager)
                App.getInstance().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                App.getInstance(), pendingIntentId, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
