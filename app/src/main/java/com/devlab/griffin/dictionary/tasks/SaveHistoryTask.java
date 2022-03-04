package com.devlab.griffin.dictionary.tasks;

import android.os.AsyncTask;

import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;

public class SaveHistoryTask extends AsyncTask<String, Void, Long> {
    
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
}
