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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReadyActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int PERMISSIONS_REQUEST_INTERNET = 3;

    ArrayList<String[]> ranks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ready);

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
                databaseReference.child("game").child("on_game").setValue(true);
                Intent i = new Intent(ReadyActivity.this, PlayingActivity.class);
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
                        if (dataSnapshot.getValue(Boolean.class)) {
                            Intent i = new Intent(ReadyActivity.this, PlayingActivity.class);
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
                    RankingListViewAdapter rankingListViewAdapter = new RankingListViewAdapter(getApplication(), ranks);
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
    }

}
