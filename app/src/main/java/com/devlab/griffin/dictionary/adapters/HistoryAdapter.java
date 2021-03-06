package com.devlab.griffin.dictionary.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devlab.griffin.dictionary.R;
import com.devlab.griffin.dictionary.constants.Constants;
import com.devlab.griffin.dictionary.data.HistoryContract.HistoryEntry;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterViewHolder> {

    private static final String TAG = HistoryAdapter.class.getSimpleName();

    private ArrayList<Long> mHistoryWordId;
    private ArrayList<String> mHistoryWord;
    private ArrayList<String> mHistoryDates;

    private final HistoryAdapterOnClickListener mClickHandler;

    public HistoryAdapter(HistoryAdapterOnClickListener clickHandler) {
        mClickHandler = clickHandler;
        mHistoryWordId = new ArrayList<Long>(Constants.MAX_HISTORY_ENTRIES);
        mHistoryWord = new ArrayList<String>(Constants.MAX_HISTORY_ENTRIES);
        mHistoryDates = new ArrayList<String>(Constants.MAX_HISTORY_ENTRIES);
    }

    @NonNull
    @Override
    public HistoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.history_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);
        return new HistoryAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapterViewHolder holder, int position) {
        if(position >= mHistoryWord.size()) {
            Log.e(TAG, "onBindViewHolder: Invalid position for history list" + position);
            return;
        }

        long wordId = mHistoryWordId.get(position);
        String word = mHistoryWord.get(position);
        String date = mHistoryDates.get(position);

        holder.mWordTextView.setTag(wordId);
        holder.mWordTextView.setText(word);
        holder.mDateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        if(mHistoryWordId == null)
            return 0;
        return mHistoryWordId.size();
    }

    public void updateHistoryListFromCursor(Cursor cursor) {
        if(cursor == null){
            Log.e(TAG, "updateHistoryListFromCursor: Null cursor in History Adapter");
            return;
        }

        int idCursorIndex = cursor.getColumnIndex(HistoryEntry._ID);
        int wordCursorIndex = cursor.getColumnIndex(HistoryEntry.COLUMN_WORD);
        int dateCursorIndex = cursor.getColumnIndex(HistoryEntry.COLUMN_SAVED_ON);

        ArrayList<Long> tempHistoryWordId = new ArrayList<>(cursor.getCount());
        ArrayList<String> tempHistoryWord = new ArrayList<>(cursor.getCount());
        ArrayList<String> tempHistoryDate = new ArrayList<>(cursor.getCount());

        try {
            while(cursor.moveToNext()) {
                tempHistoryWordId.add(cursor.getLong(idCursorIndex));
                tempHistoryWord.add(cursor.getString(wordCursorIndex));

                Long timestamp = cursor.getLong(dateCursorIndex) * 1000L;
                Date date = new java.util.Date(timestamp);

                if (DateUtils.isToday(timestamp)) {
                    tempHistoryDate.add(Constants.DATE_TODAY);
                } else if (DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS)) {
                    // Yesterday
                    tempHistoryDate.add(Constants.DATE_YESTERDAY);
                } else {
                    tempHistoryDate.add(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
                }
            }

            mHistoryWordId = tempHistoryWordId;
            mHistoryWord = tempHistoryWord;
            mHistoryDates = tempHistoryDate;

            notifyDataSetChanged();
        }
        finally {
            cursor.close();
        }
    }

    public interface HistoryAdapterOnClickListener {
        void onClick(long wordId, String word);
    }

    public class HistoryAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mWordTextView;
        public final TextView mDateTextView;

        public HistoryAdapterViewHolder(View view) {
            super(view);
            mWordTextView = (TextView) view.findViewById(R.id.history_item_word);
            mDateTextView = (TextView) view.findViewById(R.id.history_item_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if(adapterPosition < mHistoryWordId.size()) {
                long id = mHistoryWordId.get(adapterPosition);
                String word = mHistoryWord.get(adapterPosition);
                mClickHandler.onClick(id, word);
            }
        }
    }
}
