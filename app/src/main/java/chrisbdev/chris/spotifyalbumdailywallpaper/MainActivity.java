package chrisbdev.chris.spotifyalbumdailywallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import  kaaes.spotify.webapi.android.models.SavedTrack;
import  kaaes.spotify.webapi.android.models.Pager;
import  kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import retrofit.client.Response;

public class MainActivity extends Activity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "d0cdbe278ea2444fa06e8c34aa5b2f6d";
    private static final String REDIRECT_URI = "spotify-album-daily-wallpaper-login://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private ImageView imageView;

    private Player mPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) this.findViewById(R.id.albumImageView);

        final Button button = (Button) findViewById(R.id.getNewBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getNewAlbumImage();
            }
        });


        getNewAlbumImage();
    }

    private class AlbumArtGetter extends AsyncTask<String, Void, String> {

        private ImageView imageViewToChange;
        private String albumUrlToGet;
        private Bitmap albumImg;
        private Context appContext;

        public AlbumArtGetter(String albumUrl, ImageView imageView, Context context) {
            imageViewToChange = imageView;
            albumUrlToGet = albumUrl;
            appContext = context;
        }

        private Bitmap getBitmapFromURL(String src) {
            try {
                java.net.URL url = new java.net.URL(src);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected String doInBackground(String[] params) {
            albumImg = getBitmapFromURL(albumUrlToGet);
            return "";
        }

        @Override
        protected void onPostExecute(String message) {
            imageViewToChange.setImageBitmap(albumImg);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            int width = metrics.widthPixels;
            int height = metrics.heightPixels;

//            boolean isPortrait = width < height;
//            if (isPortrait) { //Scale by width
//                double scale = width / albumImg.getWidth();
//                height = (int)(albumImg.getHeight() * scale);
//            } else { //Scale by height
//                double scale = height / albumImg.getHeight();
//                width = (int)(albumImg.getWidth() * scale);
//            }


            Bitmap scaledBitmap = Bitmap.createScaledBitmap(albumImg, width, height, true);


            try {
                WallpaperManager.getInstance(appContext).setBitmap(scaledBitmap);
            } catch (IOException e) {

            }

        }
    }

    private void getNewAlbumImage() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "user-library-read", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        SpotifyApi api = new SpotifyApi();

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                api.setAccessToken(response.getAccessToken());
                final SpotifyService spotify = api.getService();

                spotify.getMySavedTracks(new SpotifyCallback<Pager<SavedTrack>>() {
                    @Override
                    public void success(Pager<SavedTrack> savedTrackPager, Response response) {
                        int totalSavedTracks = savedTrackPager.total;

                        Random r = new Random();
                        int trackToPick = r.nextInt(totalSavedTracks - 0);


                        //Repeat the request now we know the users total saved tracks.
                        Map<String, Object> options = new HashMap<String, Object>();
                        options.put("limit", 1);
                        options.put("offset", trackToPick);

                        spotify.getMySavedTracks(options, new SpotifyCallback<Pager<SavedTrack>>() {

                            @Override
                            public void success(Pager<SavedTrack> innerSavedTracks, Response res) {
                                if (innerSavedTracks.items.size() > 0) {
                                    Image albumName = innerSavedTracks.items.get(0).track.album.images.get(0);
                                    String albumUrl = albumName.url;
                                    AlbumArtGetter artGetter = new AlbumArtGetter(albumUrl, imageView, getBaseContext());
                                    artGetter.execute();
                                }
                            }

                            @Override
                            public void failure(SpotifyError errorInner) {
                                // handle error
                            }

                        });


                    }

                    @Override
                    public void failure(SpotifyError error) {
                        // handle error
                    }
                });

            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
