package com.example.intentexample;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

public class Gallery extends Fragment {

    private GalleryViewModel viewModel;
    private ImageAdapter imageAdapter;
    private Uri selectedImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, container, false);

        imageAdapter = new ImageAdapter(requireContext(), viewModel.getImages());

        // Set up the GridView
        GridView gridView = view.findViewById(R.id.feed_gallery_view);
        gridView.setAdapter(imageAdapter);

        // Set up item click listener
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            // 변경: 이미지를 클릭할 때의 동작
            Bitmap clickedImage = viewModel.getImages().get(position);
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

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    result -> {
                        if (result != null) {
                            selectedImage = result;
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                                viewModel.addImage(bitmap);
                                imageAdapter.notifyDataSetChanged();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(requireContext(), "사진 업로드 실패", Toast.LENGTH_LONG).show();
                        }
                    });
}
