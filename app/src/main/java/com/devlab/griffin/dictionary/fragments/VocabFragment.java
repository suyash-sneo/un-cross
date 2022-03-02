package com.devlab.griffin.dictionary.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.models.MeaningDefinitionExample;
import com.devlab.griffin.dictionary.models.Onyms;
import com.devlab.griffin.dictionary.models.UdDefinitionExample;
import com.devlab.griffin.dictionary.utils.TextViewBuilderUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VocabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VocabFragment extends Fragment {

    private static final String ARG_PARAM1 = "vocab";

    private String mVocabState;
    private LinearLayout mVocabLayout;
    private NestedScrollView mScrollView;

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
            mVocabState = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vocab, container, false);

        // Get TextView and Populate it
        mVocabLayout = (LinearLayout) view.findViewById(R.id.data_layout);
        mScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);

        return view;
    }

    private void resetContent() {
        mScrollView.smoothScrollTo(0, 0);
        mVocabLayout.removeAllViewsInLayout();
    }

    private void appendViewToLayout(View view) {
        mVocabLayout.addView(view);
    }

    public void setMeanings(HashMap<String, ArrayList<MeaningDefinitionExample>> meanings, Context context) {
        if(mVocabState.equals(Constants.MEANING_FRAGMENT_STATE) && meanings != null && meanings.size() > 0) {
            resetContent();
            meanings.forEach((key, value) -> {
                appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView(key, context));
                for(int i=0; i<value.size(); i++) {
                    MeaningDefinitionExample mde = value.get(i);
                    if(mde != null) {
                        appendViewToLayout(TextViewBuilderUtils.prepareMeaningDefinitionTextView(i, mde.getDefinition(), context));
                        if(mde.getExample() != null && mde.getExample().length()>0) {
                            appendViewToLayout(TextViewBuilderUtils.prepareMeaningExampleTextView(mde.getExample(), context));
                        }
                    }
                }
                appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView("", context));
            });
        }
    }

    public void setOnyms(HashMap<String, Onyms> onyms, Context context) {
        if(mVocabState.equals(Constants.ONYMS_FRAGMENT_STATE) && onyms != null && onyms.size() > 0) {
            resetContent();
            onyms.forEach((key, value) -> {
                appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView(key, context));
                ArrayList<String> synonyms = value.getSynonyms();
                if(synonyms != null && synonyms.size() > 0) {
                    appendViewToLayout(TextViewBuilderUtils.prepareOnymsHeadingTextView("synonyms", context));
                    for(int i=0; i< synonyms.size(); i++) {
                        appendViewToLayout(TextViewBuilderUtils.prepareOnymsTextView(i, synonyms.get(i), context));
                    }
                }
                ArrayList<String> antonyms = value.getAntonyms();
                if(antonyms != null && antonyms.size() > 0) {
                    appendViewToLayout(TextViewBuilderUtils.prepareOnymsHeadingTextView("antonyms", context));
                    for(int i=0; i< antonyms.size(); i++) {
                        appendViewToLayout(TextViewBuilderUtils.prepareOnymsTextView(i, antonyms.get(i), context));
                    }
                }
                appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView("", context));
            });
        }
    }

    public void setSlangs(ArrayList<UdDefinitionExample> slangs, Context context) {
        if(mVocabState.equals(Constants.SLANGS_FRAGMENT_STATE) && slangs != null && slangs.size() > 0) {
            resetContent();
            appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView("urban dictionary\n", context));

            for(int i=0; i<slangs.size(); i++) {
                UdDefinitionExample ude = slangs.get(i);
                if (ude != null && ude.getDefinition() != null && ude.getDefinition().length() > 0) {
                    appendViewToLayout(TextViewBuilderUtils.prepareSlangsDefinition(i, ude.getDefinition(), context));
                    if(ude.getExample() != null && ude.getExample().length()>0) {
                        appendViewToLayout(TextViewBuilderUtils.prepareSlangsExample(ude.getExample(), context));
                    }

                    appendViewToLayout(TextViewBuilderUtils.preparePartOfSpeechTextView("", context));
                }
            }
        }
    }
}