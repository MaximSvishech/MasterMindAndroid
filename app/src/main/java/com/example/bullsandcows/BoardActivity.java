package com.example.bullsandcows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.bullsandcows.utils.DBUtils;
import com.example.bullsandcows.utils.GameUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mrudultora.colorpicker.ColorPickerDialog;
import com.mrudultora.colorpicker.listeners.OnSelectColorListener;
import com.mrudultora.colorpicker.util.ColorItemShape;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.IntStream;

public class BoardActivity extends AppCompatActivity {

    private final int DEFAULT_COLOR = 520093696;
    private int mTryNumber = 1;
    private int mNumOfGuesses;
    private final int mMaxNumOfGuesses = 10;
    private final RandomGenerator mRandomGenerator = new RandomGenerator();
    private Vector<String> mRandomChoiceList = new Vector<>();
    private Vector<Button> mCurrentTurn = new Vector<>();
    private Vector<Button> mSecretButtons = new Vector<>();
    private Vector<Button> mCurrentComputerFeedBack = new Vector<>();
    private final ArrayList<String> mColorList = GameUtils.getColors();
    private int mNumOfColorsSelected = 0;
    private Button mArrowButton;
    private GameLogic mGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        chooseSecretNumbers();
        mSecretButtons = findSecretButtons();
        mCurrentTurn = findGuessButtons(mTryNumber);
        mNumOfGuesses = getIntent().getIntExtra("Num of Guesses", 4);
        mGame = new GameLogic(mNumOfGuesses);
        prepareBoard();
    }

    private void checkIfEnableArrowButton() {
        mNumOfColorsSelected = 0;

        for (Button button : mCurrentTurn) {
            if (button.getBackgroundTintList().getDefaultColor() != DEFAULT_COLOR)
                mNumOfColorsSelected++;
        }

            if (mNumOfColorsSelected == 4) {
                int id = getResources().getIdentifier("try" + mTryNumber + "_go", "id", getPackageName());
                mArrowButton = findViewById(id);
                if (mArrowButton != null){
                    mArrowButton.setClickable(true);
                    mArrowButton.setEnabled(true);
                }
            }
        }


    private void chooseSecretNumbers() {
        mRandomChoiceList = mRandomGenerator.generateRandomChoice();
    }

    public void calculateGuess(View v){
        DBUtils.writeNewScore(mTryNumber);
        for (Button button : mCurrentTurn) {
            button.setAlpha(.5f);
            button.setClickable(false);
            button.setEnabled(false);
        }

        mGame.CountHits(mRandomChoiceList, mGame.getGameBoard().getUserGuesses());
        mGame.getGameBoard().addComputerFeedBack(mGame.getBulPgiaCounter(),mGame.getPgiaCounter(),mTryNumber);
        colorComputerChoiceButtons();
        mArrowButton.setAlpha(.5f);
        mArrowButton.setClickable(false);
        mArrowButton.setEnabled(false);
        if (!mGame.IsWon() && mTryNumber < mNumOfGuesses)
        {
            mGame.ResetHits();
            mTryNumber++;
            mCurrentTurn = findGuessButtons(mTryNumber);
            enableNextGuess();
        }
        if (mGame.IsWon())
        {
            IntStream.range(0, 4).forEach(i -> {
                eColor trueColor = eColor.valueOf(mRandomChoiceList.elementAt(i));
                mSecretButtons.elementAt(i).setBackgroundColor(Color.parseColor(trueColor.getValue()));
            });

            DBUtils.writeNewScore(mTryNumber);
        }
    }

    private void enableNextGuess() {
        for (Button button : mCurrentTurn) {
            button.setEnabled(true);
            button.setClickable(true);
        }
    }

    private void colorComputerChoiceButtons() {
        mCurrentComputerFeedBack = findComputerFeedBackButtons(mTryNumber);
        int bulPgiaCounter = mGame.getBulPgiaCounter();
        int pgiaCounter = mGame.getPgiaCounter();

        for (Button button: mCurrentComputerFeedBack)
        {
            if (bulPgiaCounter > 0)
            {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                bulPgiaCounter--;
                continue;
            }
            if (pgiaCounter > 0)
            {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFF00")));
                pgiaCounter--;
            }
        }

    }

    private void prepareBoard() {

        for (int i = mMaxNumOfGuesses; i > mNumOfGuesses ; i--)
        {
            int relativeLayoutId = getResources().getIdentifier("try"+i, "id", getPackageName());
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(relativeLayoutId);
            relativeLayout.setVisibility(RelativeLayout.GONE);
        }
    }

    public void OpenColorDialog(View v) {
        ArrayList colors = getColors(mCurrentTurn);

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this);   // Pass the context.
        colorPickerDialog.setColors(colors)
                .setColumns(4)                        		// Default number of columns is 5.
                .setDefaultSelectedColor(1)		// By default no color is used.
                .setColorItemShape(ColorItemShape.CIRCLE)
                .setOnSelectColorListener(new OnSelectColorListener() {
                    @Override
                    public void onColorSelected(int color, int position) {
                        v.setBackgroundTintList(ColorStateList.valueOf(color));
                        mGame.getGameBoard().addUserGuess(eColor.fromString(String.format("#%06X", (0xFFFFFF & color))) , Integer.parseInt(v.getTag().toString()));
                        checkIfEnableArrowButton();
                    }

                    @Override
                    public void cancel() {
                        colorPickerDialog.dismissDialog(); // Dismiss the dialog.
                        checkIfEnableArrowButton();
                    }
                })
                .show();
    }

    private ArrayList getColors(Vector<Button> buttons) {

        ArrayList<String> colorToChoose = (ArrayList<String>) mColorList.clone();

        for (Button button : buttons){
            String color = String.format("#%06X", (0xFFFFFF & button.getBackgroundTintList().getDefaultColor()));
            if (colorToChoose.contains(color)){
                colorToChoose.remove(color);
            }
        }

        return colorToChoose;
    }

    private Vector<Button> findGuessButtons(int tryNumber) {
        Vector<Button> guessButtons = new Vector<>();
        for (int i = 1; i <= 4; i++)
        {
            int id = getResources().getIdentifier("try" + tryNumber + "_userGuesses" + i, "id", getPackageName());
            guessButtons.add((Button) findViewById(id));
        }

        return guessButtons;
    }

    private Vector<Button> findComputerFeedBackButtons(int tryNumber) {
        Vector<Button> computerFeedBackButtons = new Vector<>();
        for (int i = 1; i <= 4; i++)
        {
            int id = getResources().getIdentifier("try" + tryNumber + "_feedback" + i, "id", getPackageName());
            computerFeedBackButtons.add((Button) findViewById(id));
        }

        return computerFeedBackButtons;
    }

    private Vector<Button> findSecretButtons() {
        Vector<Button> secretButtons = new Vector<>();
        for (int i = 1; i <= 4; i++)
        {
            int id = getResources().getIdentifier("secret" + i, "id", getPackageName());
            secretButtons.add((Button) findViewById(id));
        }

        return secretButtons;
    }
}