package com.kureda.android.keepaneye.both.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.ui.App;

/**
 * Created by Sergei Kureda
 */

public class KeepAnEyeIdValidator implements TextWatcher {
    public static final long MINIMAL_ID = 1000000L;
    public static final int MINIMAL_ID_LENGTH = 6;
    private TextView mTextView;
    private static String mWhongChars = App.getContext().getString(R.string.only_numbers_allowed);
    private static String mTooShort = App.getContext().getString(R.string.should_be_longer);
    private static String mTooLong = App.getContext().getString(R.string.should_be_shorter);

    public KeepAnEyeIdValidator(TextView textView) {
        mTextView = textView;
    }

    @Override
    public void afterTextChanged(Editable s) {
        //don't need it
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int before, int count) {
        //don't need it
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mTextView.setError(getErrorMessage(s));
    }

    public static boolean isValid(String id){
        return getErrorMessage(id)==null;
    }

    private static String getErrorMessage(CharSequence s) {
        if (s==null || s.length()==0) return  mTooShort;
        if (!TextUtils.isDigitsOnly(s))
            return mWhongChars;
        if (s.length() < MINIMAL_ID_LENGTH)
            return mTooShort;
        try {
            long number = Long.parseLong(s.toString());
            if (number > Integer.MAX_VALUE)
                return mTooLong;
            if (number < 0L)
                return mWhongChars;
            if (number < MINIMAL_ID)
                return mTooShort;
            return null; //all OK, no error message
        } catch (NumberFormatException ex) {
            return mTooLong;
        }
    }
}
