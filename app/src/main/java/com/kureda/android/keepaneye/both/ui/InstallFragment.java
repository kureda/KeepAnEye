package com.kureda.android.keepaneye.both.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kureda.android.keepaneye.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class InstallFragment extends Fragment {

    public InstallFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_install, container, false);
    }
}
