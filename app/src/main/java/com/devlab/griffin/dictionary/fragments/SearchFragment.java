package com.devlab.griffin.dictionary.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.models.DictionaryEntry;
import com.devlab.griffin.dictionary.tasks.SaveHistoryTask;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;
import com.devlab.griffin.dictionary.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<DictionaryEntry> {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int DICTIONARY_LOADER = 78;
    private static final String SEARCH_WORD_EXTRA = "word";
    private Context mContext;

    public EditText mSearchEditText;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_CLEAR_DONE_STATE = "clearDoneState";
    private static final String ARG_PARAM_SAVE_DELETE_STATE = "saveDeleteState";

    // TODO: Rename and change types of parameters
    private String mClearDoneState;
    private String mSaveDeleteState;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private MaterialButton mClearDoneButton, mSaveDeleteButton;
    private Toast mToast;

    public VocabFragment meaningFragment, onymsFragment, slangsFragment;

    private String meaningStr, onymsStr, slangsStr;
    private String mWord;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String clearDoneState, String saveDeleteState) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_CLEAR_DONE_STATE, clearDoneState);
        args.putString(ARG_PARAM_SAVE_DELETE_STATE, saveDeleteState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mClearDoneState = getArguments().getString(ARG_PARAM_CLEAR_DONE_STATE);
            mSaveDeleteState = getArguments().getString(ARG_PARAM_SAVE_DELETE_STATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.tl_vocab);
        mViewPager = (ViewPager2) view.findViewById(R.id.view_pager);
        mClearDoneButton = (MaterialButton) view.findViewById(R.id.clear_done_button);
        mSaveDeleteButton = (MaterialButton) view.findViewById(R.id.save_delete_button);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> tabsList = new ArrayList<>(0);
        tabsList.add(getString(R.string.tab_meaning_header));
        tabsList.add(getString(R.string.tab_onyms_header));
        tabsList.add(getString(R.string.tab_slang_header));

        VocabAdapter adapter = new VocabAdapter(this);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(tabsList.get(position))
        );
        tabLayoutMediator.attach();

        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        mSearchEditText.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
        mSearchEditText.setOnEditorActionListener(new  TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                    case EditorInfo.IME_ACTION_SEARCH:
                    case EditorInfo.IME_ACTION_NEXT:
                        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        String word = mSearchEditText.getText().toString();
                        mWord = word;
                        loadSearchResults(word);
                        mSearchEditText.clearFocus();
                        return true;
                }
                return false;
            }
        });
        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    mSearchEditText.setCursorVisible(true);
                } else {
                    mSearchEditText.setCursorVisible(false);
                }
            }
        });

        configureClearDoneButton();
        configureSaveDeleteButton();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void setDictionaryEntry(DictionaryEntry dictionaryEntry) {
        meaningFragment.setMeanings(dictionaryEntry.getMeanings(), mContext);
        onymsFragment.setOnyms(dictionaryEntry.getOnyms(), mContext);
        slangsFragment.setSlangs(dictionaryEntry.getSlangs(), mContext);
    }

    private void showLoadingInChildrenFragments() {
        meaningFragment.startLoading();
        onymsFragment.startLoading();
        slangsFragment.startLoading();
    }

    private void showErrorInChildrenFragments() {
        meaningFragment.showError();
        onymsFragment.showError();
        slangsFragment.showError();
    }

    private void configureClearDoneButton() {
        if(mClearDoneState.equals(Constants.BUTTON_STATE_CLEAR)) {
            mClearDoneButton.setIcon(AppCompatResources.getDrawable(mContext, R.drawable.clear));
            mClearDoneButton.setText(mContext.getString(R.string.clear_done_button_clear));
            mClearDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSearchEditText.setText("");
                }
            });

            mSaveDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SaveWordTask().execute(mWord, meaningStr, onymsStr, slangsStr);
                }
            });
        }
    }

    private void configureSaveDeleteButton() {
        if(mSaveDeleteState.equals(Constants.BUTTON_STATE_SAVE)) {
            mSaveDeleteButton.setIcon(AppCompatResources.getDrawable(mContext, R.drawable.button_save));
            mSaveDeleteButton.setText(mContext.getString(R.string.save_delete_button_save));
        }
    }

    private void showLongToast(String message) {
        if(mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @NonNull
    @Override
    public Loader<DictionaryEntry> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<DictionaryEntry>(mContext) {

            @Override
            protected void onStartLoading() {
                if(args == null)
                    return;

                meaningStr = onymsStr = slangsStr = null;
                mSaveDeleteButton.setEnabled(false);

                showLoadingInChildrenFragments();
                forceLoad();
            }

            @Nullable
            @Override
            public DictionaryEntry loadInBackground() {
                String word = args.getString(SEARCH_WORD_EXTRA);

                URL meaningsUrl = NetworkUtils.buildFreeDictionaryUrl(word);
                URL onymsUrl = NetworkUtils.buildBigHugeThesaurusUrl(word);
                URL slangsUrl = NetworkUtils.buildUrbanDictionaryUrl(word);

                try {
                    meaningStr = NetworkUtils.getFreeDictionaryResponse(meaningsUrl);
                    onymsStr = NetworkUtils.getBigThesaurusResponse(onymsUrl);
                    slangsStr = NetworkUtils.getUrbanDictionaryResponse(slangsUrl);

                    DictionaryEntry dictionaryEntry = JsonParsingUtils.parseDictionaryEntry(meaningStr, onymsStr, slangsStr);

                    return dictionaryEntry;
                }
                catch (Exception e) {
                    showErrorInChildrenFragments();
                    Log.e(TAG, "loadInBackground: Exception: ", e.fillInStackTrace());
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable DictionaryEntry dictionaryEntry) {
                super.deliverResult(dictionaryEntry);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<DictionaryEntry> loader, DictionaryEntry dictionaryEntry) {
        mSaveDeleteButton.setEnabled(true);
        new SaveHistoryTask().execute(mWord, meaningStr, onymsStr, slangsStr);
        setDictionaryEntry(dictionaryEntry);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<DictionaryEntry> loader) {
        // Let's not implement this for now
    }

    private void loadSearchResults(String word) {
        Bundle wordBundle = new Bundle();
        wordBundle.putString(SEARCH_WORD_EXTRA, word);

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> loader = loaderManager.getLoader(DICTIONARY_LOADER);
        if(loader == null) {
            loaderManager.initLoader(DICTIONARY_LOADER, wordBundle, this);
        }
        else{
            loaderManager.restartLoader(DICTIONARY_LOADER, wordBundle, this);
        }
    }

    private class VocabAdapter extends FragmentStateAdapter {

        ArrayList<Fragment> fragmentArrayList;
        ArrayList<String> stringArrayList;

        public VocabAdapter(Fragment fragment) {
            super(fragment);
            fragmentArrayList = new ArrayList<Fragment>();
            stringArrayList = new ArrayList<String>();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            VocabFragment fragment;

            switch(position) {
                case Constants.MEANING_FRAGMENT_POSITION:
                    fragment = VocabFragment.newInstance(Constants.MEANING_FRAGMENT_STATE);
                    meaningFragment = fragment;
                    break;
                case Constants.ONYMS_FRAGMENT_POSITION:
                    fragment = VocabFragment.newInstance(Constants.ONYMS_FRAGMENT_STATE);
                    onymsFragment = fragment;
                    break;
                case Constants.SLANGS_FRAGMENT_POSITION:
                    fragment = VocabFragment.newInstance(Constants.SLANGS_FRAGMENT_STATE);
                    slangsFragment = fragment;
                    break;
                default:
                    Log.e(TAG, "createFragment: Invalid position " + position);
                    fragment = null;
                    break;
            }

            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public class SaveWordTask extends AsyncTask<String, Void, Long> {

        @Override
        protected void onPreExecute() {
            mSaveDeleteButton.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... params) {

            if(params.length < 4) {
                return -1L;
            }

            String word = params[0].toLowerCase();
            String meanings = params[1];
            String onyms = params[2];
            String slangs = params[3];

            if(JsonParsingUtils.IsNullOrEmpty(word) ||
                    (
                            JsonParsingUtils.IsNullOrEmpty(meanings) &&
                                    JsonParsingUtils.IsNullOrEmpty(onyms) &&
                                    JsonParsingUtils.IsNullOrEmpty(slangs)
                    )
            ) {
                return -1L;
            }

            return DictionaryQueryAgent.SaveWordEntry(word, meanings, onyms, slangs);
        }

        @Override
        protected void onPostExecute(Long savedId) {
            mSaveDeleteButton.setEnabled(true);

            if(savedId < 0) {
                Log.e(TAG, "onPostExecute: Failed to save the word: " + mWord);
                showLongToast(Constants.ERROR_SAVE_FAILED);
            } else {
                showLongToast("Successfully saved");
            }

            super.onPostExecute(savedId);
        }
    }
}