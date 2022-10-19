package com.example.bullsandcows.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.bullsandcows.R;

import java.util.HashMap;

public class SoundFXPoolManager {
    private static SoundFXPoolManager Instance;
    private static SoundPool soundPool;

    private boolean isLoaded;
    private int[] soundResourceIDs;
    private static HashMap<Integer, Integer> ResourceIdToLoadedSoundIDs = new HashMap<>();

    private Context context;


    public static void instantiate(Context context) {
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
                .build();
        soundPool = new SoundPool.Builder().
                setAudioAttributes(audioAttrib).
                setMaxStreams(3).build();


        soundResourceIDs = new int[]{R.raw.pop, R.raw.long_pop, R.raw.tada, R.raw.negative_beeps,
                R.raw.sweep, R.raw.transition_short,  R.raw.swoosh };

        for (int resourceID : soundResourceIDs)
            ResourceIdToLoadedSoundIDs.put(resourceID,
                    soundPool.load(context, resourceID , 1));

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int mySoundId, int status) {
                isLoaded = true;
            }
        });
    }

    public static void playSound(int soundResourceID) {
        Integer soundID = ResourceIdToLoadedSoundIDs.get(soundResourceID);
        soundPool.play(soundID, 1, 1, 0, 0, 1);
    }
}
