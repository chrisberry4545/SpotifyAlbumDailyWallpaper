package chrisbdev.chris.spotifyalbumdailywallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Chris on 09/08/2015.
 */

public class OnAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctxt, Intent intent) {
        Intent i = new Intent(ctxt, GenerateWallpaperActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ctxt.startActivity(i);
    }
}
