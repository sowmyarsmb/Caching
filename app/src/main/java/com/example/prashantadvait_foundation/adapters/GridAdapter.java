package com.example.prashantadvait_foundation.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prashantadvait_foundation.R;
import com.example.prashantadvait_foundation.Utils.ImageCache;
import com.example.prashantadvait_foundation.activities.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    // private String[] imageUrls;
    private List<String> imageUrls;
    private Context context;

    private List<String> keyList;
    public GridAdapter(Context context, List<String> imageUrls, List<String> keyList) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.keyList= keyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Create an instance of ImageCache
        String imageUrl = imageUrls.get(position);
        ImageCache imageCache = new ImageCache(context);

       // To retrieve a bitmap from the cache
        Bitmap cachedBitmap = imageCache.getBitmapFromCache(imageUrls.get(position));

        if (cachedBitmap != null) {
            holder.imageView.setImageBitmap(cachedBitmap);
            Log.d("mytag", "Loading from cache for URL: " + imageUrl);
        } else {
            // Load the image using Picasso
            Picasso.get().load(imageUrl).into(holder.imageView);
            Log.d("mytag", "Loading from list for URL: " + imageUrl);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
