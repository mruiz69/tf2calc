package com.example.applayouts;

import android.content.Context;
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
    private List<Uri> imageUriList;
    private Context context;

    public ImageAdapter(Context context, List<Uri> imageUriList) {
        this.context = context;
        this.imageUriList = imageUriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUriList.get(position);
        // Usar Glide para cargar la imagen en el ImageView
        Glide.with(context)
                .load(imageUri)
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return imageUriList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}

