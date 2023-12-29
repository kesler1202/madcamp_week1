package com.example.intentexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.time.DayOfWeek;
import java.util.Calendar;

public class Calendar_Frag extends Fragment {

    private MaterialCalendarView materialCalendarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        // Set the first day of the week
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .commit();

        return view;
    }
}