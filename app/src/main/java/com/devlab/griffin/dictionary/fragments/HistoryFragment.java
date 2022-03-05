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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devlab.griffin.dictionary.HistorySavedActivity;
import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.adapters.HistoryAdapter;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.interfaces.FragmentParentEventListener;

public class HistoryFragment extends Fragment implements HistoryAdapter.HistoryAdapterOnClickListener{

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private RecyclerView mHistoryRecyclerView;
    private HistoryAdapter mHistoryAdapter;
    private LinearLayout mErrorView;

    private Context mContext;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.rv_history_words);
        mErrorView = (LinearLayout) view.findViewById(R.id.error_display);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mHistoryRecyclerView.setLayoutManager(layoutManager);
        mHistoryRecyclerView.setHasFixedSize(false);

        mHistoryAdapter = new HistoryAdapter(this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);

        loadHistoryData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void showError() {
        mErrorView.setVisibility(View.VISIBLE);
        mHistoryRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        mErrorView.setVisibility(View.INVISIBLE);
        mHistoryRecyclerView.setVisibility(View.VISIBLE);
    }

    public void loadHistoryData() {
        new HistoryListTask().execute();
    }

    @Override
    public void onClick(long wordId, String word) {
        Class destinationClass = HistorySavedActivity.class;
        Intent intentToStartHistorySavedActivity = new Intent(mContext, destinationClass);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_WORD_ID, wordId);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_WORD, word);
        intentToStartHistorySavedActivity.putExtra(Constants.INTENT_KEY_SCREEN_TYPE, Constants.SCREEN_TYPE_HISTORY_WORD);
        startActivityForResult(intentToStartHistorySavedActivity, 0);
    }

    public class HistoryListTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor cursor = DictionaryQueryAgent.GetAllHistoryWordsList();
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if(cursor != null) {
                mHistoryAdapter.updateHistoryListFromCursor(cursor);
                showData();
            } else {
                showError();
            }
            super.onPostExecute(cursor);
        }
    }
}