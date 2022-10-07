package com.example.bullsandcows;

import java.util.Vector;

public class GameLogic {

    private Board mGameBoard;
    private int mBulPgiaCounter =0;
    private int mPgiaCounter = 0;
    private boolean mIsWon = false;


    public GameLogic(int numberOfGuesses)
    {
        mGameBoard = new Board(numberOfGuesses);
    }

    public int getBulPgiaCounter()
    {
          return mBulPgiaCounter;
    }

    public int getPgiaCounter()
    {
         return mPgiaCounter;
    }

    public boolean IsWon()
    {
         return mIsWon;
    }

    public Board getGameBoard()
    {
        return mGameBoard;
    }

    public void CountHits(Vector<String> randomChoice, String[] userInput)
    {
        int i = 0;

        for (String color : userInput)
        {
            if (randomChoice.contains(color))
            {
                if (randomChoice.elementAt(i) == color)
                {
                    mBulPgiaCounter++; // Bull Pgia
                }
                else
                {
                    mPgiaCounter++;
                }
            }

            i++;
        }

        if (mBulPgiaCounter == 4)
        {
                mIsWon = true;
        }
    }

    public void ResetHits()
    {
        mBulPgiaCounter = 0;
        mPgiaCounter = 0;
    }

}
