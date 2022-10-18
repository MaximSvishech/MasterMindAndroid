package com.example.bullsandcows;

public class Board {
    private final String[] mUserGuessesArray;
    private final String[] mGameFeedBackArray;

    public Board(int numOfGuesses)
    {
        mUserGuessesArray = new String[numOfGuesses];
        mGameFeedBackArray = new String[numOfGuesses];
    }

    public String[] getUserGuesses()
    {
          return mUserGuessesArray;
    }

    public void addUserGuess(String userGuess, int index)
    {
        mUserGuessesArray[index] = userGuess;
    }

    public void addComputerFeedBack(int bulPgia, int pgia, int index) // calculates user guess: "v" means bulls eye, "x" means hit
    {
        for (int i = 0; i < bulPgia; i++)
        {
            mGameFeedBackArray[index - 1] += "V";
        }

        for (int i = 0; i < pgia; i++)
        {
            mGameFeedBackArray[index - 1] += "X";
        }
    }

}
