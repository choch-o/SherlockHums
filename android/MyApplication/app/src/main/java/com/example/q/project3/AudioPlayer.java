package com.example.q.project3;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by q on 2017-01-05.
 */

public class AudioPlayer extends MediaPlayer {
    MediaPlayer mPlayer;
    String fileName;
    boolean mStartPlaying = true;

    void startPlaying(String fileName) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("MP", "COMPLETED!!");
                mp.release();
                mp = null;
            }
        });
        try {
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
