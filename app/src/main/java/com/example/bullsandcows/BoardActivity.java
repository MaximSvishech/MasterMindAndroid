package com.example.bullsandcows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.bullsandcows.utils.DBUtils;
import com.example.bullsandcows.utils.GameUtils;
import com.example.bullsandcows.utils.SoundFXPoolManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mrudultora.colorpicker.ColorPickerDialog;
import com.mrudultora.colorpicker.listeners.OnSelectColorListener;
import com.mrudultora.colorpicker.util.ColorItemShape;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.IntStream;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class BoardActivity extends AppCompatActivity {

    private final int DEFAULT_COLOR = 520093696;
    private int mTryNumber = 1;
    private int mNumOfGuesses;
    private final int mMaxNumOfGuesses = 10;
    private final RandomGenerator mRandomGenerator = new RandomGenerator();
    private Vector<String> mRandomChoiceList = new Vector<>(); // list contains the 4 secret colors
    private Vector<Button> mCurrentTurn = new Vector<>(); // ui current user guess row buttons
    private Vector<Button> mSecretButtons = new Vector<>(); // ui secret buttons
    private Vector<Button> mCurrentComputerFeedBack = new Vector<>(); // ui computer feed back buttons
    private final ArrayList<String> mColorList = GameUtils.getColors(); // list of available colors
    private int mNumOfColorsSelected = 0;
    private Button mArrowButton;
    private GameLogic mGame; // data member of the game logic
    private int pgiaSound, bulPgiaSound, youWonSound, youLostSound, activityStartSound, openDialogSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        chooseSecretNumbers();
        mSecretButtons = findSecretButtons();
        mCurrentTurn = findGuessButtons(mTryNumber);
        mNumOfGuesses = getIntent().getIntExtra("Num of Guesses", 4); //gets user choice from previous screen
        mGame = new GameLogic(mNumOfGuesses);
        prepareBoard();
        prepareSoundEffects();
        SoundFXPoolManager.playSound(activityStartSound);
        enableNextGuess();
    }

    private void prepareSoundEffects() {
        // choose individual sound resource for each in-game necessity
        openDialogSound = R.raw.long_pop;
        bulPgiaSound = R.raw.long_pop;
        pgiaSound = R.raw.pop;
        youWonSound = R.raw.tada;
        youLostSound = R.raw.negative_beeps;
        activityStartSound = R.raw.swoosh;
    }

    private void checkIfEnableArrowButton() { // only when 4 buttons are chosen, the arrow button is available
        mNumOfColorsSelected = 0;

        for (Button button : mCurrentTurn) {
            if (button.getBackgroundTintList().getDefaultColor() != DEFAULT_COLOR)
                mNumOfColorsSelected++;
        }

        if (mNumOfColorsSelected == 4) {
            int id = getResources().getIdentifier("try" + mTryNumber + "_go", "id", getPackageName());
            mArrowButton = findViewById(id);
            if (mArrowButton != null) {
                mArrowButton.setClickable(true);
                mArrowButton.setEnabled(true);
            }
        }
    }


    private void chooseSecretNumbers() {
        mRandomChoiceList = mRandomGenerator.generateRandomChoice();
    }

    public void calculateGuess(View v) {
        for (Button button : mCurrentTurn) {
            button.setAlpha(.5f);
            button.setClickable(false);
            button.setEnabled(false);
        }

        mGame.CountHits(mRandomChoiceList, mGame.getGameBoard().getUserGuesses());
        mGame.getGameBoard().addComputerFeedBack(mGame.getBulPgiaCounter(), mGame.getPgiaCounter(), mTryNumber);


        provideFeedbackForChoice();

        mArrowButton.setAlpha(.5f);
        mArrowButton.setClickable(false);
        mArrowButton.setEnabled(false);

        if (!mGame.IsWon() && mTryNumber <= mNumOfGuesses) {
            mGame.ResetHits();
            mTryNumber++;
            mCurrentTurn = findGuessButtons(mTryNumber);
            if (mTryNumber < (mMaxNumOfGuesses + 1)) {
                enableNextGuess();
            }
        }

        if (mGame.IsWon() || mTryNumber > mNumOfGuesses) {
            if (mGame.IsWon()) {
                // play sound FX / animation in separate threads
                Thread soundT = new Thread(() -> {
                    SoundFXPoolManager.playSound(youWonSound);
                    return;
                });
                Thread visualT = new Thread(() -> {
                    rainKonfetti();
                    return;
                });
                soundT.start();
                visualT.start();
                DBUtils.writeNewScore(mTryNumber);
            }
            else
                SoundFXPoolManager.playSound(youLostSound);


            IntStream.range(0, 4).forEach(i -> { //display correct choices if won or lost
                eColor trueColor = eColor.valueOf(mRandomChoiceList.elementAt(i));
                mSecretButtons.elementAt(i).setBackgroundColor(Color.parseColor(trueColor.getValue())); // reveal the answer
            });



            if (mGame.IsWon()) { // user won
                new MaterialAlertDialogBuilder(BoardActivity.this)
                        .setTitle(R.string.win_congrats_title)
                        .setMessage(formatScore(mTryNumber))
                        .setPositiveButton(R.string.play_again_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.back_to_main_menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

            if (mTryNumber > mNumOfGuesses) { //user is lost the game
                new MaterialAlertDialogBuilder(BoardActivity.this)
                        .setTitle(R.string.lost_game_title)
                        .setMessage(R.string.lost_game_message)
                        .setPositiveButton(R.string.play_again_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.back_to_main_menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    private String formatScore(Integer score) {
        int key = score == 1 ? R.string.score_one_guess : R.string.congrats_won_message;
        return String.format(getString(key), score);
    }


    private void enableNextGuess() {
        for (Button button : mCurrentTurn) {
            button.setEnabled(true);
            button.setClickable(true);
            button.animate().scaleX(1.2F).scaleY(1.05F).setDuration(300).withEndAction(() -> {
                button.animate().scaleX(1F).scaleX(1F);
            });
        }
    }

    private void provideFeedbackForChoice() {
        // provides feedback for the currently submitted choice
        // yellow circle for correct color in the wrong position
        // black circle for correct color in the correct position
        // each type of feedback has its own sound effect
        // the circles are colored and the sound fx are played consequentially,
        // with a small pause between each

        mCurrentComputerFeedBack = findComputerFeedBackButtons(mTryNumber);
        final int bulPgiaCounter = mGame.getBulPgiaCounter();
        final int pgiaCounter = mGame.getPgiaCounter();
        Thread t = new Thread(() -> {
            int mutableBulPgiaCounter = bulPgiaCounter;
            int mutablePgiaCounter = pgiaCounter;
            for (Button button : mCurrentComputerFeedBack) {
                // display each feedback consequentially
                if (mutableBulPgiaCounter > 0) {
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    if (bulPgiaCounter < 4)
                        SoundFXPoolManager.playSound(bulPgiaSound);
                    mutableBulPgiaCounter--;
                    try {
                        Thread.currentThread().sleep(100); // take a small pause until the next one
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (mutablePgiaCounter > 0) {
                    button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFF00")));
                    SoundFXPoolManager.playSound(pgiaSound);
                    mutablePgiaCounter--;
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }
            return;
        });
        t.start();

    }

    private void prepareBoard() { // show user the number of rows that he selected, no need to show all board

        for (int i = mMaxNumOfGuesses; i > mNumOfGuesses; i--) {
            int relativeLayoutId = getResources().getIdentifier("try" + i, "id", getPackageName());
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(relativeLayoutId);
            relativeLayout.setVisibility(RelativeLayout.GONE);
        }
    }

    public void OpenColorDialog(View v) { // color dialog picker, only unselected colors are available
        SoundFXPoolManager.playSound(openDialogSound);
        ArrayList colors = getColors(mCurrentTurn);

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this);   // Pass the context.
        colorPickerDialog.setColors(colors)
                .setColumns(4)                                // Default number of columns is 5.
                .setDefaultSelectedColor(1)        // By default no color is used.
                .setColorItemShape(ColorItemShape.CIRCLE)
                .setDialogTitle(getResources().getString(R.string.choose_color))
                .setOnSelectColorListener(new OnSelectColorListener() {
                    @Override
                    public void onColorSelected(int color, int position) {
                        v.setBackgroundTintList(ColorStateList.valueOf(color));
                        mGame.getGameBoard().addUserGuess(eColor.fromString(String.format("#%06X", (0xFFFFFF & color))), Integer.parseInt(v.getTag().toString()));
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

        for (Button button : buttons) {
            String color = String.format("#%06X", (0xFFFFFF & button.getBackgroundTintList().getDefaultColor()));
            if (colorToChoose.contains(color)) {
                colorToChoose.remove(color);
            }
        }

        return colorToChoose;
    }

    private Vector<Button> findGuessButtons(int tryNumber) {
        Vector<Button> guessButtons = new Vector<>();
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("try" + tryNumber + "_userGuesses" + i, "id", getPackageName());
            guessButtons.add((Button) findViewById(id));
        }

        return guessButtons;
    }

    private Vector<Button> findComputerFeedBackButtons(int tryNumber) {
        Vector<Button> computerFeedBackButtons = new Vector<>();
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("try" + tryNumber + "_feedback" + i, "id", getPackageName());
            computerFeedBackButtons.add((Button) findViewById(id));
        }

        return computerFeedBackButtons;
    }

    private Vector<Button> findSecretButtons() {
        Vector<Button> secretButtons = new Vector<>();
        for (int i = 1; i <= 4; i++) {
            int id = getResources().getIdentifier("secret" + i, "id", getPackageName());
            secretButtons.add((Button) findViewById(id));
        }

        return secretButtons;
    }

    private void rainKonfetti() { // set up and play confetti animation
        final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                .streamFor(300, 5000L);

    }
}