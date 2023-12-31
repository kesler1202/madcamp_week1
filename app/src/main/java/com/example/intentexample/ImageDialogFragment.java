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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ImageDialogFragment extends Fragment {
    // CommentChangeListener 인터페이스 정의
    public interface CommentChangeListener {
        void onCommentChanged(ArrayList<String> updatedComments);
    }

    private CommentChangeListener commentChangeListener;

    // setCommentChangeListener 메서드 정의
    public void setCommentChangeListener(CommentChangeListener listener) {
        this.commentChangeListener = listener;
    }

    private static final String ARG_POSITION = "position";
    private static final String ARG_IMAGES = "images";
    private static final String ARG_COMMENTS = "comments";

    private int position;
    private ArrayList<Bitmap> images;
    private ArrayList<String> comments;



    public static ImageDialogFragment newInstance(int position, ArrayList<Bitmap> images, ArrayList<String> comments) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelableArrayList(ARG_IMAGES, images);
        args.putStringArrayList(ARG_COMMENTS, comments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            // 저장된 인스턴스 상태에서 댓글 복원
            comments = savedInstanceState.getStringArrayList(ARG_COMMENTS);
        }

        if (comments == null) {
            // 저장된 인스턴스 상태에서 댓글을 복원하지 않았다면 초기화
            comments = new ArrayList<>();
        }

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


    private void notifyCommentChanged() {
        // CommentChangeListener가 설정되어 있을 때만 호출
        if (commentChangeListener != null) {
            commentChangeListener.onCommentChanged(new ArrayList<>(comments));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView dialogImageView = view.findViewById(R.id.dialog_image_view);
        LinearLayout layoutComments = view.findViewById(R.id.layoutComments);
        EditText editTextSchedule = view.findViewById(R.id.editTextSchedule);
        ImageButton addButton = view.findViewById(R.id.addButton);
        ImageView closeButton = view.findViewById(R.id.close_button);

        // 이미지 목록을 프래그먼트에 전달
        Bitmap clickedImage = images.get(position);
        dialogImageView.setImageBitmap(clickedImage);

        // 기존 댓글 목록 추가
        addExistingComments(layoutComments, comments);

        addButton.setOnClickListener(v -> {
            // + 버튼 클릭 시 동적으로 메모 추가
            String commentText = editTextSchedule.getText().toString();
            if (!commentText.isEmpty()) {
                addComment(layoutComments, commentText);
                editTextSchedule.setText(""); // 입력창 초기화

                // Update the comments list when a new comment is added
                comments.add(commentText);

                // 댓글이 변경되었음을 알림
                notifyCommentChanged();
            }
        });

        closeButton.setOnClickListener(v -> {
            // X 버튼 클릭 시 프래그먼트 닫기
            getParentFragmentManager().beginTransaction()
                    .remove(ImageDialogFragment.this) // 현재 프래그먼트를 제거
                    .addToBackStack(null)
                    .commit();
        });


        // 페이드인 애니메이션 적용
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        dialogImageView.startAnimation(fadeIn);


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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 댓글을 인스턴스 상태에 저장
        outState.putStringArrayList(ARG_COMMENTS, comments);
    }

    // 기존 댓글 목록 추가하는 함수
    private void addExistingComments(LinearLayout layoutComments, ArrayList<String> comments) {
        for (String commentText : comments) {
            addComment(layoutComments, commentText);
        }
    }
}

