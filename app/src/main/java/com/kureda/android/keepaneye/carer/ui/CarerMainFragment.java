package com.kureda.android.keepaneye.carer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kureda.android.keepaneye.R;

/**
 * Created by Sergei Kureda
 */
public class CarerMainFragment extends Fragment {

    public CarerMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carer_main, container, false);
    }
}
