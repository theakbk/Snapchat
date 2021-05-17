package com.example.snapchat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snapchat.adapter.ChatListAdapter;
import com.example.snapchat.repo.Repo;

public class ChatActivity extends AppCompatActivity implements Updatable
{
    ListView listView;
    ChatListAdapter adapter;

    private void setupListView()
    {
        listView = findViewById(R.id.listView1);
        adapter = new ChatListAdapter(Repo.r(), this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println("click on row: " + position);
            Intent intent = new Intent(ChatActivity.this, ChatShowPic.class);
            intent.putExtra("noteid", Repo.r().get(position).getId());
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        setupListView();
        System.out.println("Chat/view started");
    }

    public void goBack(View view) {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void update(Object o) {
        adapter.notifyDataSetChanged();
    }
}
