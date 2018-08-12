package com.mfathy.apptrack;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(R.id.fragmentLayout) == null) {
            AppListFragment list = new AppListFragment();
            fm.beginTransaction().add(R.id.fragmentLayout, list).commit();
        }
    }
}
