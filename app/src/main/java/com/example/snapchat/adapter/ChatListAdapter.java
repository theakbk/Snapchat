package com.example.snapchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.snapchat.repo.Repo;
import com.example.snapchat.R;


import java.util.List;

public class ChatListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater; // can "inflate" layout files
    private Repo repo;


    public ChatListAdapter(Repo repo, Context context) {
        this.repo = repo;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        System.out.println("adapter size " + repo.size());
        return repo.size();
    }
    @Override
    public Object getItem(int i) {
        return repo.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // make layout .xml file first...
        if(view == null){
            view = layoutInflater.inflate(R.layout.chat_row, null);
        }
        // LinearLayout linearLayout = (LinearLayout)view;
        TextView textView = view.findViewById(R.id.chatRow1);
        if(textView != null) {
            textView.setText(repo.get(i).getText()); // later I will connect to the items list
        }
        return textView;
    }
}