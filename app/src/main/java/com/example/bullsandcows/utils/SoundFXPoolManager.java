package com.example.bullsandcows.utils;

/*
 Manager class for sound effects SoundPool. For long audio clips (like background music) a MediaPlayer
 is generally used, but for short clips (like sound effects) SoundPool is a more efficient option.
 Uses a variation on the Singleton pattern.
 Sounds loading is separated from sounds playing in order to allow for preloading
 in the main activity.


*/

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.bullsandcows.R;

import java.util.HashMap;

public class SoundFXPoolManager {
    private static SoundFXPoolManager Instance;
    private static SoundPool soundPool;

    private static boolean isLoaded = false;
    private int[] soundResourceIDs;
    private static HashMap<Integer, Integer> ResourceIdToLoadedSoundIDs = new HashMap<>();

    private Context context;


    public static void instantiate(Context context) { //instantiate Singleton
        if (Instance == null)
            Instance = new SoundFXPoolManager(context);
    }

    public SoundFXPoolManager(Context context) {
        this.context = context;
        isLoaded = false;
        prepareSoundPool();
    }

    private void prepareSoundPool() {
        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(); //various audio playback attributes
        soundPool = new SoundPool.Builder().
                setAudioAttributes(audioAttrib).
                setMaxStreams(3).build();


        soundResourceIDs = new int[]{R.raw.pop, R.raw.long_pop, R.raw.tada, R.raw.negative_beeps,
                R.raw.sweep, R.raw.transition_short,  R.raw.swoosh }; //sounds to be used

        for (int resourceID : soundResourceIDs)
            ResourceIdToLoadedSoundIDs.put(resourceID,
                    soundPool.load(context, resourceID , 1)); //preload sounds

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int mySoundId, int status) {
                isLoaded = true; //sounds are now loaded and playable
            }
        });
    }

    public static void playSound(int soundResourceID) {
        Integer soundID = ResourceIdToLoadedSoundIDs.get(soundResourceID);
        if (isLoaded)
            soundPool.play(soundID, 1, 1, 0, 0, 1);
    }
}
