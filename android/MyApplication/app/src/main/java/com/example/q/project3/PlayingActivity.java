package com.example.q.project3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by q on 2017-01-07.
 */

public class PlayingActivity extends Activity {
    /* Firebase setup */
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://sherlockhums-25f4c.appspot.com");

    private String userId = AccessToken.getCurrentAccessToken().getUserId();
    int RECORDER = 1;
    int NONRECORDER = 2;
    int currentRecorder = 1;
    int currentRound = 1;
    JSONArray tempSongs;
    String currTitle;
    String currArtist;
    int round = 0;

    String prevMIDIfile = "";

    TextView player1_message;
    TextView player2_message;
    TextView player3_message;
    TextView player4_message;

    TextView tv_player1_name;
    TextView tv_player2_name;
    TextView tv_player3_name;
    TextView tv_player4_name;

    ImageView iv_player1_image;
    ImageView iv_player2_image;
    ImageView iv_player3_image;
    ImageView iv_player4_image;

    String[] player_uids = new String[4];
    String[] player_names = new String[4];
    String[] player_image_urls = new String[4];
    Integer[] player_points = new Integer[4];

    EditText answer_box;
    Button submit_btn;

    String midiFile;

    int myIndex;
    boolean is_recorder;
    // AudioPlayer player = new AudioPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_playing);

        Intent i = getIntent();
        midiFile = i.getStringExtra("midi_path");
        is_recorder = i.getBooleanExtra("is_recorder", false);


        GameData gameData = new GameData();
        Map<String, Object> gameValues = gameData.toMap();
        final Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("curr_recorder", currentRecorder);
        childUpdates.put("curr_round", currentRound);
        childUpdates.put("is_recording", true);

