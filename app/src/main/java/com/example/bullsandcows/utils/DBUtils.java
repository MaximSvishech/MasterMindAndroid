package com.example.bullsandcows.utils;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bullsandcows.GameScore;
import com.example.bullsandcows.LeaderBoardActivity;
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

    public static void writeNewScore(int scoreNum) {
        assert currentUser != null;
        GameScore score = new GameScore(scoreNum, currentUser.getDisplayName(), currentUser.getUid());
        //Save score asynchronously
        Thread t = new Thread(()-> mDatabase.child("scores").push().setValue(score));
        t.start();

    }

    public static void fetchHighScores(List<GameScore> highScores,
                                       AppCompatActivity context,
                                       ArrayAdapter adapter) {
        DatabaseReference queryLocation =
                mDatabase.child("scores");
        Query query = queryLocation.orderByChild("score").startAt(1).limitToFirst(10);
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

                Map<Integer, Long> scoresCountMap = scores.stream().collect(Collectors.groupingBy(GameScore::getScore, Collectors.counting()));
                context.onStatsObtained(scoresCountMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.could_not_fetch_scores), Toast.LENGTH_SHORT).show();
            }
        });
    }
}