package com.example.intentexample;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
            showImageDialog(position);
        });

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

    private void showImageDialog(int position) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.image_dialog);

        ImageView dialogImageView = dialog.findViewById(R.id.dialog_image_view);

        // Set the clicked image in the dialog
        Bitmap clickedImage = viewModel.getImages().get(position);
        dialogImageView.setImageBitmap(clickedImage);

        // Apply fadein animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        dialogImageView.startAnimation(fadeIn);

        dialog.show();

        // Close the dialog with fadeout animation when clicked
        dialogImageView.setOnClickListener(v -> {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            dialogImageView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(android.view.animation.Animation animation) {
                    dialog.dismiss();
                }
            });
        });
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
