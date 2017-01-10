package com.example.q.project3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RankingListViewAdapter extends BaseAdapter {

    private AccessToken accessToken =  AccessToken.getCurrentAccessToken();
    private String userId = accessToken.getUserId();

    private Context context;
    private ArrayList<String[]> ranks;
    private LayoutInflater inflater;
    private TextView TV_ranking;
    private TextView TV_name;
    private TextView TV_score;
    private ImageView IV_profile;

    public RankingListViewAdapter(Context context, ArrayList<String[]> ranks) {
        this.context = context;
        this.ranks = ranks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ranks.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.activity_ready_item, null);
        }

        TV_ranking = (TextView) view.findViewById(R.id.textViewRanking);
        TV_name = (TextView) view.findViewById(R.id.textViewName);
        TV_score = (TextView) view.findViewById(R.id.textViewScore);
        IV_profile = (ImageView) view.findViewById(R.id.imageViewProfile);

        TV_ranking.setText(Integer.toString(position + 1));
        TV_name.setText(ranks.get(position)[2]);
        TV_score.setText(ranks.get(position)[3]);
        Bitmap profileImage = getProfileImage(ranks.get(position)[1]);
        IV_profile.setImageBitmap(RoundedImageView.getCroppedBitmap(profileImage, profileImage.getWidth()));

        if (ranks.get(position)[0].equals(userId)) {
            TV_ranking.setTextColor(context.getResources().getColor(R.color.colorRecorder));
            TV_name.setTextColor(context.getResources().getColor(R.color.colorRecorder));
            TV_score.setTextColor(context.getResources().getColor(R.color.colorRecorder));
        }

        return view;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(ranks.get(position)[0]);
    }

    @Override
    public Object getItem(int position) {
        return ranks.get(position);
    }

    public Bitmap getProfileImage(String imagePath) {
        ProfileImageLoader profileImageLoader = new ProfileImageLoader();
        Bitmap result = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.com_facebook_profile_picture_blank_square)).getBitmap();
        try {
            result = profileImageLoader.execute(imagePath).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}