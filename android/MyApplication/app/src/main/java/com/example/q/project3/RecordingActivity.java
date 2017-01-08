package com.example.q.project3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity {
    private WavRecorder recorder = new WavRecorder();
    private AudioPlayer player = new AudioPlayer();
    private ImageButton recordButton;
    private ImageButton playButton;
    private ImageButton searchButton;

    private TextView countdown;
    private ImageView recording;

    String outputFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        Intent i = getIntent();
        final TextView title = (TextView) findViewById(R.id.title);
        title.setText(i.getStringExtra("title"));
        final TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(i.getStringExtra("artist"));

        countdown = (TextView) findViewById(R.id.record_status);
        recording = (ImageView) findViewById(R.id.recording);
        recording.setVisibility(View.GONE);

        new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown.setText(Long.toString(millisUntilFinished / 1000));
            }
            public void onFinish() {
                recording.setVisibility(View.VISIBLE);
                countdown.setVisibility(View.GONE);
                startRecording();
            }
        }.start();

        /*
        recordStatus.setText("Start recording");
        */
        final TextView playStatus = (TextView) findViewById(R.id.play_status);
        playStatus.setText("Start playing");

        recordButton = (ImageButton) findViewById(R.id.record_button);
        playButton = (ImageButton) findViewById(R.id.play_button);
        searchButton = (ImageButton) findViewById(R.id.search_button);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.amr";
/*
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRecord(recorder.mStartRecording);
                if (recorder.mStartRecording) {
                    recordStatus.setText("Stop recording");
                    recordButton.setImageResource(R.drawable.stop_button);
                } else {
                    recordStatus.setText("Start recording");
                    recordButton.setImageResource(R.drawable.record_button);
                }
                recorder.mStartRecording = !recorder.mStartRecording;

            }
        });
        */

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(player.mStartPlaying);
                if (player.mStartPlaying) {
                    playStatus.setText("Stop playing");
                    playButton.setImageResource(R.drawable.stop_button);
                } else {
                    playStatus.setText("Start playing");
                    playButton.setImageResource(R.drawable.play_button);
                }
                player.mStartPlaying = !player.mStartPlaying;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.upload(outputFile);
            }
        });
    }

    private void onRecord(boolean start) {
        if (start) {
            recorder.startRecording(outputFile);

        } else {
            recorder.stopRecording(outputFile);
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            player.startPlaying(outputFile);
        } else {
            player.stopPlaying();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            // recorder.release();
            recorder = null;
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    void startRecording() {
        recorder.startRecording(outputFile);
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                recorder.stopRecording(outputFile);
                recorder.upload(outputFile);
                // Intent toPlaying = new Intent(RecordingActivity.this, PlayingActivity.class);
                // startActivity(toPlaying);
                finish();
            }
        }.start();
    }
}
