package com.kureda.android.keepaneye.cared.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kureda.android.keepaneye.R;

/**
 * Created by Sergei Kureda
 */
public class CaredConfigFragment extends Fragment {

    public CaredConfigFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cared_config, container, false);
    }
}
