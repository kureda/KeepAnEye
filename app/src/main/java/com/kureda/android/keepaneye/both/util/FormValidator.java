package com.kureda.android.keepaneye.both.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.kureda.android.keepaneye.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergei Kureda
 * Interface for TextWatcher to report to the FormValidator
 */
interface Form {
    void checkAll(boolean checked);
}

/**
 * Created by Serg on 9/26/2016.
 * Validates set of EditText fields, each by its own rules
 * Invalid fields get error messages.
 * If all fields are valid, enables the submit button
 */

public class FormValidator implements Form {
    private static final String M_SUBMIT_BUTTON_IS_COMPULSORY = Res.getString(R.string.must_sb_btn);
    public static int RANGED_NUMBER = 2; //length may be +-2
    public static int SHORT_NAME = 3;
    public static int PHONE = 4;
    private static int FIXED_LENGTH_NUMBER = 1;
    private View mSubmitButton;
    private List<Watcher> mWatchers;

    public FormValidator(View submitButton) {
        mSubmitButton = submitButton;
        mWatchers = new ArrayList<>();
        submitButton.setVisibility(View.GONE);
        submitButton.setEnabled(false);
    }

    public void register(EditText field, int type, int length) {
        if (mSubmitButton == null)
            throw new NullPointerException(M_SUBMIT_BUTTON_IS_COMPULSORY);
        Watcher watcher = new Watcher(field, type, length, this);
        mWatchers.add(watcher);
    }

    /**
     * Report validity of field to the form
     */
    public void checkAll(boolean checked) {
        boolean allAreValid = true;
        for (Watcher watcher : mWatchers) {
            if (!checked)
                watcher.check();
            allAreValid = allAreValid && watcher.mValid;
        }
        mSubmitButton.setEnabled(allAreValid);
        mSubmitButton.setVisibility(allAreValid ? View.VISIBLE : View.GONE);
    }

    private class Watcher implements TextWatcher {
        static final long MINIMAL_VALID_LONG = 1000000L;
        boolean mValid = false;
        private EditText mField;
        private int mType;
        private int mLength;
        private Form mForm;

        private Watcher(EditText field, int type, int length, Form form) {
            mField = field;
            mType = type;
            mLength = length;
            mForm = form;
            mField.addTextChangedListener(this);
        }

        private void check() {
            String string = mField.getText().toString();
            if (mType == FIXED_LENGTH_NUMBER) {
                int missing = mLength - string.length();
                mValid = missing == 0;
            } else if (mType == RANGED_NUMBER) {
                int missing = mLength - string.length();
                mValid = missing > (-2) && missing < (2);
                try {
                    long number = Long.parseLong(string);
                    mValid = (number > MINIMAL_VALID_LONG && number < Long.MAX_VALUE);
                } catch (NumberFormatException ex) {
                    mValid = false;
                }
            } else if (mType == SHORT_NAME) {
                mValid = (string.length() < mLength)
                        && (string.length() > 1
                );
            } else if (mType == PHONE) {
                mValid = (string.length() >= mLength);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String string = s.toString();
            mValid = false;
            String errorMessage = "";

            if (mType == FIXED_LENGTH_NUMBER) {
                int missing = mLength - string.length();
                int absMissing = Math.abs(missing);
                mValid = missing == 0;
                int errorMessageId = missing > 0 ? R.plurals.more : R.plurals.less;
                errorMessage = Res.getQuantityString(errorMessageId, absMissing);
            } else if (mType == RANGED_NUMBER) {
                int missing = mLength - string.length();
                int absMissing = Math.abs(missing);
                mValid = missing > (-2) && missing < (2);
                int errorMessageId = missing > 0 ? R.string.too_short : R.string.too_long;
                if (mValid) {
                    try {
                        long number = Long.parseLong(string);
                        mValid = (number > MINIMAL_VALID_LONG && number < Long.MAX_VALUE);
                        errorMessageId = R.string.too_long;
                    } catch (NumberFormatException ex) {
                        mValid = false;
                        errorMessageId = R.string.too_long;
                    }
                }
                errorMessage = Res.getString(errorMessageId);
            } else if (mType == SHORT_NAME) {
                mValid = (string.length() < mLength)
                        && (string.length() > 2
                        && !string.startsWith(" ")
                );
                int errorMessageId = string.length() < mLength ? R.string.more : R.string.less;
                errorMessage = Res.getString(errorMessageId, mLength);
            } else if (mType == PHONE) {
                mValid = (string.length() >= mLength);
                errorMessage = Res.getString(R.string.must_be_phone, mLength);
            }
            mField.setError(mValid ? null : errorMessage);
            mForm.checkAll(true);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    }

}
