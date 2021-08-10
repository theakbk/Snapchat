package com.example.snapchat;

//Hunden er fin

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.snapchat.model.Note;
import com.example.snapchat.repo.Repo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Updatable{

    ImageView camera;
    EditText abc;
    Button snap;
    Button chat;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (ImageView) findViewById(R.id.cameraView);
        abc = (EditText) findViewById(R.id.Textfield1);

        abc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(abc.getWindowToken(), 0);
                    onTextEnter();
                    return true;
                }
                return false;

            }
        });

        snap = (Button) findViewById(R.id.cameraBtn);

        //This is one of the more important methods in my snap-app, as it fills crucial functionality in a snapchat app.
        //This method is the one that actually takes a new picture
        //Its a small lampda function that makes new intent and leads the user to the camera to take the picture
        snap.setOnClickListener(
                v -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,0);
                }
        );



        Repo.r().setup(this);
    }


    public void goChat(View view) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
        System.out.println("Going to chat");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //System.out.println("hello");
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");

        //Show image on frontpage
        camera.setImageBitmap(bitmap);

        snap.setText("Send");
        //snap.setVisibility(View.INVISIBLE);

        snap.setOnClickListener(
                v -> {
                    onSend(v);

        }
        );
    }


    public void onSend(View view)
    {
        System.out.println("Keyboard on click "+abc.getText());

        Bitmap b = drawTextToBitmap(bitmap, abc.getText().toString());
        //save snap to firebase
        Repo.r().addNoteAndImage(abc.getText().toString() , b);

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);

    }



    private void onTextEnter()
    {
        //draw text on image
        Bitmap b = drawTextToBitmap(bitmap, abc.getText().toString());
        camera.setImageBitmap(b);
    }

    //tak til Jon ;)
    private Bitmap drawTextToBitmap(Bitmap image, String gText) {
        Bitmap.Config bitmapConfig = image.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        image = image.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(image);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 0, 0));
        paint.setTextSize((int) (20)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // text shadow
        canvas.drawText(gText, 10, 100, paint);
        return image;
    }

    @Override
    public void update(Object o) {

    }
}
