package com.example.intentexample;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    private ArrayList<Bitmap> images = new ArrayList<>();
    private ArrayList<String> comments = new ArrayList<>();
    private MutableLiveData<ArrayList<String>> commentsLiveData = new MutableLiveData<>();

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public LiveData<ArrayList<String>> getCommentsLiveData() {
        if (commentsLiveData == null) {
            commentsLiveData = new MutableLiveData<>();
            commentsLiveData.setValue(new ArrayList<>());
        }
        return commentsLiveData;
    }

    public void addImage(Bitmap image) {
        images.add(image);
        Log.d("GalleryViewModel", "Image added. Total images: " + images.size());
    }

    public void addComment(String comment) {
        commentsLiveData.getValue().add(comment);
        commentsLiveData.setValue(new ArrayList<>(commentsLiveData.getValue()));
        Log.d("GalleryViewModel", "Comment added. Total comments: " + commentsLiveData.getValue().size());
    }

    public void setComments(ArrayList<String> comments) {
        commentsLiveData.setValue(comments);
    }

    protected void onCleared() {
        super.onCleared();
        // ViewModel이 파괴될 때 호출되는 메서드
        // 여기에서 데이터 초기화 또는 갱신 코드 추가
        images.clear();
        commentsLiveData.setValue(new ArrayList<>());
    }
}
