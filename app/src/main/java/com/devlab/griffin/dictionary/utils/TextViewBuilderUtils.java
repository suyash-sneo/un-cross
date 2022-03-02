package com.devlab.griffin.dictionary.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.devlab.griffin.dictionary.R;

public class TextViewBuilderUtils {

    @NonNull
    public static TextView preparePartOfSpeechTextView(String partOfSpeech, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(context.getColor(R.color.purple_500));
        textView.setText(partOfSpeech);
        return textView;
    }

    @NonNull
    public static TextView prepareMeaningDefinitionTextView(int position, String definition, Context context) {
        String definitionString = (position+1) + ". " + definition;
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(48, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(definitionString);
        return textView;
    }

    @NonNull
    public static TextView prepareMeaningExampleTextView(String example, Context context) {
        String exampleString =  "e.g. " + example;
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.ITALIC);
        textView.setPadding(96, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(exampleString);
        return textView;
    }

    @NonNull
    public static TextView prepareOnymsHeadingTextView(String heading, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(16);
        textView.setAllCaps(true);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(48, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(heading);
        return textView;
    }

    @NonNull
    public static TextView prepareOnymsTextView(int position, String definition, Context context) {
        String definitionString = (position+1) + ". " + definition;
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setPadding(96, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(definitionString);
        return textView;
    }

    @NonNull
    public static TextView prepareSlangsDefinition(int position, String definition, Context context) {
        String definitionString = (position+1) + ". " + definition;
        TextView textView = new TextView(context);
        textView.setTextSize(16);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(48, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(definitionString);
        return textView;
    }

    @NonNull
    public static TextView prepareSlangsExample(String example, Context context) {
        String exampleString = "e.g. " + example;
        TextView textView = new TextView(context);
        textView.setTextSize(14);
        textView.setTypeface(null, Typeface.ITALIC);
        textView.setPadding(96, 0, 0, 0);
        textView.setTextColor(context.getColor(R.color.black));
        textView.setText(exampleString);
        return textView;
    }
}
