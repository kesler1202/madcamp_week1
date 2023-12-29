package com.example.intentexample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imagePaths;

    public ImageAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // If convertView is null, create a new ImageView
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Adjust size as needed
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            // If convertView is not null, reuse it
            imageView = (ImageView) convertView;
        }

        // Load the image into the ImageView using a library like Picasso or Glide
        // For simplicity, let's assume the image path is a local path (not recommended for production)
        String imagePath = imagePaths.get(position);
        // Load image into imageView using your preferred image loading library (e.g., Picasso, Glide)
        // For example: Picasso.get().load(imagePath).into(imageView);

        return imageView;
    }
}
