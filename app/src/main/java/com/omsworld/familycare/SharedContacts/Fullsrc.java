package com.omsworld.familycare.SharedContacts;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.omsworld.familycare.R;
import com.squareup.picasso.Picasso;

/**
 * Created by om's on 11/13/2016.
 */

public class Fullsrc extends AppCompatActivity {
ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_fullsrc);
        image=(ImageView)findViewById(R.id.image);

        Bundle extras = getIntent().getExtras();
        if (extras.getString("imagebitmap") != null) {
            Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
            image.setImageBitmap(bmp );
        }else {
            String url = extras.getString("img_url");
           // Picasso.with(this).load(url).placeholder(R.drawable.place_holder).error(R.drawable.place_holder).into(image);
        }
    }
}
