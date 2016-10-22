package com.kureda.android.keepaneye.carer.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.carer.util.Cared;

/**
 * Created by Sergei Kureda
 */

public class CaredCursorAdapter extends CursorAdapter {
    public CaredCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_carer_main, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Cared cared = new Cared(context);
        cared.readFromCursor(cursor);
        cared.displayToView(view);
    }
}
