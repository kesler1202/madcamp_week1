package com.example.intentexample; // Use your own package name

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.*;

import androidx.fragment.app.Fragment;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myprofile, container, false);

        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextPhone = view.findViewById(R.id.editTextPhone);
        EditText editTextSchool = view.findViewById(R.id.editTextSchool);
        imageProfile = view.findViewById(R.id.imageProfile);

        // Load saved data
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String photoPath = sharedPreferences.getString("photoPath", null);
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String school = sharedPreferences.getString("school", "");

        editTextName.setText(name);
        editTextPhone.setText(phone);
        editTextSchool.setText(school);

        if (photoPath != null) {
            imageProfile.setImageURI(Uri.parse(photoPath));
        }

        Button buttonUploadPhoto = view.findViewById(R.id.buttonUploadPhoto);
        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void saveProfileData() {
        EditText editTextName = getView().findViewById(R.id.editTextName);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextSchool = getView().findViewById(R.id.editTextSchool);

        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String school = editTextSchool.getText().toString();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("school", school);
        editor.apply();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                String imagePath = saveImageToInternalStorage(imageUri);
                SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("photoPath", imagePath);
                editor.apply();
                imageProfile.setImageURI(Uri.parse(imagePath));
            } catch (IOException e) {
                Log.e("MyProfileFragment", "Error saving image", e);
            }
        }
    }

    private String saveImageToInternalStorage(Uri uri) throws IOException {
        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
        File file = new File(getActivity().getFilesDir(), "profile_image.jpg");
        OutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return file.getAbsolutePath();
    }
}