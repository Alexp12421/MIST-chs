package com.example.mist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener{

    public TextView store;

    public TextView imageInsert;

    public TextView library;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        store = (TextView) findViewById(R.id.go_store);
        store.setOnClickListener(this);

        imageInsert = (TextView) findViewById(R.id.insert_image);
        imageInsert.setOnClickListener(this);


        library = (TextView) findViewById(R.id.go_lib);
        library.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView usernameProfile = (TextView) findViewById(R.id.usernameProfile);


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String username = userProfile.username;
                    String email = userProfile.emial;

                    usernameProfile.setText("Welcome " + username + " !");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.insert_image:
                startActivity(new Intent(this, ImageInsertActivity.class));
                break;

            case R.id.go_store:
                startActivity(new Intent(this, StoreActivity.class));
                break;

            case R.id.go_lib:
                startActivity(new Intent(this, LibraryActivity.class));
                break;
            case R.id.balance:
                startActivity(new Intent(this, WalletActivity.class));
                break;



        }
    }



    }
