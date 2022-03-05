package com.devlab.griffin.dictionary.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.devlab.griffin.dictionary.data.DictionaryQueryAgent;
import com.devlab.griffin.dictionary.utils.ToastUtils;

import java.lang.ref.WeakReference;

public class DeleteWordTask extends AsyncTask<String, Void, Integer> {

    private static final String TAG = DeleteWordTask.class.getSimpleName();

    private final WeakReference<FragmentActivity> mParentActivityWeakReference;
    private final WeakReference<Context> mContextWeakReference;

    public DeleteWordTask(FragmentActivity parentActivity, Context context) {
        mParentActivityWeakReference = new WeakReference<>(parentActivity);
        mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected Integer doInBackground(String... params) {
        if(params.length < 1) {
            Log.e(TAG, "doInBackground: too few params");
            return -1;
        }

        String word = params[0].toLowerCase();

        return DictionaryQueryAgent.DeleteSavedByWord(word);
    }

    @Override
    protected void onPostExecute(Integer rowsDeleted) {
        if(mParentActivityWeakReference.get() != null && mContextWeakReference.get() != null) {
            if(rowsDeleted > 0) {
                ToastUtils.showLongToast(mContextWeakReference.get(), "Successfully deleted");
                mParentActivityWeakReference.get().finish();
            }
            else {
                ToastUtils.showLongToast(mContextWeakReference.get(), "Failed to delete");
            }
        }
        super.onPostExecute(rowsDeleted);
    }
}
