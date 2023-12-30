package com.example.intentexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhoneBook extends Fragment {
    ListView listView;
    ArrayList<Contact> contactList;
    ArrayAdapter<Contact> adapter;
    ArrayList<Contact> originalContactList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phonebook, container, false);
        listView = view.findViewById(R.id.listView);
        contactList = loadContacts();
        originalContactList = new ArrayList<>(contactList);

        adapter = new ArrayAdapter<Contact>(getContext(), R.layout.list_item, R.id.textViewItem, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = adapter.getItem(position);
                showContactDetailsDialog(selectedContact);
            }
        });

        EditText searchBox = view.findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterContacts(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });
        return view;
    }

    private void filterContacts(String text) {
        ArrayList<Contact> filteredList = new ArrayList<>();

        for (Contact contact : originalContactList) {
            if (contact.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(contact);
            }
        }

        // Update the adapter with the filtered list
        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private void showContactDetailsDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        builder.setTitle("Detail");

        // Create the message showing contact details
        String message = "Name: " + contact.getName() + "\n" +
                "Phone: " + contact.getPhone() + "\n" +
                "School: " + contact.getSchool() + "\n\n" +
                contact.getMemo();
        builder.setMessage(message);

        // Modify button
        builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Implement the functionality to modify the contact
                // showModifyContactDialog(contact);
            }
        });

        // Delete button
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContact(contact);
            }
        });

        // Cancel button
        builder.setNeutralButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void deleteContact(final Contact contact) {
        // Create an AlertDialog for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Contact");
        builder.setMessage("Are you sure you want to delete this contact?");

        // YES button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the actual deletion
                performDeletion(contact);
            }
        });

        // NO button
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Just close the dialog
            }
        });

        builder.show();
    }

    private void performDeletion(Contact contact) {
        // Remove the contact from your data list
        contactList.remove(contact);

        // Update the JSON in storage
        Gson gson = new Gson();
        String json = gson.toJson(contactList);
        saveJsonToStorage(json);

        // Refresh the ListView
        adapter.notifyDataSetChanged();
    }


    private void saveJsonToStorage(String json) {
        try {
            FileOutputStream fos = getContext().openFileOutput("Profile_Internal.json", Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(json);
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contacts;
        File file = new File(getContext().getFilesDir(), "Profile_Internal.json");
        if (file.exists()) {
            // Load from internal storage
            contacts = loadContactsFromInternalStorage();
        } else {
            // Load from assets
            contacts = loadContactsFromAssets();
        }
        return contacts;
    }

    private ArrayList<Contact> loadContactsFromInternalStorage() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            FileInputStream fis = getContext().openFileInput("Profile_Internal.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(builder.toString(), listType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private ArrayList<Contact> loadContactsFromAssets() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            InputStream is = getContext().getAssets().open("Profile.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(json, listType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return contacts;
    }
}