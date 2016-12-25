package com.theeaglehaslanded.goalazo.view;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.theeaglehaslanded.goalazo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity implements EasyVideoCallback {

    private Bundle extras;
    @Bind(R.id.video_player)
    EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_activity);
        ButterKnife.bind(this);
        FullScreencall();
        player.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        extras = getIntent().getExtras();
//        String url = extras.getString("URL") + ".mp4";
        String url = extras.getString("URL");
        url = url + ".mp4";
//        String modVar = url.substring(url.lastIndexOf("/") + 1).trim();
//        url = "https://streamable.com/" + modVar;
        Log.d("MainActivity", url);
        player.setSource(Uri.parse(url));
//        player.start();
        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.
    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    // Methods for the implemented EasyVideoCallback

    @Override
    public void onPreparing(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        // TODO handle

    }

    @Override
    public void onBuffering(int percent) {
        player.setBottomLabelText("Buffering (" + Integer.toString(percent) + "%)");
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        String url = extras.getString("URL");
        String modVar = url.substring(url.lastIndexOf("/") + 1).trim();
        url = "https://streamable.com/" + modVar;
        url = url.replace("mp4-mobile", "mp4");
        player.setSource(Uri.parse(url));
        Log.d("MainActivity", "ERROR " + url);
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        // TODO handle if needed
        player.release();
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        // TODO handle if needed
    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}