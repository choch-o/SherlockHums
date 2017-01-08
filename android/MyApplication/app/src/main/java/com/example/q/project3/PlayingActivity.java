package com.example.q.project3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-07.
 */

public class PlayingActivity extends Activity {
    int RECORDER = 1;
    int currentRecorder = 1;
    JSONArray tempSongs;
    String currTitle;
    String currArtist;
    int round = 0;
    // ArrayList<String> tempSongs = new ArrayList<>();

    /* Temp variables */
    boolean is_recorder = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playing);
        try {
            tempSongs = new JSONArray(
                    "[ {\"title\": \"Single lady\", \"artist\": \"Beyonce\"}, "
                    + "{\"title\": \"Tell me\", \"artist\": \"원더걸스\"}, "
                    + "{\"title\": \"보고싶다\", \"artist\": \"김범수\"}, "
                    + "{\"title\": \"나 항상 그대를\", \"artist\": \"이선희\"}, "
                    + "{\"title\": \"Hug\", \"artist\": \"동방신기\"}, "
                    + "{\"title\": \"Gee\", \"artist\": \"소녀시대\"}, "
                    + "{\"title\": \"무조건\", \"artist\": \"박상철\"}, "
                    + "{\"title\": \"Isn't she lovely\", \"artist\": \"Stevie Wonder\"} ]"
            ) ;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        startRound();
    }

    private void startRound() {
        int id = getResources().getIdentifier("player" + currentRecorder, "id", getPackageName());
        LinearLayout recorder = (LinearLayout) findViewById(id);
        recorder.setBackgroundColor(getResources().getColor(R.color.colorRecorder));
        try {
            Log.d("JSON Obj 0", tempSongs.getJSONObject(round).toString());
            currTitle = tempSongs.getJSONObject(round).getString("title");
            currArtist = tempSongs.getJSONObject(round).getString("artist");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (is_recorder) {
            Intent i = new Intent(PlayingActivity.this, RecordingActivity.class);
            i.putExtra("title", currTitle);
            i.putExtra("artist", currArtist);
            startActivityForResult(i, RECORDER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == RECORDER) {
            if (currentRecorder > 3 ) {
                currentRecorder = 1;
            } else {
                currentRecorder++;
            }
            // TEMP
            is_recorder = false;
        }
        */

    }
}
