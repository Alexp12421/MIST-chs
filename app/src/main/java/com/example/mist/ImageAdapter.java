package com.example.mist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<InsertGame> urlList;
    private Context context;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;



    public ImageAdapter(List<InsertGame> urlList, Context context) {
        this.urlList = urlList;
        this.context = context;
    }

    public void setUpdatedData(List<InsertGame> urlList) {
        this.urlList = urlList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        InsertGame insertGame = urlList.get(position);

        //String imageUri = null;
        String imageUri = insertGame.getImage();

        //Picasso.get().load(imageUri).into(holder.imageview);

        holder.gamename.setText(insertGame.getGameName());
        holder.gameprice.setText(String.valueOf(insertGame.getGamePrice()) + "$");


        Glide.with(context)
                .load(imageUri)
                .into(holder.imageview);
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    public void setLocalUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userID = user.getUid();

    }
        // aici a fost Static, tb sa discutam !!!
     class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageview;
        TextView gamename, gameprice;
        Button button;

        public  ImageViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageViewPreview);
            gamename = itemView.findViewById(R.id.imageGameName);
            gameprice = itemView.findViewById(R.id.imageGamePrice);
            button = itemView.findViewById(R.id.buy_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        //converting string to float the gameprice!
                    System.out.println(Float.valueOf(gameprice.getText().toString().trim().substring(0,gameprice.getText().toString().trim().indexOf("$"))));
                    float gamePrice_Float = Float.valueOf(gameprice.getText().toString().trim().substring(0,gameprice.getText().toString().trim().indexOf("$")));
                    setLocalUser();
                    reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userProfile = snapshot.getValue(User.class);

                            if(userProfile != null){
                                String username = userProfile.getUsername();
                                String email = userProfile.getEmail();
                                boolean gameExistsLib = false;

                                for(String i : userProfile.getLibrary()){
                                    if(i.equals(gamename.getText().toString().trim()) == true) {
                                        gameExistsLib = true;
                                        break;
                                    }

                                }
                                if(gameExistsLib == false)
                                {
                                    if(userProfile.getWallet() >= gamePrice_Float){
                                        userProfile.substractBalance(gamePrice_Float);
                                        userProfile.addGame(gamename.getText().toString().trim());
                                    }
                                }

                                float wallet = userProfile.getWallet();
                                ArrayList<String> library = userProfile.getLibrary();


                                HashMap User = new HashMap<>();
                                User.put("email",email);
                                User.put("username",username);
                                User.put("wallet", wallet);
                                User.put("library",library);
                                reference.child(userID).updateChildren(User).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            System.out.println("Success");
                                        }
                                        else{
                                            System.out.println("Failed");
                                        }
                                    }
                                });
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            System.out.println("Failed 2");
                        }
                    });


                   // Intent intent = new Intent(itemView.getContext(),LibraryActivity.class);
                    //itemView.getContext().startActivity(intent);
                }
            });
        }


    }
}
