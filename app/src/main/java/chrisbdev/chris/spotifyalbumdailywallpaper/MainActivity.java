package chrisbdev.chris.spotifyalbumdailywallpaper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.spotify.sdk.android.player.Player;




public class MainActivity extends Activity {




    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GenerateWallpaperActivity.class);
        startActivity(intent);
//        final Button button = (Button) findViewById(R.id.getNewBtn);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                getNewAlbumImage();
//            }
//        });
//        getNewAlbumImage();

//        setWallpaperChangeAlarm(getApplicationContext());
    }

//    private void setWallpaperChangeAlarm(Context context) {
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.setAction(Intent. "packagename.ACTION");
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        alarm.cancel(pendingIntent);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
