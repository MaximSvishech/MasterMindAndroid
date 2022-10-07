package com.example.bullsandcows;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mrudultora.colorpicker.ColorPickerDialog;
import com.mrudultora.colorpicker.ColorPickerPopUp;
import com.mrudultora.colorpicker.listeners.OnSelectColorListener;
import com.mrudultora.colorpicker.util.ColorItemShape;

import java.security.PrivateKey;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private Button mPlayButton;
    private Button mInformationButton;
    private CircularLinkedList mNumOfChoices;
    private Node mCurrentChoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNumOfChoices = new CircularLinkedList();
        GameUtils.addChoices(mNumOfChoices);
        mCurrentChoice = mNumOfChoices.getHead();
        mPlayButton = findViewById(R.id.startButton);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGuessDialog();
            }
        });

        mInformationButton = findViewById(R.id.informationButton);
        mInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    private void showGuessDialog() {
        final Dialog guessDialog = new Dialog(MainActivity.this);
        guessDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        guessDialog.setContentView(R.layout.guesses_choice_dialog);

        final EditText numOfGuesses = guessDialog.findViewById(R.id.userGuesses);
        final Button plusButton = guessDialog.findViewById(R.id.addBtn);
        final Button minusButton = guessDialog.findViewById(R.id.removeBtn);
        final Button startGame = guessDialog.findViewById(R.id.playButton);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentChoice = mCurrentChoice.nextNode;
                numOfGuesses.setText(String.valueOf(mCurrentChoice.value));
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentChoice.value == 4){
                    mCurrentChoice = mNumOfChoices.containsNode(10);
                }
                else {
                    mCurrentChoice = mNumOfChoices.containsNode(mCurrentChoice.value - 1);
                }
                numOfGuesses.setText(String.valueOf(mCurrentChoice.value));
            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                intent.putExtra("Num of Guesses", mCurrentChoice.value);
                startActivity(intent);
            }
        });


        guessDialog.show();
    }
}

