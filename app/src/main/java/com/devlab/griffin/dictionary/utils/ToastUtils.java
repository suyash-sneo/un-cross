package com.devlab.griffin.dictionary.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    public static void showLongToast(Context context, String message) {
        if(mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
    }
}