        answer_box = (EditText) findViewById(R.id.answer_box);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        answer_box.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    databaseReference.child("player" + Integer.toString(myIndex) + "_message")
                            .setValue(answer_box.getText().toString());
                    answer_box.setText("");
                    return true;
                }
                return false;
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = answer_box.getText().toString();
                message = message.replaceAll("\\s+", "");

                databaseReference.child("player" + Integer.toString(myIndex) + "_message")
                        .setValue(answer_box.getText().toString());
                answer_box.setText("");
            }
        });

        player1_message = (TextView) findViewById(R.id.player1_message);
        player2_message = (TextView) findViewById(R.id.player2_message);
        player3_message = (TextView) findViewById(R.id.player3_message);
        player4_message = (TextView) findViewById(R.id.player4_message);

        tv_player1_name = (TextView) findViewById(R.id.player1_name);
        tv_player2_name = (TextView) findViewById(R.id.player2_name);
        tv_player3_name = (TextView) findViewById(R.id.player3_name);
        tv_player4_name = (TextView) findViewById(R.id.player4_name);

        iv_player1_image = (ImageView) findViewById(R.id.player1_profile);
        iv_player2_image = (ImageView) findViewById(R.id.player2_profile);
        iv_player3_image = (ImageView) findViewById(R.id.player3_profile);
        iv_player4_image = (ImageView) findViewById(R.id.player4_profile);

        databaseReference.child("midi").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("ADDADDHEREHERE", dataSnapshot.getKey());
                switch(dataSnapshot.getKey()) {
                    case "midi_path":
                        if (is_recorder) {
                            String midi_path = dataSnapshot.getValue(String.class);
                            Log.d("ADDADDMIDIMIDIHHHHHH", midi_path);
                            StorageReference midiRef = storageReference.child(midi_path);
                            try {
                                final File midiFile = File.createTempFile("midiplay", "mid");
                                midiRef.getFile(midiFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("FILE DOWNLOAD", "SUCCESS");
                                        playMIDI(midiFile.getAbsolutePath());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FILE DOWNLOAD", "FAILURE");
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("HEREHERE", dataSnapshot.getKey());
                switch(dataSnapshot.getKey()) {
                    case "midi_path":
                        String midi_path = dataSnapshot.getValue(String.class);
                        Log.d("MIDIMIDIMIDIPATHHHHHHH", midi_path);
                        StorageReference midiRef = storageReference.child(midi_path);
                        try {
                            final File midiFile = File.createTempFile("midiplay", "mid");
                            midiRef.getFile(midiFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("FILE DOWNLOAD", "SUCCESS");
                                    playMIDI(midiFile.getAbsolutePath());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("FILE DOWNLOAD", "FAILURE");
                                    e.printStackTrace();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("game").updateChildren(childUpdates);

        databaseReference.child("game").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ININININ", "SINGLE VALUE EVENT DATA CHANGE");
                Log.d("KEYKEYKEY", dataSnapshot.getKey());

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("CHILD", child.toString());

                    switch(child.getKey()) {
                        case "player1_uid":
                            player_uids[0] = child.getValue(String.class);
                            if (userId.equals(player_uids[0]))
                                myIndex = 1;
                            break;
                        case "player2_uid":
                            player_uids[1] = child.getValue(String.class);
                            if (userId.equals(player_uids[1]))
                                myIndex = 2;
                            break;
                        case "player3_uid":
                            player_uids[2] = child.getValue(String.class);
                            if (userId.equals(player_uids[2]))
                                myIndex = 3;
                            break;
                        case "player4_uid":
                            player_uids[3] = child.getValue(String.class);
                            if (userId.equals(player_uids[3]))
                                myIndex = 4;
                            break;
                        case "player1_name":
                            player_names[0] = child.getValue(String.class);
                            tv_player1_name.setText(child.getValue(String.class));
                            break;
                        case "player2_name":
                            player_names[1] = child.getValue(String.class);
                            tv_player2_name.setText(child.getValue(String.class));
                            break;
                        case "player3_name":
                            player_names[2] = child.getValue(String.class);
                            tv_player3_name.setText(child.getValue(String.class));
                            break;
                        case "player4_name":
                            player_names[3] = child.getValue(String.class);
                            tv_player4_name.setText(child.getValue(String.class));
                            break;
                        case "player1_image":
                            player_image_urls[0] = child.getValue(String.class);
                            Bitmap profileImage1 = getProfileImage(child.getValue(String.class));
                            iv_player1_image.setImageBitmap(RoundedImageView.getCroppedBitmap(profileImage1, profileImage1.getWidth()));
                            break;
                        case "player2_image":
                            player_image_urls[1] = child.getValue(String.class);
                            Bitmap profileImage2 = getProfileImage(child.getValue(String.class));
                            iv_player2_image.setImageBitmap(RoundedImageView.getCroppedBitmap(profileImage2, profileImage2.getWidth()));
                            break;
                        case "player3_image":
                            player_image_urls[2] = child.getValue(String.class);
                            Bitmap profileImage3 = getProfileImage(child.getValue(String.class));
                            iv_player3_image.setImageBitmap(RoundedImageView.getCroppedBitmap(profileImage3, profileImage3.getWidth()));
                            break;
                        case "player4_image":
                            player_image_urls[3] = child.getValue(String.class);
                            Bitmap profileImage4 = getProfileImage(child.getValue(String.class));
                            iv_player4_image.setImageBitmap(RoundedImageView.getCroppedBitmap(profileImage4, profileImage4.getWidth()));
                            break;
                        case "player1_point":
                            player_points[0] = child.getValue(Integer.class);
                        case "player2_point":
                            player_points[1] = child.getValue(Integer.class);
                        case "player3_point":
                            player_points[2] = child.getValue(Integer.class);
                        case "player4_point":
                            player_points[3] = child.getValue(Integer.class);
                    }
                }
//                startRound();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("game").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "player1_message":
                        player1_message.setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player2_message":
                        player2_message.setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player3_message":
                        player3_message.setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player4_message":
                        player4_message.setText(dataSnapshot.getValue(String.class));
                        break;
                    case "is_recording":
                        if (dataSnapshot.getValue(Boolean.class)) {

                        }
                        else {
                            start_guessing();
                        }
                        break;
                    case "curr_recorder":
                        currentRecorder = dataSnapshot.getValue(Integer.class);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.d("FAIL IN DB", "Failed to read value. ", databaseError.toException());
            }
        });

        if (!midiFile.equals("")) {
            databaseReference.child("midi").child("midi_path").setValue(midiFile);
        }
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
        /*
        if (is_recorder()) {
            Intent i = new Intent(PlayingActivity.this, RecordingActivity.class);
            i.putExtra("title", currTitle);
            i.putExtra("artist", currArtist);
            startActivityForResult(i, RECORDER);
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // databaseReference.child("is_recording").setValue(false);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("is_recording", false);
    }

    void start_guessing() {

    }

    boolean is_recorder() {
        return (player_uids[currentRecorder - 1].equals(userId));
    }

    public Bitmap getProfileImage(String imagePath) {
        ProfileImageLoader profileImageLoader = new ProfileImageLoader();
        Bitmap result = ((BitmapDrawable) getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square)).getBitmap();
        try {
            result = profileImageLoader.execute(imagePath).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    void playMIDI(final String midiPath) {
        final MediaPlayer mPlayer = new MediaPlayer();
        final MediaPlayer mPlayer2 = new MediaPlayer();
        try {
            mPlayer.setDataSource(midiPath);
            mPlayer.prepare();
            mPlayer.start();
            Log.d("MPLAYER", "STARTED");
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("MPLAYER", "STOPPED");
                    mp.stop();
                    mp.release();
                    try {
                        mPlayer2.setDataSource(midiPath);
                        mPlayer2.prepare();
                        mPlayer2.start();
                        mPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp2) {
                                Log.d("MPLAYER2", "STOPPED");
                                mp2.stop();
                                mp2.release();
                            }
                        });
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            });
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
