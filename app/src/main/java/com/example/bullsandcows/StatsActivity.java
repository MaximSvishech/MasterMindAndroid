package com.example.bullsandcows;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullsandcows.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {
    public class Item {
        private String name;
        private double value;

        public String getName() {
            return name;
        }

        public double getValue() {
            return value;
        }

        public Item(String name, double value) {
            this.name = name;
            this.value = value;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        DBUtils.fetchMyStats(this);
    }

    public void onStatsObtained(Map<Integer, Long> statsItems) {
        BarChartLayout barChart = new BarChartLayout(this);
        ViewGroup vg = (ViewGroup)(this.findViewById(R.id.stats_layout));
        vg.addView(barChart);

        statsItems.forEach((score, count) -> {
            TextView nameView = new TextView(this);
            nameView.setText(formatScore(score));
            nameView.setTypeface(nameView.getTypeface(), Typeface.BOLD);
            nameView.setPadding(8,8,8,8);
            View barView = new View(this);
            barView.setBackgroundColor(Color.rgb(116, 197, 237));
            barView.setPadding(8,8,8,8);

            barView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(StatsActivity.this, formatScore(score), Toast.LENGTH_SHORT).show();
                }
            });
            TextView labelView = new TextView(this);
            labelView.setText(String.valueOf(count));
            labelView.setTypeface(nameView.getTypeface(), Typeface.BOLD);
            labelView.setPadding(8,8,18,8);
            Bar bar = new Bar(nameView, barView, labelView, count.doubleValue());
            barChart.add(bar);
        });
    }
    private String formatScore(Integer score) {
        int key = score == 1 ? R.string.score_one_guess : R.string.score_num_of_guesses;
        return String.format(getString(key), score);
    }
}