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

public class ImageAdapter2 extends RecyclerView.Adapter<ImageAdapter2.ImageViewHolder> {

    private List<InsertGame> urlList;
    private Context context;

    public ImageAdapter2(List<InsertGame> urlList, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row2, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        InsertGame insertGame = urlList.get(position);

        //String imageUri = null;
        String imageUri = insertGame.getImage();

        //Picasso.get().load(imageUri).into(holder.imageview);

        holder.gamename.setText(insertGame.getGameName());

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
        TextView gamename;

        public  ImageViewHolder(View itemView) {
            super(itemView);
            imageview = itemView.findViewById(R.id.imageViewPreviewLibrary);
            gamename = itemView.findViewById(R.id.imageGameNameLibrary);
        }
    }
}