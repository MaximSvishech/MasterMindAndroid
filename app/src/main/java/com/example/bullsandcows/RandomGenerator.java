package com.example.bullsandcows;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class RandomGenerator {

    private final Random mRandom;

    private final int mNumberOfCharactersToGenerate = 4;

    public RandomGenerator(){
        mRandom = new Random();
    }

    public Vector<String> generateRandomChoice(){
        Vector<String> randomChoiceList = new Vector<String>();
        eColor[] values = eColor.values();
        eColor randomColor;

        for (int i = 0; i < mNumberOfCharactersToGenerate; i++){
            do {
                randomColor = eColor.randomLetter();
            }
            while (randomChoiceList.contains(randomColor.toString()));

            randomChoiceList.add(randomColor.toString());
        }

        return randomChoiceList;
    }
}
