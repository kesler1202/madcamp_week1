package com.example.intentexample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GalleryViewModel extends AndroidViewModel {
    private ArrayList<Bitmap> images = new ArrayList<>();
    private MutableLiveData<ArrayList<String>> commentsLiveData = new MutableLiveData<>(new ArrayList<>());

    public GalleryViewModel(Application application) {
        super(application);
        // 이미지를 SharedPreferences에서 불러와서 ViewModel에 설정
        Set<String> imageSet = loadImagesFromSharedPreferences();
        for (String encodedImage : imageSet) {
            Bitmap decodedImage = decodeStringToImage(encodedImage);
            images.add(decodedImage);
        }
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void setComments(ArrayList<String> comments) {
        commentsLiveData.setValue(comments);
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

    protected void onCleared() {
        super.onCleared();
        // ViewModel이 파괴될 때 호출되는 메서드
        // 여기에서 데이터 초기화 또는 갱신 코드 추가
        images.clear();
        commentsLiveData.setValue(new ArrayList<>());

        // 앱이 종료될 때 SharedPreferences에 댓글을 저장
        saveCommentsToSharedPreferences(commentsLiveData.getValue());
    }

    private void saveCommentsToSharedPreferences(ArrayList<String> comments) {
        // getApplication() 대신 getApplication() 메서드를 사용하여 Application을 가져옴
        Application application = getApplication();
        if (application != null) {
            SharedPreferences preferences = application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            // ArrayList를 문자열의 Set으로 변환하여 저장
            Set<String> commentsSet = new HashSet<>(comments);
            editor.putStringSet("comments", commentsSet);

            editor.apply();
        }
    }

    private Set<String> loadImagesFromSharedPreferences() {
        SharedPreferences preferences = getApplication().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getStringSet("images", new HashSet<>());
    }

    private Bitmap decodeStringToImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return decodeByteArray(decodedString);
    }

    private Bitmap decodeByteArray(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
