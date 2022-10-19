package com.example.bullsandcows;

import android.content.Context;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GameScore {

    public Integer score;
    public String name;
    public String userID;


    public GameScore(Integer score, String name, String userID) {
        this.score = score;
        this.name = name;
        this.userID = userID;
    }

    public GameScore() {
        // Default constructor required for calls to DataSnapshot.getValue(GameScore.class)
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
       return  + score + " " + (score == 1 ? "Guess" : "Guesses") +
                ": " + name;
    }

}
