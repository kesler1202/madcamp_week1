package com.example.intentexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Bitmap> images; // 변경: Bitmap으로 변경

    public ImageAdapter(Context context, ArrayList<Bitmap> images) { // 변경: ArrayList<Bitmap>으로 변경
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // 변경: 이미지를 Bitmap으로 설정
        imageView.setImageBitmap(images.get(position));
        return imageView;
    }
}