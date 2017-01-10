package com.example.q.project3;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Created by q on 2017-01-05.
 */

public class AudioPlayer extends MediaPlayer {
    MediaPlayer mPlayer;
    String fileName;
    boolean mStartPlaying = true;

    void startPlaying(final String fileName) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("MP", "FIRST PLAY COMPLETED!!");
                mp.release();
                mp = null;
                // mp.prepare();
                /*
                AudioPlayer sp = new AudioPlayer();
                sp.startPlaying(fileName);
                // mp.startPlaying(midiFile.getAbsolutePath());
                sp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer smp) {
                        Log.d("SMP", "COMPLETED!!");
                        smp.release();
                        smp = null;
                    }
                });*/
            }
        });
        try {
            Log.d("DATA SOURCE", fileName);
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();

        } catch (IOException ioe) {
            Log.e("mPlayer", "Media player prepare() failed");
            ioe.printStackTrace();
        }
    }
/*
    void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }*/

}
