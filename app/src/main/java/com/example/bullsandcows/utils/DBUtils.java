package com.example.bullsandcows.utils;

/* Utility class for managing connection to Firebase Realtime DB
Allows writing a new game score do DB asynchronously, later to be used for statistics/leaderboard,
and fetching current user's gameplay statistics.
 */

import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bullsandcows.GameScore;
import com.example.bullsandcows.R;
import com.example.bullsandcows.StatsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DBUtils {
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = auth.getCurrentUser();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    //Write a new score to DB. ScoreNum is passed from outside, and user name/id are fetched from
    //Firebase Auth
    public static void writeNewScore(int scoreNum) {
        assert currentUser != null;
        GameScore score = new GameScore(scoreNum, currentUser.getDisplayName(), currentUser.getUid());
        //Save score asynchronously
        Thread t = new Thread(()-> mDatabase.child("scores").push().setValue(score));
        t.start();

    }

    //Loads current highscores into the provided highScores list, and notifies the provided ArrayAdapter
    //that changes have been made to the scores List which backs it
    public static void fetchHighScores(List<GameScore> highScores,
                                       AppCompatActivity context,
                                       ArrayAdapter adapter) {
        DatabaseReference queryLocation =
                mDatabase.child("scores");
        Query query = queryLocation.orderByChild("score").startAt(1).limitToFirst(10); //load top 10
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    GameScore score = scoreSnapshot.getValue(GameScore.class);
                    highScores.add(score);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.could_not_fetch_scores), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Fetches current user's gameplay stats. returns grouped results in a map (key is number of
    // guesses when won, value is the number of games won with this amount of guesses
    // e.g. 4 guesses -> 20 games won, 5 guesses -> 18 games won, etc.
    public static void fetchMyStats(StatsActivity context) {
        List<GameScore> scores = new ArrayList<>();
        DatabaseReference queryLocation =
                mDatabase.child("scores");
        Query query = queryLocation.orderByChild("userID").equalTo(currentUser.getUid());
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    GameScore score = scoreSnapshot.getValue(GameScore.class);
                    scores.add(score);
                }
                //perform grouping
                Map<Integer, Long> scoresCountMap = scores.stream().collect(Collectors.groupingBy(GameScore::getScore, Collectors.counting()));
                context.onStatsObtained(scoresCountMap); //notify the activity new data is available
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.could_not_fetch_scores), Toast.LENGTH_SHORT).show();
            }
        });
    }
}