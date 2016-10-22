package com.kureda.android.keepaneye.both.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.kureda.android.keepaneye.R;

/**
 * Created by Sergei Kureda
 */

public class PhoneValidator implements TextWatcher {
    private TextView mTextView;
    private String mErrorMessage;

    public PhoneValidator(TextView textView) {
        mTextView = textView;
        mErrorMessage = textView.getContext().getString(R.string.not_phone_number);
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
        boolean valid = Phone.isPhoneNumber(s.toString());
        mTextView.setError(valid ? null : mErrorMessage);
    }
}
