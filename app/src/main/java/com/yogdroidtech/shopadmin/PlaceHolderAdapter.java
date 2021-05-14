package com.yogdroidtech.shopadmin;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceHolderAdapter  extends RecyclerView.Adapter<PlaceHolderAdapter.PlaceViewHolder> {
    List<Bitmap> bitmapList;
    uploadListener uploadListener;

    public PlaceHolderAdapter(List<Bitmap> bitmapList, com.yogdroidtech.shopadmin.uploadListener uploadListener) {
        this.bitmapList = bitmapList;
        this.uploadListener = uploadListener;
    }

    public void setBitmapList(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_placeholder, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.imageView.setImageBitmap(bitmapList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadListener.onclick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
