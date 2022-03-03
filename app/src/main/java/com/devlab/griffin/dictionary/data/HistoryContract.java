package com.devlab.griffin.dictionary.data;

import android.provider.BaseColumns;

public class HistoryContract {

    public static final class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "history";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_MEANINGS_JSON = "meanings_json";
        public static final String COLUMN_ONYMS_JSON = "onyms_json";
        public static final String COLUMN_SLANGS_JSON = "slangs_json";
        public static final String COLUMN_SAVED_ON = "saved_on";
    }
}