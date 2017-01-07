package com.example.q.project3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ReadyActivity extends AppCompatActivity {

    ArrayList<String[]> ranks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ready);

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReadyActivity.this, PlayingActivity.class);
                startActivity(i);
            }
        });

        createTestRanks();

        ListView LV_ranking = (ListView) findViewById(R.id.listViewRanking);
        RankingListViewAdapter rankingListViewAdapter = new RankingListViewAdapter(getApplication(), ranks);
        LV_ranking.setAdapter(rankingListViewAdapter);

    }

    public void createTestRanks() {
        String[] data;
        data = new String[4];
        data[0] = "1";
        data[2] = "Youngsoo Jang";
        data[3] = "54321";
        ranks.add(data);
        data = new String[4];
        data[0] = Integer.toString(2);
        data[2] = "Hyunsung Cho";
        data[3] = Integer.toString(4321);
        ranks.add(data);
        for(int i = 3; i < 20; i++) {
            data = new String[4];
            data[0] = Integer.toString(i);
            data[2] = "Player" + Integer.toString(i);
            data[3] = Integer.toString(i * 100);
            ranks.add(data);
        }
    }

}
