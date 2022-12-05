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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<InsertGame> urlList;
    private Context context;

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

    static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageview;
        TextView gamename, gameprice;
        Button button;

        public  ImageViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageViewPreview);
            gamename = itemView.findViewById(R.id.imageGameName);
            gameprice = itemView.findViewById(R.id.imageGamePrice);
            button = itemView.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(),LibraryActivity.class);
                    itemView.getContext().startActivity(intent);
                }
            });
        }


    }
}
