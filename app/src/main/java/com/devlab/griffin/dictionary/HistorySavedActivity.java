package com.devlab.griffin.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.data.HistoryContract.HistoryEntry;
import com.devlab.griffin.dictionary.data.SavedContract.SavedEntry;
import com.devlab.griffin.dictionary.fragments.SearchFragment;
import com.devlab.griffin.dictionary.interfaces.FragmentParentEventListener;
import com.devlab.griffin.dictionary.models.DictionaryEntry;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;

public class HistorySavedActivity extends AppCompatActivity implements FragmentParentEventListener {

    private static final String TAG = HistorySavedActivity.class.getSimpleName();

    private long mWordId;
    private String mWord;
    private String mScreenType;
    private boolean searchLoaded = false, meaningsLoaded = false, onymsLoaded = false, slangsLoaded = false;

    private SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_history_saved);

        Intent triggerIntent = getIntent();
        if (triggerIntent != null && triggerIntent.hasExtra(Constants.INTENT_KEY_WORD_ID) && triggerIntent.hasExtra(Constants.INTENT_KEY_WORD)) {
            mWordId = triggerIntent.getLongExtra(Constants.INTENT_KEY_WORD_ID, 0);
            mWord = triggerIntent.getStringExtra(Constants.INTENT_KEY_WORD);

            mScreenType = Constants.SCREEN_TYPE_HISTORY_WORD;
            if(triggerIntent.hasExtra(Constants.INTENT_KEY_SCREEN_TYPE))
                mScreenType = triggerIntent.getStringExtra(Constants.INTENT_KEY_SCREEN_TYPE);

            createSearchFragment();

        } else {
            Log.e(TAG, "onCreate: empty intent or no word id. Exiting activity");
            finish();
        }
    }

    private void createSearchFragment() {
        if (mScreenType.equals(Constants.SCREEN_TYPE_HISTORY_WORD)) {
            mSearchFragment = SearchFragment.newInstance(Constants.BUTTON_STATE_DONE, Constants.BUTTON_STATE_SAVE);
        } else if (mScreenType.equals(Constants.SCREEN_TYPE_SAVED_WORD)) {
            mSearchFragment = SearchFragment.newInstance(Constants.BUTTON_STATE_DONE, Constants.BUTTON_STATE_DELETE);
        } else {
            Log.e(TAG, "createSearchFragment: Invalid mScreenType " + mScreenType);
            finish();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.history_saved_fragment_container, mSearchFragment).show(mSearchFragment).commitNow();
    }

    private void updateFragmentEditTextState() {
        mSearchFragment.setEditTextWordAndState(mWord, false);
    }

    @Override
    public void passEventData(String event, String data) {
        if(event.equals(Constants.EVENT_SEARCH_FRAGMENT_LOADED) || event.equals(Constants.EVENT_VOCAB_FRAGMENT_LOADED)) {
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
                loadDictionaryEntryIntoFragment();
            }
        }
    }

    private void loadDictionaryEntryIntoFragment() {
        System.out.println("SCREEN TYPE: " + mScreenType);
        if(mScreenType.equals(Constants.SCREEN_TYPE_HISTORY_WORD)) {
            new LoadWordFromDbTask().execute(HistoryEntry.TABLE_NAME, mWord);
        } else {
            new LoadWordFromDbTask().execute(SavedEntry.TABLE_NAME, mWord);
        }
    }

    public class LoadWordFromDbTask extends AsyncTask<String, Void, DictionaryEntry> {

        @Override
        protected void onPreExecute() {
            mSearchFragment.startUiLoading();
            super.onPreExecute();
        }

        @SuppressLint("Range")
        @Override
        protected DictionaryEntry doInBackground(String... params) {

            if(params.length < 2) {
                return null;
            }

            String table = params[0];
            String word = params[1].toLowerCase();
            String meanings, onyms, slangs;

            System.out.println("TABLE: " + table);

            if(table.equals(HistoryEntry.TABLE_NAME)) {
                System.out.println("HISTORY WORD: " + word);
                Cursor cursor = DictionaryQueryAgent.GetHistoryEntryByWord(word);
                System.out.println("HISTORY CURSOR COUNT: " + cursor.getCount());
                if(cursor == null || cursor.getCount() < 1 || !cursor.moveToNext()) {
                    return null;
                }

                meanings = cursor.getString(cursor.getColumnIndex(HistoryEntry.COLUMN_MEANINGS_JSON));
                onyms = cursor.getString(cursor.getColumnIndex(HistoryEntry.COLUMN_ONYMS_JSON));
                slangs = cursor.getString(cursor.getColumnIndex(HistoryEntry.COLUMN_SLANGS_JSON));

                cursor.close();
            }
            else {
                System.out.println("SAVED WORD: " + word);
                Cursor cursor = DictionaryQueryAgent.GetSavedEntryByWord(word);
                System.out.println("SAVED CURSOR COUNT: " + cursor.getCount());
                if(cursor == null || cursor.getCount() < 1 || !cursor.moveToNext()) {
                    return null;
                }

                meanings = cursor.getString(cursor.getColumnIndex(SavedEntry.COLUMN_MEANINGS_JSON));
                onyms = cursor.getString(cursor.getColumnIndex(SavedEntry.COLUMN_ONYMS_JSON));
                slangs = cursor.getString(cursor.getColumnIndex(SavedEntry.COLUMN_SLANGS_JSON));

                cursor.close();
            }

            mSearchFragment.setDictionaryStrings(meanings, onyms, slangs);

            return JsonParsingUtils.parseDictionaryEntry(meanings, onyms, slangs);
        }

        @Override
        protected void onPostExecute(DictionaryEntry dictionaryEntry) {
            mSearchFragment.stopUiLoadingAndSetDictionaryEntry(dictionaryEntry);
            mSearchFragment.setNestedScrollViewPadding(8);
            super.onPostExecute(dictionaryEntry);
        }
    }
}