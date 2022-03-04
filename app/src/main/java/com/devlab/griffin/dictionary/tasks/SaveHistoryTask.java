package com.devlab.griffin.dictionary.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.interfaces.FragmentParentEventListener;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;

public class SaveHistoryTask extends AsyncTask<String, Void, Long> {

    private static final String TAG = SaveHistoryTask.class.getSimpleName();

    private FragmentParentEventListener parentEventListener;

    public void setParentEventListener(FragmentParentEventListener parentEventListener) {
        this.parentEventListener = parentEventListener;
    }

    private void updateHistoryView() {
        this.parentEventListener.passEventData(Constants.EVENT_WORD_FETCHED, "");
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

        return DictionaryQueryAgent.SaveHistoryEntry(word, meanings, onyms, slangs);
    }

    @Override
    protected void onPostExecute(Long savedId) {
        if(savedId < 0) {
            Log.e(TAG, "onPostExecute: Failed to save the word");
        } else {
            updateHistoryView();
        }

        super.onPostExecute(savedId);
    }
}
