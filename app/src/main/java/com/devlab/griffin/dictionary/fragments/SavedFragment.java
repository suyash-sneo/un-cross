package com.devlab.griffin.dictionary.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devlab.griffin.dictionary.HistorySavedActivity;
import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.adapters.HistoryAdapter;
import com.devlab.griffin.dictionary.adapters.SavedAdapter;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;

public class SavedFragment extends Fragment implements SavedAdapter.SavedAdapterOnClickListener {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private RecyclerView mSavedRecyclerView;
    private SavedAdapter mSavedAdapter;

    private Context mContext;

    public SavedFragment() {
        // Required empty public constructor
    }

    public static SavedFragment newInstance() {
        SavedFragment fragment = new SavedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        mSavedRecyclerView = (RecyclerView) view.findViewById(R.id.rv_saved_words);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mSavedRecyclerView.setLayoutManager(layoutManager);
        mSavedRecyclerView.setHasFixedSize(false);

        mSavedAdapter = new SavedAdapter(this);
        mSavedRecyclerView.setAdapter(mSavedAdapter);

        loadSavedData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void loadSavedData() {
        new SavedListTask().execute();
    }

    @Override
    public void onClick(long wordId, String word) {
        Class destinationClass = HistorySavedActivity.class;
        Intent intentToStartHistorySavedActivity = new Intent(mContext, destinationClass);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_WORD_ID, wordId);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_WORD, word);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_SCREEN_TYPE, Constants.SCREEN_TYPE_SAVED_WORD);
        startActivityForResult(intentToStartHistorySavedActivity, 0);
    }

    public class SavedListTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = DictionaryQueryAgent.GetAllSavedWordsList();
            System.out.println("CURSOR COUNT: " + cursor.getCount());
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if(cursor != null) {
                mSavedAdapter.updateSavedListFromCursor(cursor);
            }
            super.onPostExecute(cursor);
        }
    }
}