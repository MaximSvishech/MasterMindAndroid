package com.example.bullsandcows;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_ON_CREATE = 1;
    private static final int REQ_CODE_AFTER_SIGN_OUT = 2;
    private Button mPlayButton;
    private Button mInformationButton;
    private ImageButton mLanguageButton;
    private CircularLinkedList mNumOfChoices;
    private ImageButton mlogoutButton;
    private Node mCurrentChoice;
    private TextView mhelloText;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

        // check if user has already signed-in (due to firebase persistent credentials)
        if (FirebaseAuth.getInstance().getCurrentUser() == null) // no user is signed-in
            signIn(REQ_CODE_ON_CREATE);
        else {
            // user is signed-in
            showUserDetails(true);
        }
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
        mlogoutButton = findViewById(R.id.logoutButton);
        mlogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        mLanguageButton = findViewById(R.id.languageButton);
        mLanguageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();

            }
        });


    }


    public void signIn(int requestCode) {
        Intent signInIntent = AuthUI.getInstance().createSignInIntentBuilder().build();
        startActivityForResult(signInIntent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ON_CREATE) {
            if (resultCode == RESULT_OK) {
                showUserDetails(true);
            } else {
                showUserDetails(false);
                finish();
            }
        }
    }

    public void signOut() {
        Task<Void> signOutTask = AuthUI.getInstance().signOut(this);
        signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,
                        "Signed-out successfully", Toast.LENGTH_LONG).show();
                signIn(REQ_CODE_AFTER_SIGN_OUT);
            }
        });
        signOutTask.addOnCompleteListener((aTask) -> {
            Toast.makeText(MainActivity.this, R.string.log_out, Toast.LENGTH_LONG).show();
            signIn(REQ_CODE_AFTER_SIGN_OUT);
        });
    }


    @SuppressLint("SetTextI18n")
    public void showUserDetails(boolean success) {
        if (success) {
            mhelloText = (TextView) findViewById(R.id.helloText);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            assert currentUser != null;
            String userDetails = "Display name = " + currentUser.getDisplayName() +
                    ", ID = " + currentUser.getUid() + ", Provider = " + currentUser.getProviderId();
            mhelloText.setText(Objects.requireNonNull(currentUser.getDisplayName()).split("\\s")[0]);
        } else {
            Toast.makeText(this, R.string.terminate_msg, Toast.LENGTH_LONG).show();
        }
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
                if (mCurrentChoice.value == 4) {
                    mCurrentChoice = mNumOfChoices.containsNode(10);
                } else {
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

    public void showLanguageDialog() {
        final Dialog languageDialog = new Dialog(MainActivity.this);
        languageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        languageDialog.setContentView(R.layout.langueage_dialog);

        final ImageButton englishButton = languageDialog.findViewById(R.id.englishButton);
        final ImageButton hebrewButton = languageDialog.findViewById(R.id.hebrewButton);

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                recreate();
                languageDialog.dismiss();
            }
        });
        hebrewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("iw");
                recreate();
                languageDialog.dismiss();
            }
        });
        languageDialog.show();

    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared Prefernces
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", language);
        editor.apply();

    }

    // load language saved in shared prefernces
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "iw-rlL");
        setLocale(language);
    }
}
