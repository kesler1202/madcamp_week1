package com.example.intentexample;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
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
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ImageDialogFragment extends DialogFragment {
    public interface CommentChangeListener {
        void onCommentChanged(ArrayList<String> updatedComments);
    }

    private CommentChangeListener commentChangeListener;

    private static final String ARG_POSITION = "position";
    private static final String ARG_IMAGES = "images";
    private static final String ARG_COMMENTS = "comments";

    private int position;
    private ArrayList<Bitmap> images;
    private ArrayList<String> comments;
    private ArrayList<String> loadCommentsFromSharedPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // 문자열의 Set을 불러와 ArrayList로 변환
        Set<String> commentsSet = preferences.getStringSet("comments", new HashSet<>());
        return new ArrayList<>(commentsSet);
    }


    public static ImageDialogFragment newInstance(int position, ArrayList<Bitmap> images, ArrayList<String> comments) {
        ImageDialogFragment fragment = new ImageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelableArrayList(ARG_IMAGES, images);
        args.putStringArrayList(ARG_COMMENTS, comments);
        fragment.setArguments(args);
        return fragment;
    }

    private void saveCommentsToSharedPreferences(ArrayList<String> comments) {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // ArrayList를 문자열의 Set으로 변환하여 저장
        Set<String> commentsSet = new HashSet<>(comments);
        editor.putStringSet("comments", commentsSet);

        editor.apply();
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            comments = savedInstanceState.getStringArrayList(ARG_COMMENTS);
        }

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            images = getArguments().getParcelableArrayList(ARG_IMAGES);
            if (comments == null) {
                // getArguments()에서 댓글을 로드하고, 사용 가능하지 않은 경우 SharedPreferences에서 불러옴
                comments = getArguments().getStringArrayList(ARG_COMMENTS);
                if (comments == null) {
                    // 수정된 부분: loadCommentsFromSharedPreferences()의 반환값을 comments에 할당
                    comments = loadCommentsFromSharedPreferences();
                }
            }
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

        PhotoView dialogPhotoView = view.findViewById(R.id.dialog_photo_view);
        LinearLayout layoutComments = view.findViewById(R.id.layoutComments);
        EditText editTextSchedule = view.findViewById(R.id.editTextSchedule);
        ImageButton addButton = view.findViewById(R.id.addButton);
        ImageView closeButton = view.findViewById(R.id.close_button);

        Bitmap clickedImage = images.get(position);
        dialogPhotoView.setImageBitmap(clickedImage);

        addExistingComments(layoutComments, comments);

        addButton.setOnClickListener(v -> {
            String commentText = editTextSchedule.getText().toString();
            if (!commentText.isEmpty()) {
                addComment(layoutComments, commentText);
                editTextSchedule.setText("");

                comments.add(commentText);

                notifyCommentChanged();
            }
        });

        closeButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .remove(ImageDialogFragment.this)
                    .addToBackStack(null)
                    .commit();
        });

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        dialogPhotoView.startAnimation(fadeIn);
    }

    private void notifyCommentChanged() {
        if (commentChangeListener != null) {
            commentChangeListener.onCommentChanged(new ArrayList<>(comments));

            // 댓글을 SharedPreferences에 저장
            saveCommentsToSharedPreferences(comments);
        }
    }


    private void addComment(LinearLayout layoutComments, String commentText) {
        TextView commentTextView = new TextView(requireContext());
        commentTextView.setText(commentText);
        commentTextView.setTextColor(Color.WHITE);
        // 텍스트 크기를 설정
        commentTextView.setTextSize(16);

        // 색상 및 모서리가 둥근 형태의 박스 스타일을 적용
        commentTextView.setBackgroundResource(R.drawable.rounded_box);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(50, 0, 50, 16);
        commentTextView.setLayoutParams(params);

        layoutComments.addView(commentTextView);
    }

    private void addExistingComments(LinearLayout layoutComments, ArrayList<String> comments) {
        for (String commentText : comments) {
            addComment(layoutComments, commentText);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(ARG_COMMENTS, comments);
    }

    public void setCommentChangeListener(CommentChangeListener listener) {
        this.commentChangeListener = listener;
    }
}