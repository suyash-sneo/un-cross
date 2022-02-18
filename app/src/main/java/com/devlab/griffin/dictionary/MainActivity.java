package com.devlab.griffin.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.autofill.SaveCallback;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public BottomNavigationView mNavigationView;
    public SearchFragment mSearchFragment;
    public HistoryFragment mHistoryFragment;
    public SavedFragment mSavedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        mSearchFragment = new SearchFragment();
        mHistoryFragment = new HistoryFragment();
        mSavedFragment = new SavedFragment();

        mNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mNavigationView.setOnItemSelectedListener(this);
        mNavigationView.setSelectedItemId(R.id.page_nav_search);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.page_nav_search) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mSearchFragment).commit();
            return true;
        }
        else if (itemId == R.id.page_nav_history) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mHistoryFragment).commit();
            return true;
        }
        else if (itemId == R.id.page_nav_saved) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mSavedFragment).commit();
            return true;
        }
        else {
            Log.e(TAG, "onNavigationItemSelected: Invalid item id " + itemId);
            return false;
        }
    }
}