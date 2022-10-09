package com.example.bullsandcows;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_ON_CREATE = 1;
    private static final int REQ_CODE_AFTER_SIGN_OUT = 2;
    private Button mPlayButton;
    private Button mInformationButton;
    private CircularLinkedList mNumOfChoices;
    private Node mCurrentChoice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    }

    public void signIn(int requestCode) {
        Intent signInIntent = AuthUI.getInstance().createSignInIntentBuilder().build();
        startActivityForResult(signInIntent, requestCode);
    }

    /**
     * This method is invoked automatically after the other activity was finished successfully
     * or terminated with failure.
     *
     * @param requestCode that was originally passed to startActivityForResult method
     * @param resultCode  how did the activity finished
     * @param data        the finished activity can return data to the invoking activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_ON_CREATE) {
            if (resultCode == RESULT_OK) {
                // user is logged-in
                showUserDetails(true);
                enterChat();
            } else {
                // user did not log-in. if MainActivity closes, the application will close
                showUserDetails(false);
                finish();
            }
        }
    }

    private void enterChat() {

    }

    /**
     * when signing-out from firebase, the procedure will happen sometime
     * in the future. We need to somehow signal our app upon completion
     * We will use a CompletionListener in order to run our code only when
     * sign-out is completed successfully
     */
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
            Toast.makeText(MainActivity.this, "Signed-out successfully", Toast.LENGTH_LONG).show();
            signIn(REQ_CODE_AFTER_SIGN_OUT);
        });
    }

    /**
     * This method shows a pop-up on MainActivity with either the user details in case of
     * successful login, or a termination message in case a user did not login/signed-up
     * to the application
     *
     * @param success
     */
    public void showUserDetails(boolean success) {
        if (success) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String userDetails = "Display name = " + currentUser.getDisplayName() +
                    ", ID = " + currentUser.getUid() + ", Provider = " + currentUser.getProviderId();
            Toast.makeText(this, userDetails, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.terminate_msg, Toast.LENGTH_LONG).show();
        }
    }

    // ClickHandler for our button
    public void invokeSecondActivity(View view) {
        Intent openSecondActivity = new Intent(MainActivity.this, secondActivity.class);
        startActivity(openSecondActivity);


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
}
