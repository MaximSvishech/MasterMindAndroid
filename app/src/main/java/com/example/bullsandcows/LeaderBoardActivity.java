package com.example.bullsandcows;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bullsandcows.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    ListView leaderBoardListView;
    ArrayList<GameScore> highScores = new ArrayList<>();
    //GameScore dummyScore = new GameScore(1, "bart simpsoon", "uuuu55");
    ArrayAdapter<GameScore> scoresAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        leaderBoardListView = findViewById(R.id.leaderBoardListView);
        //highScores.add(dummyScore);
        scoresAdapter = new HighScoresAdapter(this, highScores);

        leaderBoardListView.setAdapter(scoresAdapter);
        DBUtils.fetchHighScores(highScores, this, scoresAdapter);
    }
}