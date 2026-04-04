package com.example.photogallery;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    // Interface for click callback — cleaner than passing activity reference
    public interface OnImageClickListener {
        void onImageClick(Uri imageUri);
    }

    private final List<Uri> images;
    private final OnImageClickListener listener;

    public ImageAdapter(List<Uri> images, OnImageClickListener listener) {
        this.images   = images;
        this.listener = listener;
    }

    // ViewHolder caches one grid cell's views — avoids repeated findViewById
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.ivGridImage);
        }
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item_image.xml — one grid cell
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = images.get(position);
        // Glide: loads image from URI, applies centerCrop to fill the square, caches it
        Glide.with(holder.imageView.getContext())
                .load(uri)
                .centerCrop()
                .into(holder.imageView);
        holder.imageView.setOnClickListener(v -> listener.onImageClick(uri));
    }

    @Override
    public int getItemCount() { return images.size(); }
}