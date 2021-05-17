package com.example.snapchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.snapchat.model.Note;
import com.example.snapchat.repo.Repo;

public class ChatShowPic extends AppCompatActivity implements Updatable, TaskListener
{
    String noteID;
    //Receive image and show on screen
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_showpic);

        noteID = getIntent().getStringExtra("noteid");

        //Note note = Repo.r().getNoteById(noteID);

        Repo.r().downloadBitmap(noteID, this);

        System.out.println("Chat picture view started " + noteID);
    }

    //Go back from chat to listview
    public void back(View view)
    {
        Intent intent = new Intent(ChatShowPic.this, ChatActivity.class);
        startActivity(intent);
        System.out.println("Going to chat");

        Repo.r().deleteById(noteID);

    }

    @Override
    public void update(Object o) {

    }

    //Get image from bitmap
    @Override
    public void receive(byte[] data) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        ImageView picture = findViewById(R.id.imageView3);
        picture.setImageBitmap(bmp);
        System.out.println("Display picture");
    }
}
