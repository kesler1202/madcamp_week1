package com.example.intentexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Gallery extends Fragment {

    private GalleryViewModel viewModel;
    private ImageAdapter imageAdapter;
    private Uri selectedImage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);

        // 이미지 피커 런처 등록
        registerImagePickerLauncher();
    }

    private ActivityResultLauncher<String> pickImageLauncher;

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
            Bitmap clickedImage = viewModel.getImages().get(position);
            Toast.makeText(requireContext(), "Clicked: " + clickedImage, Toast.LENGTH_SHORT).show();
            showImageDialog(position);
        });

        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(v -> openImagePicker());

        return view;
    }

    private void registerImagePickerLauncher() {
        if (pickImageLauncher == null) {
            pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    selectedImage = result;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImage);
                        addImageAndSaveToSharedPreferences(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(requireContext(), "사진 업로드 실패", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }

    private void addImageAndSaveToSharedPreferences(Bitmap bitmap) {
        // 이미지를 SharedPreferences에 저장
        Set<String> imageSet = loadImagesFromSharedPreferences();
        byte[] byteArray = getByteArrayFromBitmap(bitmap);
        String encodedImage = encodeImageToString(byteArray);
        imageSet.add(encodedImage);
        saveImagesToSharedPreferences(imageSet);

        // ViewModel에 이미지 추가
        viewModel.addImage(bitmap);

        // 로그 추가
        Log.d("Bitmap", "Image added to ViewModel");

        // 이미지 어댑터에 데이터 변경을 알려줌
        imageAdapter.notifyDataSetChanged();
    }

    private void showImageDialog(int position) {
        // 이미지 및 댓글 데이터와 함께 ImageDialogFragment의 인스턴스 생성
        ImageDialogFragment dialogFragment = ImageDialogFragment.newInstance(
                position,
                new ArrayList<>(viewModel.getImages()),
                new ArrayList<>(viewModel.getCommentsLiveData().getValue())
        );

        // Set up a CommentChangeListener to handle comment changes
        dialogFragment.setCommentChangeListener(new ImageDialogFragment.CommentChangeListener() {
            @Override
            public void onCommentChanged(ArrayList<String> updatedComments) {
                // 댓글이 변경되었을 때 수행할 작업
                viewModel.setComments(updatedComments);
            }
        });

        // FragmentTransaction을 사용하여 ImageDialogFragment를 표시합니다.
        getParentFragmentManager().beginTransaction()
                .replace(android.R.id.content, dialogFragment)
                .addToBackStack(null)  // 백 스택에 추가하여 뒤로 가기 동작을 지원
                .commit();
    }

    // 이미지 목록을 SharedPreferences에 저장하기
    private void saveImagesToSharedPreferences(Set<String> imageSet) {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("images", imageSet);
        editor.apply();
    }

    // 이미지 목록을 SharedPreferences에서 불러오기
    private Set<String> loadImagesFromSharedPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return preferences.getStringSet("images", new HashSet<>());
    }

    // Bitmap을 byte 배열로 변환하는 메서드
    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // byte 배열을 Base64로 인코딩하는 메서드
    private String encodeImageToString(byte[] byteArray) {
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
