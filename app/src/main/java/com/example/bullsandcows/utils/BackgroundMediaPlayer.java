package com.example.bullsandcows.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.bullsandcows.R;

import java.io.IOException;

public class BackgroundMediaPlayer {

    private static BackgroundMediaPlayer Instance;
    MediaPlayer mediaPlayer;

    public static BackgroundMediaPlayer getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new BackgroundMediaPlayer();
        }
        return Instance;
    }

    public void playAudioFile(Context context, int sampleAudio, boolean playInLoop) {
        mediaPlayer = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + sampleAudio);
        try {
            mediaPlayer.setDataSource(context, mediaPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0.6F, 0.6F);
                if (playInLoop)
                    mp.setLooping(true);
                mp.start();
                if (playInLoop)
                    mp.setLooping(true);
            }
        });
        mediaPlayer.prepareAsync(); // prepare async to not block main thread
    }

    public void stopAudioFile() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }}