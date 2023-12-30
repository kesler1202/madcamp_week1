package com.example.intentexample;
import android.graphics.Bitmap;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    private ArrayList<Bitmap> images = new ArrayList<>();

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void addImage(Bitmap image) {
        images.add(image);
        Log.d("GalleryViewModel", "Image added. Total images: " + images.size());
    }

    protected void onCleared() {
        super.onCleared();
        // ViewModel이 파괴될 때 호출되는 메서드
        // 여기에서 데이터 초기화 또는 갱신 코드 추가
        images.clear();
    }
}
