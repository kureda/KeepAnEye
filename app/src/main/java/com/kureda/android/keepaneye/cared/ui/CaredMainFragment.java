package com.kureda.android.keepaneye.cared.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kureda.android.keepaneye.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaredMainFragment extends Fragment {

    public CaredMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cared_main, container, false);
    }
}
