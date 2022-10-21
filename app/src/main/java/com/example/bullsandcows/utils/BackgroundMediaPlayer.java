package com.example.bullsandcows.utils;
/* Manager class for playing game background music through Android media player.
   Uses singleton pattern to ensure a single media player throughout the app, so the music continues
   when changing between different activities.
 */

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.IOException;



public class BackgroundMediaPlayer {

    private static BackgroundMediaPlayer Instance;
    MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;

    public static BackgroundMediaPlayer getMediaPlayerInstance() {
        if (Instance == null) {
            return Instance = new BackgroundMediaPlayer(); //singleton init
        }
        return Instance;
    }

    public void playAudioFile(Context context, int sampleAudio, boolean playInLoop) {
        if (!isPlaying) {
            mediaPlayer = new MediaPlayer();
            Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + sampleAudio);
            try {
                mediaPlayer.setDataSource(context, mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //runs when media player is finished being prepared asynchronously
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setVolume(0.6F, 0.6F);
                    if (playInLoop)
                        mp.setLooping(true);
                    mp.start();
                    if (playInLoop)
                        mp.setLooping(true);
                    isPlaying = true;
                }
            });
            mediaPlayer.prepareAsync(); // prepare media player asynchronously to not block main thread
        }
    }

    //stop playing when necessary
    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
        }
    }}