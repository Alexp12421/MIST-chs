package com.example.mist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public TextView register;
    public TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register= (TextView) findViewById(R.id.id_RegisterButton);
        register.setOnClickListener(this);

        login = (TextView)  findViewById(R.id.id_LoginButton);
        login.setOnClickListener(this);



    }


    public void openRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_RegisterButton:
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.id_LoginButton:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}