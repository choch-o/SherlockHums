package com.example.q.project3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URI;
import java.net.URL;

public class ProfileImageLoader extends AsyncTask<String, Void, Bitmap> {
    protected Bitmap doInBackground(String... params) {
        try {
            URI uri = new URI(params[0]);
            uri.normalize();
            URL url = uri.toURL();
            //Log.d("url", url.toString());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}