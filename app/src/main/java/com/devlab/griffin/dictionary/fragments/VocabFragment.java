package com.devlab.griffin.dictionary.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devlab.griffin.dictionary.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VocabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VocabFragment extends Fragment {

    private static final String ARG_PARAM1 = "vocab";

    private String mVocabContent;
    private TextView mVocabContentTextView;

    public VocabFragment() {
        // Required empty public constructor
    }

    public static VocabFragment newInstance(String vocabContent) {
        VocabFragment fragment = new VocabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, vocabContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVocabContent = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vocab, container, false);

        // Get TextView and Populate it
        mVocabContentTextView = (TextView) view.findViewById(R.id.tv_vocab_content);
        mVocabContentTextView.setText(mVocabContent);

        return view;
    }
}