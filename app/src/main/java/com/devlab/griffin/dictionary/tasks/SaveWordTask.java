package com.devlab.griffin.dictionary.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;
import com.devlab.griffin.dictionary.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;

import java.lang.ref.WeakReference;

public class SaveWordTask extends AsyncTask<String, Void, Long> {

    private static final String TAG = SaveWordTask.class.getSimpleName();

    private final WeakReference<MaterialButton> buttonWeakReference;
    private final WeakReference<Context> contextWeakReference;
    private String mWord;

    public SaveWordTask(MaterialButton saveDeleteButton, Context context) {
        buttonWeakReference = new WeakReference<>(saveDeleteButton);
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        buttonWeakReference.get().setEnabled(false);
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

        mWord = word;

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
        buttonWeakReference.get().setEnabled(true);

        if(savedId < 0) {
            Log.e(TAG, "onPostExecute: Failed to save the word: " + mWord);
            ToastUtils.showLongToast(contextWeakReference.get(), Constants.ERROR_SAVE_FAILED);
        } else {
            ToastUtils.showLongToast(contextWeakReference.get(), "Successfully saved");
        }

        super.onPostExecute(savedId);
    }
}