package com.example.bullsandcows;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bullsandcows.GameScore;
import com.example.bullsandcows.R;

import java.util.ArrayList;

public class HighScoresAdapter extends ArrayAdapter<GameScore> {

    ArrayList<GameScore> requests;
    public HighScoresAdapter(Context context, ArrayList<GameScore> requests)
    {
        super(context, 0, requests);
        this.requests = requests;
    }
    public String customLabel(GameScore score) {
        int key = score.getScore() == 1 ? R.string.score_one_guess : R.string.score_num_of_guesses;
        return score.getName() + ": " + String.format(getContext().getString(key), score.getScore());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        GameScore score = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.high_score, parent, false);
        }

        TextView textView = (TextView)
                convertView.findViewById(R.id.highScoreTextView);
        textView.setText(customLabel(score));

        return convertView;
    }

}