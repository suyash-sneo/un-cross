package com.devlab.griffin.dictionary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryDbHelper;
import com.devlab.griffin.dictionary.data.DictionaryDefaultData;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.fragments.HistoryFragment;
import com.devlab.griffin.dictionary.fragments.SavedFragment;
import com.devlab.griffin.dictionary.fragments.SearchFragment;
import com.devlab.griffin.dictionary.interfaces.FragmentParentEventListener;
import com.devlab.griffin.dictionary.models.DictionaryEntry;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, FragmentParentEventListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public BottomNavigationView mNavigationView;
    public SearchFragment mSearchFragment;
    public HistoryFragment mHistoryFragment;
    public SavedFragment mSavedFragment;
    public Fragment activeFragment;

    private boolean searchLoaded = false, meaningsLoaded = false, onymsLoaded = false, slangsLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        mSearchFragment = SearchFragment.newInstance(Constants.BUTTON_STATE_CLEAR, Constants.BUTTON_STATE_SAVE);
        mHistoryFragment = new HistoryFragment();
        mSavedFragment = new SavedFragment();

        activeFragment = mSearchFragment;

        getSupportFragmentManager().beginTransaction().
                add(R.id.fragment_container, mSavedFragment).hide(mSavedFragment).
                add(R.id.fragment_container, mHistoryFragment).hide(mHistoryFragment).
                add(R.id.fragment_container, mSearchFragment).commit();

        mNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mNavigationView.setOnItemSelectedListener(this);
        mNavigationView.setSelectedItemId(R.id.page_nav_search);

        DictionaryQueryAgent.initializeDb(this);
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if(view == null)
            view = new View(this);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        hideSoftKeyboard();
        int itemId = item.getItemId();

        if(itemId == R.id.page_nav_search) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(mSearchFragment).commit();
            activeFragment = mSearchFragment;
            return true;
        }
        else if (itemId == R.id.page_nav_history) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(mHistoryFragment).commit();
            activeFragment = mHistoryFragment;
            return true;
        }
        else if (itemId == R.id.page_nav_saved) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(mSavedFragment).commit();
            activeFragment = mSavedFragment;
            return true;
        }
        else {
            Log.e(TAG, "onNavigationItemSelected: Invalid item id " + itemId);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mSavedFragment.loadSavedData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DictionaryQueryAgent.closeDb();
    }

    private void updateFragmentEditTextState() {
        mSearchFragment.setEditTextWordAndState(DictionaryDefaultData.defaultWord, true);
    }

    private void putDictionaryEntryIntoFragment(DictionaryEntry dictionaryEntry) {
        mSearchFragment.stopUiLoadingAndSetDictionaryEntry(dictionaryEntry);
    }

    @Override
    public void passEventData(String event, String data) {
        if (event.equals(Constants.EVENT_WORD_FETCHED)) {
            mHistoryFragment.loadHistoryData();
        } else if (event.equals(Constants.EVENT_WORD_SAVED)) {
            mSavedFragment.loadSavedData();
        } else if(event.equals(Constants.EVENT_SEARCH_FRAGMENT_LOADED) || event.equals(Constants.EVENT_VOCAB_FRAGMENT_LOADED)) {
            if(event.equals(Constants.EVENT_SEARCH_FRAGMENT_LOADED)) {
                searchLoaded = true;
                updateFragmentEditTextState();
            }
            else {
                if (data.equals(Constants.MEANING_FRAGMENT_STATE))
                    meaningsLoaded = true;
                else if (data.equals(Constants.ONYMS_FRAGMENT_STATE))
                    onymsLoaded = true;
                else if (data.equals(Constants.SLANGS_FRAGMENT_STATE))
                    slangsLoaded = true;
            }

            boolean allChildrenLoaded = searchLoaded & meaningsLoaded & onymsLoaded & slangsLoaded;
            if(allChildrenLoaded) {
                DictionaryEntry dictionaryEntry = JsonParsingUtils.parseDictionaryEntry(DictionaryDefaultData.defaultMeaning, DictionaryDefaultData.defaultOnyms, DictionaryDefaultData.defaultSlang);
                mSearchFragment.setDictionaryStrings(DictionaryDefaultData.defaultMeaning, DictionaryDefaultData.defaultOnyms, DictionaryDefaultData.defaultSlang);
                putDictionaryEntryIntoFragment(dictionaryEntry);
            }
        }
    }
}