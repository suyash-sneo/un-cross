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
import com.devlab.griffin.dictionary.data.SavedContract.SavedEntry;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedAdapterViewHolder> {

    private static final String TAG = SavedAdapter.class.getSimpleName();

    private ArrayList<Long> mSavedWordId;
    private ArrayList<String> mSavedWord;
    private ArrayList<String> mSavedDate;

    private final SavedAdapterOnClickListener mClickHandler;

    public SavedAdapter (SavedAdapterOnClickListener clickHandler) {
        mClickHandler = clickHandler;
        mSavedWordId = new ArrayList<Long>(Constants.MAX_SAVED_ENTRIES);
        mSavedWord = new ArrayList<String>(Constants.MAX_SAVED_ENTRIES);
        mSavedDate = new ArrayList<String>(Constants.MAX_SAVED_ENTRIES);
    }

    @NonNull
    @Override
    public SavedAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.saved_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(layoutIdForListItem, parent, false);
        return new SavedAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedAdapterViewHolder holder, int position) {
        if(position >= mSavedWordId.size()) {
            Log.e(TAG, "onBindViewHolder: Invalid position for saved list" + position);
            return;
        }

        long wordId = mSavedWordId.get(position);
        String word = mSavedWord.get(position);
        String date = mSavedDate.get(position);

        holder.mWordTextView.setTag(wordId);
        holder.mWordTextView.setText(word);
        holder.mDateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        if(mSavedWordId == null)
            return 0;
        return mSavedWordId.size();
    }

    public void updateSavedListFromCursor(Cursor cursor) {
        if(cursor == null){
            Log.e(TAG, "updateSavedListFromCursor: Null cursor in Saved Adapter");
            return;
        }

        int idCursorIndex = cursor.getColumnIndex(SavedEntry._ID);
        int wordCursorIndex = cursor.getColumnIndex(SavedEntry.COLUMN_WORD);
        int dateCursorIndex = cursor.getColumnIndex(SavedEntry.COLUMN_SAVED_ON);

        ArrayList<Long> tempSavedWordId = new ArrayList<>(cursor.getCount());
        ArrayList<String> tempSavedWord = new ArrayList<>(cursor.getCount());
        ArrayList<String> tempSavedDates = new ArrayList<>(cursor.getCount());

        try {
            while(cursor.moveToNext()) {
                tempSavedWordId.add(cursor.getLong(idCursorIndex));
                tempSavedWord.add(cursor.getString(wordCursorIndex));

                Long timestamp = cursor.getLong(dateCursorIndex) * 1000L;
                Date date = new java.util.Date(timestamp);

                if (DateUtils.isToday(timestamp)) {
                    tempSavedDates.add(Constants.DATE_TODAY);
                } else if (DateUtils.isToday(date.getTime() + DateUtils.DAY_IN_MILLIS)) {
                    // Yesterday
                    tempSavedDates.add(Constants.DATE_YESTERDAY);
                } else {
                    tempSavedDates.add(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date));
                }
            }

            mSavedWordId = tempSavedWordId;
            mSavedWord = tempSavedWord;
            mSavedDate = tempSavedDates;

            notifyDataSetChanged();
        }
        finally {
            cursor.close();
        }
    }

    public interface SavedAdapterOnClickListener {
        void onClick(long wordId, String word);
    }

    public class SavedAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mWordTextView;
        public final TextView mDateTextView;

        public SavedAdapterViewHolder(View view) {
            super(view);
            mWordTextView = (TextView) view.findViewById(R.id.saved_item_word);
            mDateTextView = (TextView) view.findViewById(R.id.saved_item_date);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if(adapterPosition < mSavedWordId.size()) {
                long id = mSavedWordId.get(adapterPosition);
                String word = mSavedWord.get(adapterPosition);
                mClickHandler.onClick(id, word);
            }
        }
    }
}
