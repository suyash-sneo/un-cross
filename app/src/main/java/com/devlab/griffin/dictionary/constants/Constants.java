package com.devlab.griffin.dictionary.constants;

public class Constants {

    public static final String FREE_DICTIONARY_ENGLISH_BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en";
    public static final String BIG_HUGE_THESAURUS_BASE_URL = "https://words.bighugelabs.com/api/2";
    public static final String URBAN_DICTIONARY_BASE_URL = "https://mashape-community-urban-dictionary.p.rapidapi.com/define";

    public static final String URBAN_DICTIONARY_QUERY_PARAM_TERM = "term";

    public static final String X_RAPID_API_HOST_HEADER_KEY = "x-rapidapi-host";
    public static final String X_RAPID_API_KEY_HEADER_KEY = "x-rapidapi-key";

    public static final int MEANING_FRAGMENT_POSITION = 0;
    public static final int ONYMS_FRAGMENT_POSITION = 1;
    public static final int SLANGS_FRAGMENT_POSITION = 2;

    public static final String MEANING_FRAGMENT_STATE = "meanings";
    public static final String ONYMS_FRAGMENT_STATE = "onyms";
    public static final String SLANGS_FRAGMENT_STATE = "slangs";

    public static final String BUTTON_STATE_CLEAR = "clear";
    public static final String BUTTON_STATE_DONE = "done";
    public static final String BUTTON_STATE_SAVE = "save";
    public static final String BUTTON_STATE_DELETE = "delete";

    public static final int MAX_HISTORY_ENTRIES = 50;
    public static final int MAX_SAVED_ENTRIES = 200;

    public static final String EVENT_WORD_FETCHED = "word_fetched";
    public static final String EVENT_WORD_SAVED = "word_saved";
    public static final String EVENT_SEARCH_FRAGMENT_LOADED = "search_fragment_loaded";
    public static final String EVENT_VOCAB_FRAGMENT_LOADED = "vocab_fragment_loaded";

    public static final String INTENT_KEY_WORD_ID = "wordId";
    public static final String INTENT_KEY_WORD = "word";
    public static final String INTENT_KEY_SCREEN_TYPE = "screenType";
    public static final String SCREEN_TYPE_SAVED_WORD = "savedWord";
    public static final String SCREEN_TYPE_HISTORY_WORD = "historyWord";

    public static final String DATE_TODAY = "Today";
    public static final String DATE_YESTERDAY = "Yesterday";

    public static final String ERROR_SAVE_FAILED = "Failed to Save the word";
}
