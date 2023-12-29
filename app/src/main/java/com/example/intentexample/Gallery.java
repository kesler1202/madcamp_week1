package com.example.intentexample;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

public class Gallery extends Fragment {

    private static final int REQUEST_CODE = 1;

    private ArrayList<Bitmap> images; // 변경: Bitmap으로 이미지를 저장하는 리스트
    private ImageAdapter imageAdapter;
    private Uri selectedImage;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri result) {
                            if (result != null) {
                                selectedImage = result;

                                // 이미지뷰에 이미지 불러오기
                                ImageView imageView = requireView().findViewById(R.id.feed_profile_img);
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                                    imageView.setImageBitmap(bitmap);

                                    // 변경: 비트맵 리스트에 추가
                                    images.add(bitmap);
                                    imageAdapter.notifyDataSetChanged();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(requireContext(), "사진 업로드 실패", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, container, false);

        // Initialize the images list and the ImageAdapter
        images = new ArrayList<>();
        imageAdapter = new ImageAdapter(requireContext(), images);

        // Set up the GridView
        GridView gridView = view.findViewById(R.id.feed_gallery_view);
        gridView.setAdapter(imageAdapter);

        // Set up item click listener
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            // 변경: 이미지를 클릭할 때의 동작
            Bitmap clickedImage = images.get(position);
            Toast.makeText(requireContext(), "Clicked: " + clickedImage, Toast.LENGTH_SHORT).show();
        });

        // Set up image button click listener
        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }
}
