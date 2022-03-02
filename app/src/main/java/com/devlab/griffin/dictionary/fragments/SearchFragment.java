package com.devlab.griffin.dictionary.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.models.DictionaryEntry;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;
import com.devlab.griffin.dictionary.utils.NetworkUtils;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;

    public VocabFragment meaningFragment, onymsFragment, slangsFragment;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //    mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mTabLayout = (TabLayout) view.findViewById(R.id.tl_vocab);
        mViewPager = (ViewPager2) view.findViewById(R.id.view_pager);

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
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Loader<DictionaryEntry> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<DictionaryEntry>(mContext) {

            @Override
            protected void onStartLoading() {
                if(args == null)
                    return;

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
                    String meaningsStr = NetworkUtils.getFreeDictionaryResponse(meaningsUrl);
                    String onymsStr = NetworkUtils.getBigThesaurusResponse(onymsUrl);
                    String slangsStr = NetworkUtils.getUrbanDictionaryResponse(slangsUrl);

                    DictionaryEntry dictionaryEntry = JsonParsingUtils.parseDictionaryEntry(meaningsStr, onymsStr, slangsStr);

                    return dictionaryEntry;
                }
                catch (Exception e) {
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
        meaningFragment.setMeanings(dictionaryEntry.getMeanings(), mContext);
        onymsFragment.setOnyms(dictionaryEntry.getOnyms(), mContext);
        slangsFragment.setSlangs(dictionaryEntry.getSlangs(), mContext);
        Toast toast = Toast.makeText(mContext, "FETCHED", Toast.LENGTH_LONG);
        toast.show();
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
}