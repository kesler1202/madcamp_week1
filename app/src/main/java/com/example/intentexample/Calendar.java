package com.example.intentexample;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.CalendarView;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;


public class Calendar extends Fragment {

    private static final String TAG = "CalendarFragment";
    private CalendarView calendarView;
    private EditText editTextPlan;
    private Button saveButton;
    private SharedPreferences sharedPreferences;
    private Set<String> datesWithPlans;
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        editTextPlan = view.findViewById(R.id.editTextPlan);
        saveButton = view.findViewById(R.id.saveButton);

        sharedPreferences = getActivity().getSharedPreferences("CalendarPlans", Context.MODE_PRIVATE);
        datesWithPlans = new HashSet<>(sharedPreferences.getStringSet("datesWithPlans", new HashSet<>()));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                editTextPlan.setText(sharedPreferences.getString(selectedDate, ""));
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save button clicked");
                String plan = editTextPlan.getText().toString();
                Log.d(TAG, "Saving plan for date " + selectedDate + ": " + plan);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(selectedDate, plan);
                datesWithPlans.add(selectedDate);
                editor.putStringSet("datesWithPlans", datesWithPlans);
                editor.apply();

                Log.d(TAG, "Plan saved");
            }
        });

        return view;
    }
}