package com.example.q.project3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class ReadyActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int PERMISSIONS_REQUEST_INTERNET = 3;

    boolean is_recorder = false;
    ArrayList<String[]> ranks = new ArrayList<>();
    ArrayList<String[]> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ready);

//        databaseReference.child("song").child("1").child("title").setValue("보고싶다");
//        databaseReference.child("song").child("1").child("artist").setValue("김범수");
//        databaseReference.child("song").child("2").child("title").setValue("나 항상 그대를");
//        databaseReference.child("song").child("2").child("artist").setValue("이선희");
//        databaseReference.child("song").child("3").child("title").setValue("Tell me");
//        databaseReference.child("song").child("3").child("artist").setValue("원더걸스");
//        databaseReference.child("song").child("4").child("title").setValue("Gee");
//        databaseReference.child("song").child("4").child("artist").setValue("소녀시대");
//        databaseReference.child("song").child("5").child("title").setValue("Isn't she lovely");
//        databaseReference.child("song").child("5").child("artist").setValue("Stevie Wonder");
//        databaseReference.child("song").child("6").child("title").setValue("무조건");
//        databaseReference.child("song").child("6").child("artist").setValue("박상철");
//        databaseReference.child("song").child("7").child("title").setValue("거짓말");
//        databaseReference.child("song").child("7").child("artist").setValue("빅뱅");
//        databaseReference.child("song").child("8").child("title").setValue("이별택시");
//        databaseReference.child("song").child("8").child("artist").setValue("김연우");
//        databaseReference.child("song").child("9").child("title").setValue("귀로");
//        databaseReference.child("song").child("9").child("artist").setValue("나얼");
//        databaseReference.child("song").child("10").child("title").setValue("애인있어요");
//        databaseReference.child("song").child("10").child("artist").setValue("이은미");

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            if (this.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            if (this.checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{android.Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_INTERNET);
        }

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> songIndex = getSongsIndex();
                for(int i = 1; i < 5; i++) {
                    databaseReference.child("game").child("song" + Integer.toString(i) + "_title").setValue(songs.get(songIndex.get(i - 1))[0]);
                    databaseReference.child("game").child("song" + Integer.toString(i) + "_artist").setValue(songs.get(songIndex.get(i - 1))[1]);
                }

                is_recorder = true;
                databaseReference.child("game").child("on_game").setValue(true);
                Intent i = new Intent(getApplicationContext(), RecordingActivity.class);
                i.putExtra("title", songs.get(songIndex.get(0))[0]);
                i.putExtra("artist", songs.get(songIndex.get(0))[1]);
                startActivity(i);
            }
        });

        databaseReference.child ("game").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                switch (dataSnapshot.getKey()) {
                    case "on_game":
                        if (dataSnapshot.getValue(Boolean.class) && !is_recorder) {
                            Intent i = new Intent(getApplicationContext(), PlayingActivity.class);
                            i.putExtra("midi_path", "");
                            startActivity(i);
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

        databaseReference.child("user").addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String[] data;
                        data = new String[4];
                        data[0] = child.child("userId").getValue(String.class);
                        data[1] = child.child("profileImageURL").getValue(String.class);
                        data[2] = child.child("userName").getValue(String.class);
                        if (child.child("score").getValue(Integer.class) == null) {
                            data[3] = "0";
                        } else {
                            data[3] = Integer.toString(child.child("score").getValue(Integer.class));
                        }
                        ranks.add(data);
                    }
                    Collections.sort(ranks, new Comparator<String[]>() {
                        @Override
                        public int compare(String[] o1, String[] o2) {
                            return (Integer.parseInt(o2[3]) - Integer.parseInt(o1[3]));
                        }
                    });

                    ListView LV_ranking = (ListView) findViewById(R.id.listViewRanking);
                    RankingListViewAdapter rankingListViewAdapter = new RankingListViewAdapter(getApplicationContext(), ranks);
                    LV_ranking.setAdapter(rankingListViewAdapter);

                    LV_ranking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("getUserList:onCancelled", databaseError.toString());
                }
        });

        databaseReference.child("song").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String[] data;
                            data = new String[2];
                            data[0] = child.child("title").getValue(String.class);
                            data[1] = child.child("artist").getValue(String.class);
                            songs.add(data);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    public ArrayList<Integer> getSongsIndex() {
        ArrayList<Integer> songsIndex = new ArrayList<>();
        final Random random = new Random();
        final Set<Integer> intSet = new HashSet<>();
        while (intSet.size() < 4) {
            intSet.add(random.nextInt(10));
        }
        final Iterator<Integer> iterator = intSet.iterator();
        for (int i = 0; iterator.hasNext(); ++i) {
            songsIndex.add(iterator.next());
        }
        return songsIndex;
    }

}
