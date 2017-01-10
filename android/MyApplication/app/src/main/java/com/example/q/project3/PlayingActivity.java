package com.example.q.project3;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    final int NEXT_ROUND_REQUEST = 1;

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
    Integer[] user_score = new Integer[4];
    TextView[] player_messages = new TextView[4];
    String[] song_titles = new String[4];
    String[] song_artists = new String[4];
    TextView[] tv_player_points = new TextView[4];

    EditText answer_box;
    Button submit_btn;

    String midiFile;

    int myIndex;
    boolean is_recorder;
    // AudioPlayer player = new AudioPlayer();
    MediaPlayer mPlayer = new MediaPlayer();

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

        player_messages[0] = (TextView) findViewById(R.id.player1_message);
        player_messages[1] = (TextView) findViewById(R.id.player2_message);
        player_messages[2] = (TextView) findViewById(R.id.player3_message);
        player_messages[3] = (TextView) findViewById(R.id.player4_message);

        tv_player1_name = (TextView) findViewById(R.id.player1_name);
        tv_player2_name = (TextView) findViewById(R.id.player2_name);
        tv_player3_name = (TextView) findViewById(R.id.player3_name);
        tv_player4_name = (TextView) findViewById(R.id.player4_name);

        iv_player1_image = (ImageView) findViewById(R.id.player1_profile);
        iv_player2_image = (ImageView) findViewById(R.id.player2_profile);
        iv_player3_image = (ImageView) findViewById(R.id.player3_profile);
        iv_player4_image = (ImageView) findViewById(R.id.player4_profile);

        tv_player_points[0] = (TextView) findViewById(R.id.player1_point);
        tv_player_points[1] = (TextView) findViewById(R.id.player2_point);
        tv_player_points[2] = (TextView) findViewById(R.id.player3_point);
        tv_player_points[3] = (TextView) findViewById(R.id.player4_point);
        tv_player_points[0].setText("0");
        tv_player_points[1].setText("0");
        tv_player_points[2].setText("0");
        tv_player_points[3].setText("0");

        databaseReference.child("midi").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("ADDADDHEREHERE", dataSnapshot.getKey());
                switch(dataSnapshot.getKey()) {
                    case "midi_path":
                        if (is_recorder) {
                            String midi_path = dataSnapshot.getValue(String.class);
                            Log.d("ADDDDDMIDIMIDIMHHH", midi_path);
                            StorageReference midiRef = storageReference.child(midi_path);
                            try {
                                final File midiFile = File.createTempFile("midiplay", "mid");
                                midiRef.getFile(midiFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Log.d("FILE DOWNLOAD", "SUCCESS");
                                        startRound(midiFile.getAbsolutePath());
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
                        } else {
                            Log.d("WHYHEHHDK", "DSJLKFHDS");
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
                                    startRound(midiFile.getAbsolutePath());
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

                    if (child.getKey().equals("song" + Integer.toString(currentRound) + "_title")) {
                        Log.d("currTitle", child.getValue(String.class));
                        currTitle = child.getValue(String.class);
                    } else if (child.getKey().equals("song" + Integer.toString(currentRound) + "_artist")) {
                        Log.d("currArtist", child.getValue(String.class));
                        currArtist = child.getValue(String.class);
                    }

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
                            break;
                        case "player2_point":
                            player_points[1] = child.getValue(Integer.class);
                            break;
                        case "player3_point":
                            player_points[2] = child.getValue(Integer.class);
                            break;
                        case "player4_point":
                            player_points[3] = child.getValue(Integer.class);
                            break;
                        case "song1_title":
                            song_titles[0] = child.getValue(String.class);
                            break;
                        case "song2_title":
                            song_titles[1] = child.getValue(String.class);
                            break;
                        case "song3_title":
                            song_titles[2] = child.getValue(String.class);
                            break;
                        case "song4_title":
                            song_titles[3] = child.getValue(String.class);
                            break;
                        case "song1_artist":
                            song_artists[0] = child.getValue(String.class);
                            break;
                        case "song2_artist":
                            song_artists[1] = child.getValue(String.class);
                            break;
                        case "song3_artist":
                            song_artists[2] = child.getValue(String.class);
                            break;
                        case "song4_artist":
                            song_artists[3] = child.getValue(String.class);
                            break;
                    }
                }

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
                        player_messages[0].setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player2_message":
                        player_messages[1].setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player3_message":
                        player_messages[2].setText(dataSnapshot.getValue(String.class));
                        break;
                    case "player4_message":
                        player_messages[3].setText(dataSnapshot.getValue(String.class));
                        break;
//                    case "curr_recorder":
//                        currentRecorder = dataSnapshot.getValue(Integer.class);
//                        break;
                    case "player1_point":
                        player_points[0] = dataSnapshot.getValue(Integer.class);
                        tv_player_points[0].setText(Integer.toString(player_points[0]));
                        onCorrectAnswer(0);
                        break;
                    case "player2_point":
                        player_points[1] = dataSnapshot.getValue(Integer.class);
                        tv_player_points[1].setText(Integer.toString(player_points[1]));
                        onCorrectAnswer(1);
                        break;
                    case "player3_point":
                        player_points[2] = dataSnapshot.getValue(Integer.class);
                        tv_player_points[2].setText(Integer.toString(player_points[2]));
                        onCorrectAnswer(2);
                        break;
                    case "player4_point":
                        player_points[3] = dataSnapshot.getValue(Integer.class);
                        tv_player_points[3].setText(Integer.toString(player_points[3]));
                        onCorrectAnswer(3);
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

        answer_box = (EditText) findViewById(R.id.answer_box);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        answer_box.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String message = answer_box.getText().toString();
                    Log.d("MESSAGE BEFORE", message);
                    message = message.replaceAll("\\s+", "");
                    Log.d("MESSAGE AFTER", message);
                    Log.d("CURRTITLE BEFORE", currTitle);
                    Log.d("CURRTITLE AFTER", currTitle.replaceAll("\\s+", ""));
                    if (message.equals(currTitle.replaceAll("\\s+", "").toLowerCase())) {
                        Log.d("currTitle", currTitle.replaceAll("\\s+", "").toLowerCase());
                        databaseReference.child("game").child("player" + Integer.toString(myIndex) + "_point").setValue(player_points[myIndex - 1] + 1);
                    }
                    databaseReference.child("game").child("player" + Integer.toString(myIndex) + "_message")
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
                if (message.equals(currTitle.replaceAll("\\s+", "").toLowerCase())) {
                    Log.d("currTitle", currTitle.replaceAll("\\s+", "").toLowerCase());
                    databaseReference.child("game").child("player" + Integer.toString(myIndex) + "_point").setValue(player_points[myIndex - 1] + 1);
                }
                databaseReference.child("game").child("player" + Integer.toString(myIndex) + "_message")
                        .setValue(answer_box.getText().toString());
                answer_box.setText("");
            }
        });
    }

    private void startRound(String midi_path) {
        int id = getResources().getIdentifier("player" + currentRecorder, "id", getPackageName());
        LinearLayout recorder = (LinearLayout) findViewById(id);
        recorder.setBackgroundColor(getResources().getColor(R.color.colorRecorder));

        playMIDI(midi_path);
    }

    void nextRound() {
        if (currentRecorder == myIndex && currentRound < 4) {
            Intent i = new Intent(this, RecordingActivity.class);
            i.putExtra("title", song_titles[currentRound - 1]);
            i.putExtra("artist", song_artists[currentRound - 1]);
            i.putExtra("from", "PlayingActivity");
            startActivityForResult(i, NEXT_ROUND_REQUEST);
        }
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
        try {
            mPlayer.reset();
            mPlayer.setDataSource(midiPath);
            mPlayer.prepare();
            mPlayer.start();
            Log.d("MPLAYER", "STARTED");
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("MPLAYER", "STOPPED");
                    Toast.makeText(PlayingActivity.this, "REPLAY", Toast.LENGTH_LONG);
                    mp.stop();
                    try {
                        mPlayer.prepare();
                        mPlayer.start();
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp2) {
                                Log.d("MPLAYER2", "STOPPED");
                                mp2.stop();
                                // mp2.release();
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

    void onCorrectAnswer(final int playerIndex) {
        player_messages[playerIndex].setTextColor(Color.rgb(255, 153, 00));
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                player_messages[playerIndex].setTextColor(Color.BLACK);
                int id = getResources().getIdentifier("player" + currentRecorder, "id", getPackageName());
                LinearLayout recorder = (LinearLayout) findViewById(id);
                recorder.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                mPlayer.stop();
                final Map<String, Object> childUpdates = new HashMap<>();
                if (currentRecorder > 3) {
                    // currentRecorder = 1;
                    finishGame();
                } else {
                    currentRecorder++;
                }
                childUpdates.put("curr_recorder", currentRecorder);
                if (currentRound > 3) {
                    finishGame();
                } else {
                    currentRound++;
                }
                childUpdates.put("curr_round", currentRound);
                databaseReference.child("game").updateChildren(childUpdates);
                currTitle = song_titles[currentRound - 1];
                currArtist = song_artists[currentRound - 1];
                nextRound();
            }
        }.start();
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        Log.d("MIMIMIMIMIMidJNDI", RecordingActivity.next_midi);

        databaseReference.child("midi").child("midi_path").setValue(RecordingActivity.next_midi);
    }

    void finishGame() {
        databaseReference.child("user").child(player_uids[myIndex - 1]).child("score").setValue(Integer.parseInt(ReadyActivity.ranks.get(myIndex - 1)[3]) + player_points[myIndex - 1] * 10);
        databaseReference.child("game").child("on_game").setValue(false);
        Intent i = new Intent(this, ReadyActivity.class);
        startActivity(i);
    }
}
