package com.example.intentexample;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
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
import java.util.ArrayList;

public class Gallery extends Fragment {

    private GalleryViewModel viewModel;
    private ImageAdapter imageAdapter;
    private Uri selectedImage;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("YourFragment", "onResume called");
        // onResume에서 이미지를 초기화하고 업데이트
        if (imageAdapter == null) {
            imageAdapter = new ImageAdapter(requireContext(), viewModel.getImages());
            GridView gridView = requireView().findViewById(R.id.feed_gallery_view);
            gridView.setAdapter(imageAdapter);
        } else {
            // 이미 어댑터가 생성되었으면 이미지 데이터만 업데이트
            imageAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);
    }


    private ActivityResultLauncher<String> pickImageLauncher;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, container, false);

        imageAdapter = new ImageAdapter(requireContext(), viewModel.getImages());

        // Set up the GridView
        GridView gridView = view.findViewById(R.id.feed_gallery_view);
        gridView.setAdapter(imageAdapter);

        // Set up item click listener
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            Bitmap clickedImage = viewModel.getImages().get(position);
            Toast.makeText(requireContext(), "Clicked: " + clickedImage, Toast.LENGTH_SHORT).show();
            showImageDialog(position);
        });

        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(v -> openImagePicker());

        // 이미지 피커 런처 등록
        registerImagePickerLauncher();

        return view;
    }
    private void registerImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
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

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }


    private void showImageDialog(int position) {
        // 이미지 목록을 프래그먼트에 전달
        ImageDialogFragment.newInstance(position, new ArrayList<>(viewModel.getImages()))
                .show(getChildFragmentManager(), ImageDialogFragment.TAG);
    }


    // SimpleAnimationListener class to override only onAnimationEnd
    private static class SimpleAnimationListener implements android.view.animation.Animation.AnimationListener {
        @Override
        public void onAnimationStart(android.view.animation.Animation animation) {
        }

        @Override
        public void onAnimationEnd(android.view.animation.Animation animation) {
        }

        @Override
        public void onAnimationRepeat(android.view.animation.Animation animation) {
        }
    }
}
