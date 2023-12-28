package com.example.intentexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            final int tabCallId = R.id.tab_call;
            final int tabMessageId = R.id.tab_message;
            final int tabCameraId = R.id.tab_camera;

            if (item.getItemId() == tabCallId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();
                return true;
            } else if (item.getItemId() == tabMessageId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();
                return true;
            } else if (item.getItemId() == tabCameraId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                return true;
            } else {
                return false;
            }
        });
    }
}
