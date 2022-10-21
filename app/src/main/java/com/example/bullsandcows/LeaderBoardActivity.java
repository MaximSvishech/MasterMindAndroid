package com.example.bullsandcows;
/* Displays Leaderboard */
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bullsandcows.utils.DBUtils;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {
    ListView leaderBoardListView;
    ArrayList<GameScore> highScores = new ArrayList<>();
    ArrayAdapter<GameScore> scoresAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        leaderBoardListView = findViewById(R.id.leaderBoardListView);
        scoresAdapter = new HighScoresAdapter(this, highScores);
        //ListView is be backed by
        // a list of scores via the HighScoresAdapter
        leaderBoardListView.setAdapter(scoresAdapter);
        DBUtils.fetchHighScores(highScores, this, scoresAdapter); //fetch from DB into adapter
    }
}