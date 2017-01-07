package com.example.q.project3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RankingListViewAdapter extends BaseAdapter {
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

        TV_ranking.setText(ranks.get(position)[0]);
        TV_name.setText(ranks.get(position)[2]);
        TV_score.setText(ranks.get(position)[3]);

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
}