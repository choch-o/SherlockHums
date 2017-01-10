package com.example.q.project3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity {
    private WavRecorder recorder = new WavRecorder();
    private AudioPlayer player = new AudioPlayer();
    private ImageButton recordButton;
    private ImageButton playButton;
    private ImageButton searchButton;

    private TextView countdown;
    private ImageView recording;

    private ProgressBar progressBar;

    public static String next_midi;

    String outputFile = "";
    String from_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        Intent i = getIntent();
        final TextView title = (TextView) findViewById(R.id.title);
        title.setText(i.getStringExtra("title"));
        final TextView artist = (TextView) findViewById(R.id.artist);
        artist.setText(i.getStringExtra("artist"));
        from_activity = i.getStringExtra("from");
        countdown = (TextView) findViewById(R.id.record_status);
        recording = (ImageView) findViewById(R.id.recording);
        recording.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        new CountDownTimer(6000, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown.setText(Long.toString(millisUntilFinished / 1000));
            }
            public void onFinish() {
                recording.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                countdown.setVisibility(View.GONE);
                startRecording();
            }
        }.start();

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.amr";

    }

    void startRecording() {
        recorder.startRecording(outputFile);
        new CountDownTimer(10000, 500) {
            public void onTick(long millisUntilFinished) {
                progressBar.incrementProgressBy(1);
            }
            public void onFinish() {
                recorder.stopRecording(outputFile);
                String midi_path = recorder.upload(outputFile);
                Log.d("RECORDING_MIDIPATH", midi_path);
                if (from_activity.equals("ReadyActivity")) {
                    Intent i = new Intent(RecordingActivity.this, PlayingActivity.class);
                    i.putExtra("midi_path", midi_path);
                    i.putExtra("is_recorder", true);
                    startActivity(i);
                }
                else {
                    next_midi = midi_path;
                    finish();
                }
            }
        }.start();
    }
}
