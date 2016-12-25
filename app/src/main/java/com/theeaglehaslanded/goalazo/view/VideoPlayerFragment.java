package com.theeaglehaslanded.goalazo.view;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.okhttp.Response;
import com.theeaglehaslanded.goalazo.BuildConfig;
import com.theeaglehaslanded.goalazo.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class VideoPlayerFragment extends Fragment {


    MediaController mediaControls;
    VideoView videoView;
    SmoothProgressBar progressBar;
    Response response;
    private int position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_player, container, false);
        videoView = (VideoView) view.findViewById(R.id.videoView1);
        progressBar = (SmoothProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.progressiveStart();
//        String var = getArguments().getString("URL");
//        String modVar = var.substring(var.lastIndexOf("/") + 1).trim();
//        String url = "https://cdn.streamable.com/video/mp4-mobile/" + modVar + ".mp4";
        String url = getArguments().getString(("URL")) + ".mp4";

        if (BuildConfig.DEBUG) {
            Log.d("MainActivity", url);
        }
        if (mediaControls == null) {
            mediaControls = new MediaController(getActivity());
        }

        //String proxyURL = proxy.getProxyUrl(url);
        videoView.setMediaController(mediaControls);
        //videoView.setVideoPath(proxyURL);
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.setZOrderOnTop(true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                videoView.seekTo(position);
                videoView.start();
                progressBar.progressiveStop();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(getActivity(),
                        getString(R.string.playback_error), Toast.LENGTH_LONG).show();
                return false;
            }
        });
        return view;
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        try {
            position = savedInstanceState.getInt("Position");
            videoView.seekTo(position);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", videoView.getCurrentPosition());
        videoView.pause();
    }


}
