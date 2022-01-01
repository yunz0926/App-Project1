package com.example.project1_2;

import static com.example.project1_2.ImageAdapter.mThumblds;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class FullImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        // get intent data
        Intent i = getIntent();

        // Selected image id
        int position = i.getExtras().getInt("id");
        com.example.project1_2.ImageAdapter imageAdapter = new com.example.project1_2.ImageAdapter(this, 360);

        ImageView imageView = (ImageView) findViewById(R.id.full_image_view);

        if(imageAdapter.mThumblds[position] instanceof Integer){
            imageView.setImageResource((Integer)imageAdapter.mThumblds[position]);
        }
        else if(imageAdapter.mThumblds[position] instanceof Uri){
            imageView.setImageURI((Uri)imageAdapter.mThumblds[position]);
        }

    }

}
