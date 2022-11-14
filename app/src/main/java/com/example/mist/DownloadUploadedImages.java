package com.example.mist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class DownloadUploadedImages extends AppCompatActivity {

    RecyclerView recyclerView;

    private ArrayList<String> urlList = new ArrayList<>();
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_uploaded_images);


        initRecyclerView();
        loadURLs();
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        imageAdapter = new ImageAdapter(urlList,this);
        recyclerView.setAdapter(imageAdapter);
    }

    private void loadURLs(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null && snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        urlList.add(dataSnapshot.getValue().toString());
                    }
                    imageAdapter.setUpdatedData(urlList);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatabaseReference dbRef = database.getReference().child("images");
        dbRef.addValueEventListener(listener);
    }
}