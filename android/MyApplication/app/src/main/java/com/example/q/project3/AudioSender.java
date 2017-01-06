package com.example.q.project3;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by q on 2017-01-05.
 */

public class AudioSender implements Callable<String> {
    String outputFile;

    AudioSender(String fileLocation){
        outputFile=fileLocation;
    }

    public String call(){
        File output=new File(outputFile);
        String response = new String ();
        try {
            MultipartUtility multipart = new MultipartUtility("http://52.78.52.132:3000/recorded");
            multipart.addFilePart("audio", output);
            response = multipart.finish();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            Log.d("SR", "SERVER REPLIED:");
            return response;
        }

    }
}
