package com.example.intentexample;
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
            comments = savedInstanceState.getStringArrayList(ARG_COMMENTS);
        }

        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            images = getArguments().getParcelableArrayList(ARG_IMAGES);
            if (comments == null) {
                comments = getArguments().getStringArrayList(ARG_COMMENTS);
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