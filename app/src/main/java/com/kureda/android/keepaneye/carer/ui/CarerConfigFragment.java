package com.kureda.android.keepaneye.carer.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kureda.android.keepaneye.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CarerConfigFragment extends Fragment {

    public CarerConfigFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carer_config, container, false);

    }
}
