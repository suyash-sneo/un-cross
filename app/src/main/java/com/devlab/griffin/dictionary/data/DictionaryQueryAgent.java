package com.devlab.griffin.dictionary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.SavedContract.SavedEntry;
import com.devlab.griffin.dictionary.data.HistoryContract.HistoryEntry;
import com.devlab.griffin.dictionary.utils.JsonParsingUtils;

public class DictionaryQueryAgent {

    private static final String TAG =DictionaryQueryAgent.class.getSimpleName();

    private static SQLiteDatabase writeDb;
    private static SQLiteDatabase readDb;

    private static long getCurrentTimeStamp() {
        return System.currentTimeMillis()/1000L;
    }

    public static void initializeDb(Context context) {
        DictionaryDbHelper dbHelper = new DictionaryDbHelper(context);
        writeDb = dbHelper.getWritableDatabase();
        readDb = dbHelper.getReadableDatabase();
    }

    public static Cursor GetHistoryEntryById(long id) {
        return readDb.query(
                HistoryEntry.TABLE_NAME,
                null,
                HistoryEntry._ID + "=?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public static Cursor GetSavedEntryById(long id) {
        return readDb.query(
                SavedEntry.TABLE_NAME,
                null,
                SavedEntry._ID + "=?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null
        );
    }

    public static Cursor GetSavedEntryByWord(String word) {
        return readDb.query(
                SavedEntry.TABLE_NAME,
                null,
                SavedEntry.COLUMN_WORD + "=?",
                new String[] {word},
                null,
                null,
                null
        );
    }

    public static Cursor GetHistoryEntryByWord(String word) {
        return readDb.query(
                HistoryEntry.TABLE_NAME,
                null,
                HistoryEntry.COLUMN_WORD + "=?",
                new String[] {word},
                null,
                null,
                null
        );
    }

    public static long GetHistoryCount() {
        return DatabaseUtils.queryNumEntries(readDb, HistoryEntry.TABLE_NAME);
    }

    public static void DeleteHistoryEntriesOlderThan(long maxHistoryEntries) {

        String deleteQuery = "DELETE FROM " + HistoryEntry.TABLE_NAME + " WHERE " + HistoryEntry._ID + " NOT IN " +
                "(" +
                    "SELECT " + HistoryEntry._ID + " FROM " + HistoryEntry.TABLE_NAME + " ORDER BY " + HistoryEntry.COLUMN_SAVED_ON + " DESC LIMIT " + maxHistoryEntries +
                ")";

        writeDb.execSQL(deleteQuery);
    }

    public static long SaveWordEntry(String word, String meanings, String onyms, String slangs) {
        if(JsonParsingUtils.IsNullOrEmpty(word) ||
                (
                        JsonParsingUtils.IsNullOrEmpty(meanings) &&
                        JsonParsingUtils.IsNullOrEmpty(onyms) &&
                        JsonParsingUtils.IsNullOrEmpty(slangs)
                )
        ) {
            Log.e(TAG, "SaveWordEntry: Cannot save word. Empty entries");
            return -1;
        }


        ContentValues cv = new ContentValues();
        cv.put(SavedEntry.COLUMN_MEANINGS_JSON, meanings);
        cv.put(SavedEntry.COLUMN_ONYMS_JSON, onyms);
        cv.put(SavedEntry.COLUMN_SLANGS_JSON, slangs);
        cv.put(SavedEntry.COLUMN_SAVED_ON, getCurrentTimeStamp());

        Cursor wordCursor = GetSavedEntryByWord(word);
        int wordCount = wordCursor.getCount();
        wordCursor.close();

        if(wordCount > 0) {
            // Update case
            return writeDb.update(
                    SavedEntry.TABLE_NAME,
                    cv,
                    SavedEntry.COLUMN_WORD + "=?",
                    new String[] {word}
            );
        }

        cv.put(SavedEntry.COLUMN_WORD, word);
        // Create case
        return writeDb.insert(
                SavedEntry.TABLE_NAME,
                null,
                cv);
    }

    public static long SaveHistoryEntry(String word, String meanings, String onyms, String slangs) {
        if(JsonParsingUtils.IsNullOrEmpty(word) ||
                (
                        JsonParsingUtils.IsNullOrEmpty(meanings) &&
                                JsonParsingUtils.IsNullOrEmpty(onyms) &&
                                JsonParsingUtils.IsNullOrEmpty(slangs)
                )
        ) {
            Log.e(TAG, "SaveHistoryEntry: Cannot save word. Empty entries");
            return -1;
        }

        if(GetHistoryCount() >= Constants.MAX_HISTORY_ENTRIES) {
            DeleteHistoryEntriesOlderThan(Constants.MAX_HISTORY_ENTRIES);
        }

        ContentValues cv = new ContentValues();
        cv.put(HistoryEntry.COLUMN_MEANINGS_JSON, meanings);
        cv.put(HistoryEntry.COLUMN_ONYMS_JSON, onyms);
        cv.put(HistoryEntry.COLUMN_SLANGS_JSON, slangs);
        cv.put(HistoryEntry.COLUMN_SAVED_ON, getCurrentTimeStamp());

        Cursor wordCursor = GetHistoryEntryByWord(word);
        int wordCount = wordCursor.getCount();
        wordCursor.close();

        if(wordCount > 0) {
            // Update case
            return writeDb.update(
                    SavedEntry.TABLE_NAME,
                    cv,
                    SavedEntry.COLUMN_WORD + "=?",
                    new String[] {word}
            );
        }

        cv.put(SavedEntry.COLUMN_WORD, word);
        // Create case

        return writeDb.insert(
                HistoryEntry.TABLE_NAME,
                null,
                cv);
    }

    public static void closeDb() {
        writeDb.close();
        readDb.close();
    }
}
