package com.example.intentexample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ImageDialogFragment extends DialogFragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_IMAGES = "images";

    private int position;
    private ArrayList<Bitmap> images;

    // 추가된 부분: newInstance 메서드와 TAG 상수 정의
    public static final String TAG = "ImageDialogFragment";


    public static ImageDialogFragment newInstance(int position, ArrayList<Bitmap> images) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelableArrayList(ARG_IMAGES, images);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            images = getArguments().getParcelableArrayList(ARG_IMAGES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView dialogImageView = view.findViewById(R.id.dialog_image_view);
        LinearLayout layoutComments = view.findViewById(R.id.layoutComments);
        EditText editTextSchedule = view.findViewById(R.id.editTextSchedule);
        ImageButton addButton = view.findViewById(R.id.addButton);

        // 이미지 목록을 프래그먼트에 전달
        Bitmap clickedImage = images.get(position);
        dialogImageView.setImageBitmap(clickedImage);

        addButton.setOnClickListener(v -> {
            // + 버튼 클릭 시 동적으로 메모 추가
            String commentText = editTextSchedule.getText().toString();
            if (!commentText.isEmpty()) {
                addComment(layoutComments, commentText);
                editTextSchedule.setText(""); // 입력창 초기화
            }
        });

        // 페이드인 애니메이션 적용
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        dialogImageView.startAnimation(fadeIn);

        // 클릭 시 페이드아웃 애니메이션 및 프래그먼트 닫기
        dialogImageView.setOnClickListener(v -> {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            dialogImageView.startAnimation(fadeOut);

            fadeOut.setAnimationListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationEnd(android.view.animation.Animation animation) {
                    dismiss();
                }
            });
        });
    }

    // 메모를 동적으로 추가하는 함수
    private void addComment(LinearLayout layoutComments, String commentText) {
        TextView commentTextView = new TextView(requireContext());
        commentTextView.setText(commentText);
        commentTextView.setTextColor(Color.WHITE);

        // 추가된 메모의 레이아웃 설정
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16); // 메모 간 간격 조절
        commentTextView.setLayoutParams(params);

        // 메모를 레이아웃에 추가
        layoutComments.addView(commentTextView);
    }

    // SimpleAnimationListener class to override only onAnimationEnd
    private static abstract class SimpleAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }


    }
}