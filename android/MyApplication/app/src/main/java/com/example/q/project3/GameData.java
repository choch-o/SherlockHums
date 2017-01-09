package com.example.q.project3;

import com.google.firebase.database.Exclude;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by q on 2017-01-09.
 */

public class GameData {
    private int currRecorder;
    private int currRound;
    private boolean isRecording;
    private String player1Message;
    private String player2Message;
    private String player3Message;
    private String player4Message;

    public GameData() {}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("curr_recorder", currRecorder);
        result.put("curr_round", currRound);
        result.put("is_recording", isRecording);
        return result;
    }

    public String getPlayer1Message() {
        return player1Message;
    }

    public void setPlayer1Message(String message) {
        this.player1Message = message;
    }

    public String getPlayer2Messsage() {
        return player2Message;
    }

    public void setPlayer2Message(String message) {
        this.player2Message = message;
    }

    public String getPlayer3Message() {
        return player3Message;
    }

    public void setPlayer3Message(String message) {
        this.player3Message = message;
    }

    public String getPlayer4Message() {
        return player4Message;
    }

    public void setPlayer4Message(String message) {
        this.player4Message = message;
    }

}
