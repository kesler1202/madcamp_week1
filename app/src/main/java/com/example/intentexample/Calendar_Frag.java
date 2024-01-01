package com.example.intentexample;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class Calendar_Frag extends Fragment {

    private MaterialCalendarView materialCalendarView;
    private HashMap<CalendarDay, List<String>> dateToPlansMap = new HashMap<>();
    private EditText editTextSchedule;
    private LinearLayout scrollViewLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        materialCalendarView = view.findViewById(R.id.calendarView);
        materialCalendarView.setPadding(0, 60, 0, 0);
        editTextSchedule = view.findViewById(R.id.editTextSchedule);
        ImageButton addButton = view.findViewById(R.id.addButton);
        scrollViewLayout = view.findViewById(R.id.planContainer); // Replace with your actual LinearLayout ID

        // Set the first day of the week
        Calendar today = Calendar.getInstance();
        materialCalendarView.setSelectedDate(today);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .commit();
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                updateScrollViewForDate(date);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planText = editTextSchedule.getText().toString();
                if (!planText.isEmpty()) {
                    CalendarDay selectedDate = materialCalendarView.getSelectedDate();
                    List<String> plans = dateToPlansMap.getOrDefault(selectedDate, new ArrayList<>());
                    plans.add(planText);
                    dateToPlansMap.put(selectedDate, plans);
                    updateScrollViewForDate(selectedDate);
                    updateCalendarWithEvents();
                    editTextSchedule.setText(""); // Clear the input box
                }
            }
        });
        updateCalendarWithEvents();
        return view;
    }
    private void updateCalendarWithEvents() {
        Set<CalendarDay> datesWithEvents = new HashSet<>();

        for (CalendarDay date : dateToPlansMap.keySet()) {
            if (!dateToPlansMap.get(date).isEmpty()) {
                datesWithEvents.add(date);
            }
        }

        EventDecorator decorator = new EventDecorator(datesWithEvents);
        materialCalendarView.addDecorator(decorator);
    }
    public void onResume() {
        super.onResume();
        if (materialCalendarView != null) {
            updateCalendarWithEvents();
            CalendarDay selectedDate = materialCalendarView.getSelectedDate();
            if (selectedDate != null) {
                updateScrollViewForDate(selectedDate);
            }
        }
    }
    private void updateScrollViewForDate(CalendarDay date) {
        scrollViewLayout.removeAllViews();
        List<String> plans = dateToPlansMap.getOrDefault(date, new ArrayList<>());
        for (String plan : plans) {
            addPlanToScrollView(plan);
        }
    }

    private void addPlanToScrollView(String planText) {
        TextView planView = new TextView(getActivity());
        planView.setText(planText);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int verticalMargin = -1 * getResources().getDimensionPixelSize(R.dimen.border_width); // Adjust this value as needed
        layoutParams.setMargins(0, verticalMargin, 0, 0);
        planView.setLayoutParams(layoutParams);
        planView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rectangular_plan)); // Replace with your actual drawable
        planView.setTextColor(Color.WHITE); // Set text color (change as needed)
        planView.setTextSize(16);
        planView.setPadding(10, 10, 10, 10);

        scrollViewLayout.addView(planView);
    }
}