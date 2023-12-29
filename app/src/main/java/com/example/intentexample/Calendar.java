package com.example.intentexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Calendar extends Fragment {

    private MaterialCalendarView materialCalendarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        materialCalendarView = view.findViewById(R.id.calendarView);

        // Select today's date by default
        CalendarDay today = CalendarDay.today();
        materialCalendarView.setSelectedDate(today);

        return view;
    }
}