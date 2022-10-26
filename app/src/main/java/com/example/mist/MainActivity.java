package com.example.mist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mist.ui.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    public TextView main_text;
    public Button buttonus,loginbutton;
    public Titlu titlu;
    private com.google.firebase.database.DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_text = (TextView) findViewById(R.id.main_text_id);

        buttonus = (Button)findViewById(R.id.Amodo_Button);

        loginbutton = (Button)findViewById(R.id.id_loginbutton);


        buttonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_text.setText("Shmeck");
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchActivities();
            }
        });

        databaseReference = database.getReference().child("Titluri");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titlu = snapshot.getValue(Titlu.class);
                main_text.setText(titlu.getTitle());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void switchActivities() {
        Intent switchActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(switchActivityIntent);
    }
}