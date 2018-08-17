package com.mfathy.apptrack.presentation;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mfathy.apptrack.R;
import com.mfathy.apptrack.presentation.ui.applist.AppListFragment;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * Application starting point.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentById(R.id.fragmentLayout) == null) {
            AppListFragment list = new AppListFragment();
            fm.beginTransaction().add(R.id.fragmentLayout, list).commit();
        }
    }
}
