package com.example.intentexample;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {
    private ArrayList<Bitmap> images = new ArrayList<>();

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void addImage(Bitmap image) {
        images.add(image);
    }
}
