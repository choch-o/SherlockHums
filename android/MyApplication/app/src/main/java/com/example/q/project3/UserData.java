package com.example.q.project3;

public class UserData {
    private String userId;
    private String userName;
    private String profileImageURL;
    private String score;
    private String onGame;

    public UserData() {}

    public UserData(String userId, String userName, String profileImageURL, String score, String onGame) {
        this.userId = userId;
        this.userName = userName;
        this.profileImageURL = profileImageURL;
        this.score = score;
        this.onGame = onGame;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getOnGame() {
        return onGame;
    }

    public void setOnGame(String onGame) {
        this.onGame = onGame;
    }
}
