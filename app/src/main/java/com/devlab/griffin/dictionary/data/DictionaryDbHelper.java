package com.devlab.griffin.dictionary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.devlab.griffin.dictionary.data.SavedContract.SavedEntry;
import com.devlab.griffin.dictionary.data.HistoryContract.HistoryEntry;

public class DictionaryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dictionary.db";

    private static final int DATABASE_VERSION = 1;

    public DictionaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase sqLiteDatabase) {

        // Create a table for Saved Words
        final String QUERY_CREATE_SAVED_TABLE = "CREATE TABLE " + SavedEntry.TABLE_NAME + " (" +
                SavedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SavedEntry.COLUMN_WORD + " TEXT NOT NULL, " +
                SavedEntry.COLUMN_MEANINGS_JSON + " TEXT, " +
                SavedEntry.COLUMN_ONYMS_JSON + " TEXT, " +
                SavedEntry.COLUMN_SLANGS_JSON + " TEXT, " +
                SavedEntry.COLUMN_SAVED_ON + " TIMESTAMP NOT NULL" +
                ");";

        // Create a table for History
        final String QUERY_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryEntry.COLUMN_WORD + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_MEANINGS_JSON + " TEXT, " +
                HistoryEntry.COLUMN_ONYMS_JSON + " TEXT, " +
                HistoryEntry.COLUMN_SLANGS_JSON + " TEXT, " +
                HistoryEntry.COLUMN_SAVED_ON + " TIMESTAMP NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(QUERY_CREATE_SAVED_TABLE);
        sqLiteDatabase.execSQL(QUERY_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Leave it empty for now
    }
}
