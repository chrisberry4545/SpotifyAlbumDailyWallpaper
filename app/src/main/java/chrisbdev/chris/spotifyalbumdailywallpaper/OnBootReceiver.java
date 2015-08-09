package chrisbdev.chris.spotifyalbumdailywallpaper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Chris on 09/08/2015.
 */

public class OnBootReceiver extends BroadcastReceiver {
    public static void setAlarm(Context ctxt) {
        AlarmManager mgr=(AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);
        Calendar cal=Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis()<System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        mgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                getPendingIntent(ctxt));
    }

    public static void cancelAlarm(Context ctxt) {
        AlarmManager mgr=(AlarmManager)ctxt.getSystemService(Context.ALARM_SERVICE);

        mgr.cancel(getPendingIntent(ctxt));
    }

    private static PendingIntent getPendingIntent(Context ctxt) {
        Intent i=new Intent(ctxt, OnAlarmReceiver.class);

        return(PendingIntent.getBroadcast(ctxt, 0, i, 0));
    }

    @Override
    public void onReceive(Context ctxt, Intent intent) {
        setAlarm(ctxt);
    }
}