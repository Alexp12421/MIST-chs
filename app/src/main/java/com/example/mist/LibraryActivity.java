package com.example.mist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    RecyclerView recyclerViewLibrary2;
    List<InsertGame> urlListLibrary;
    List<String> games, images;
    List<String> userLibrary;
    ImageAdapter2 imageAdapterLibrary;

    FirebaseDatabase databaseL;
    DatabaseReference databaseReferenceL;
    FirebaseStorage firebaseStorageL;


    public void setLocalUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        databaseL = FirebaseDatabase.getInstance();
        databaseReferenceL = databaseL.getReference().child("images");
        firebaseStorageL = FirebaseStorage.getInstance();

        recyclerViewLibrary2 = findViewById(R.id.recyclerViewLibrary);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewLibrary2.setHasFixedSize(true);
        recyclerViewLibrary2.setLayoutManager(layoutManager);

        urlListLibrary = new ArrayList<InsertGame>();
        imageAdapterLibrary = new ImageAdapter2(urlListLibrary,LibraryActivity.this);
        recyclerViewLibrary2.setAdapter(imageAdapterLibrary);

        setLocalUser();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    userLibrary = userProfile.getLibrary();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LibraryActivity.this, "Something wrong", Toast.LENGTH_LONG).show();
            }
        });

        databaseReferenceL.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String image = snapshot.child("Game Image").getValue().toString();
                String name = snapshot.child("Game Name").getValue().toString();

                for(String i : userLibrary){
                    if (i.equals(name)) {
                        InsertGame insertGame = new InsertGame(image, name, "");
                        urlListLibrary.add(insertGame);
                        imageAdapterLibrary.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                break;

            case R.id.library:
                startActivity(new Intent(this, LibraryActivity.class));
                break;
            case R.id.balance:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.store:
                startActivity(new Intent(this, StoreActivity.class));
                break;
        }
        return true;
    }
}