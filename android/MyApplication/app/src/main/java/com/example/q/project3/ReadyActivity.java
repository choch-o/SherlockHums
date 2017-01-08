package com.example.q.project3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ReadyActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int PERMISSIONS_REQUEST_INTERNET = 3;

    ArrayList<String[]> ranks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ready);

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            if (this.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            if (this.checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_INTERNET);
        }

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

        LV_ranking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

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
