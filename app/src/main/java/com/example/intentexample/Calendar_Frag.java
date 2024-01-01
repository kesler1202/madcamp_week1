    package com.example.intentexample;

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.util.TypedValue;
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

    import org.json.JSONArray;
    import org.json.JSONException;


    public class Calendar_Frag extends Fragment {

        private MaterialCalendarView materialCalendarView;
        private SharedPreferences sharedPreferences;
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

            sharedPreferences = getActivity().getSharedPreferences("CalendarPlans", Context.MODE_PRIVATE);

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
                        savePlanToSharedPreferences(selectedDate, planText);
                        updateScrollViewForDate(selectedDate);
                        updateCalendarWithEvents(false);
                        editTextSchedule.setText(""); // Clear the input box
                    }
                }
            });
            updateCalendarWithEvents(false);
            return view;
        }
        private void savePlanToSharedPreferences(CalendarDay date, String planText) {
            List<String> plans = getPlansForDate(date);
            plans.add(planText);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONArray jsonArray = new JSONArray(plans);
            editor.putString(date.toString(), jsonArray.toString());
            editor.apply();
        }
        private List<String> getPlansForDate(CalendarDay date) {
            List<String> plans = new ArrayList<>();
            String json = sharedPreferences.getString(date.toString(), null);

            if (json != null) {
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        plans.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return plans;
        }
        private void updateCalendarWithEvents(boolean refreshImmediately) {
            Set<CalendarDay> datesWithEvents = new HashSet<>();

            // Define the range of dates to check. For example, current year
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.DAY_OF_YEAR, 1); // Start from the first day of the year
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(Calendar.DAY_OF_YEAR, startCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // End on the last day of the year

            while (startCalendar.before(endCalendar)) {
                CalendarDay day = CalendarDay.from(startCalendar);
                List<String> plans = getPlansForDate(day);
                if (!plans.isEmpty()) {
                    datesWithEvents.add(day);
                }
                startCalendar.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day
            }

            EventDecorator decorator = new EventDecorator(datesWithEvents);
            materialCalendarView.removeDecorators();
            materialCalendarView.addDecorator(decorator);
            if (refreshImmediately) {
                materialCalendarView.invalidateDecorators(); // Refresh the calendar view immediately
            }
        }
        public void onResume() {
            super.onResume();
            if (materialCalendarView != null) {
                updateCalendarWithEvents(true);
                CalendarDay selectedDate = materialCalendarView.getSelectedDate();
                if (selectedDate != null) {
                    updateScrollViewForDate(selectedDate);
                }
            }
        }
        private void updateScrollViewForDate(CalendarDay date) {
            scrollViewLayout.removeAllViews();
            List<String> plans = getPlansForDate(date);
            for (String plan : plans) {
                addPlanToScrollView(plan, date);
            }
        }

        private void addPlanToScrollView(String planText, CalendarDay date) {
            TextView planView = new TextView(getActivity());
            planView.setText(planText);

            // Define your desired padding, text size, and margin
            int padding = getResources().getDimensionPixelSize(R.dimen.plan_padding); // Define this in dimens.xml
            int textSize = getResources().getDimensionPixelSize(R.dimen.plan_text_size); // Define this in dimens.xml
            int verticalMargin = getResources().getDimensionPixelSize(R.dimen.vertical_margin); // Set this to 2dp in dimens.xml
            int extraTopMargin = getResources().getDimensionPixelSize(R.dimen.extra_top_margin); // Set this to 1dp in dimens.xml

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (scrollViewLayout.getChildCount() == 0) {
                // If first plan view, set top margin to verticalMargin + extraTopMargin
                layoutParams.setMargins(0, verticalMargin + extraTopMargin, 0, verticalMargin);
            } else {
                // If not the first, set only the bottom margin
                layoutParams.setMargins(0, 0, 0, verticalMargin);
            }

            planView.setLayoutParams(layoutParams);
            planView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rectangular_plan)); // Your drawable
            planView.setTextColor(Color.WHITE);
            planView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            planView.setPadding(padding, padding, padding, padding);

            ImageButton deleteButton = new ImageButton(getActivity());
            deleteButton.setImageResource(R.drawable.red_trash); // Use your red_trash.png here
            deleteButton.setBackgroundColor(Color.TRANSPARENT);

            // Create a horizontal layout to hold the plan text and delete button
            LinearLayout horizontalLayout = new LinearLayout(getActivity());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams planViewParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f); // 1f weight
            planView.setLayoutParams(planViewParams);

            // Set layout parameters for deleteButton
            LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteButton.setLayoutParams(deleteButtonParams);

            horizontalLayout.addView(planView);
            horizontalLayout.addView(deleteButton);


            // Set OnClickListener for delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deletePlanFromSharedPreferences(date, planText);
                    updateScrollViewForDate(date);
                    updateCalendarWithEvents(true);
                }
            });

            // Add horizontal layout to scrollViewLayout
            scrollViewLayout.addView(horizontalLayout);
        }
        private void deletePlanFromSharedPreferences(CalendarDay date, String planText) {
            List<String> plans = getPlansForDate(date);
            plans.remove(planText);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            JSONArray jsonArray = new JSONArray(plans);
            editor.putString(date.toString(), jsonArray.toString());
            editor.apply();
            updateCalendarWithEvents(true);
        }
    }