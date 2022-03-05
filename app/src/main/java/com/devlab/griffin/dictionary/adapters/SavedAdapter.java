package com.devlab.griffin.dictionary.adapters;

import android.content.Context;
import android.database.Cursor;
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

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedAdapterViewHolder> {

    private static final String TAG = SavedAdapter.class.getSimpleName();

    private ArrayList<Long> mSavedWordId;
    private ArrayList<String> mSavedWord;

    private final SavedAdapterOnClickListener mClickHandler;

    public SavedAdapter (SavedAdapterOnClickListener clickHandler) {
        mClickHandler = clickHandler;
        mSavedWordId = new ArrayList<Long>(Constants.MAX_SAVED_ENTRIES);
        mSavedWord = new ArrayList<String>(Constants.MAX_SAVED_ENTRIES);
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

        System.out.println("ONBIND VIEW HOLDER: " + position);
        long wordId = mSavedWordId.get(position);
        String word = mSavedWord.get(position);

        holder.mWordTextView.setTag(wordId);
        holder.mWordTextView.setText(word);
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

        ArrayList<Long> tempSavedWordId = new ArrayList<>(cursor.getCount());
        ArrayList<String> tempSavedWord = new ArrayList<>(cursor.getCount());

        try {
            while(cursor.moveToNext()) {
                tempSavedWordId.add(cursor.getLong(idCursorIndex));
                tempSavedWord.add(cursor.getString(wordCursorIndex));
            }

            mSavedWordId = tempSavedWordId;
            mSavedWord = tempSavedWord;

            System.out.println("MSAVEDWORDID LENGTH: " + mSavedWordId.size());

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

        public SavedAdapterViewHolder(View view) {
            super(view);
            mWordTextView = (TextView) view.findViewById(R.id.saved_item_word);
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
