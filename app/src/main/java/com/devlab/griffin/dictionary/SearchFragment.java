package com.devlab.griffin.dictionary;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
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
        mViewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(tabsList.get(position))
        );
        tabLayoutMediator.attach();
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
            VocabFragment fragment = VocabFragment.newInstance("" + position + 1);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}