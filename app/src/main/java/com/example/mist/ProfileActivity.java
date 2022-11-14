package com.example.mist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener{

    public TextView store;
    public TextView imageInsert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        store = (TextView) findViewById(R.id.store);
        store.setOnClickListener(this);
        imageInsert = (TextView) findViewById(R.id.insert_image);
        imageInsert.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.store:
                startActivity(new Intent(this, StoreActivity.class));
                break;

            case R.id.insert_image:
                startActivity(new Intent(this, ImageInsertActivity.class));
                break;
        }
    }
}