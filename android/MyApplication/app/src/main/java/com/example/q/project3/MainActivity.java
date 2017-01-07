package com.example.q.project3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int PERMISSIONS_REQUEST_INTERNET = 3;
    private WavRecorder recorder = new WavRecorder();
    private AudioPlayer player = new AudioPlayer();
    private ImageButton recordButton;
    private ImageButton playButton;
    private ImageButton searchButton;

    String outputFile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView recordStatus = (TextView) findViewById(R.id.record_status);
        recordStatus.setText("Start recording");
        final TextView playStatus = (TextView) findViewById(R.id.play_status);
        playStatus.setText("Start playing");
        recordButton = (ImageButton) findViewById(R.id.record_button);
        playButton = (ImageButton) findViewById(R.id.play_button);
        searchButton = (ImageButton) findViewById(R.id.search_button);


        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            if (this.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            if (this.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_INTERNET);
        }

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.amr";

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
}
