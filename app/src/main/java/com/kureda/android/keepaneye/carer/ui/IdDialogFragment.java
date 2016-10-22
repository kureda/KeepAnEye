package com.kureda.android.keepaneye.carer.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.kureda.android.keepaneye.R;
import com.kureda.android.keepaneye.both.util.KeepAnEyeIdValidator;
import com.kureda.android.keepaneye.both.util.Prefs;
import com.kureda.android.keepaneye.both.util.UserDetails;

import static com.kureda.android.keepaneye.both.util.UserDetails.USER_DETAILS_FILE;

/**
 * Created by Sergei Kureda
 */
public class IdDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.id_dialog, null);
        final EditText idEnterField = (EditText) view.findViewById(R.id.dialog_id_enter_field);
        final KeepAnEyeIdValidator validator = new KeepAnEyeIdValidator(idEnterField);
        idEnterField.addTextChangedListener(validator);
        builder.setView(view)
                .setMessage(R.string.you_will_change_careds)
                .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newId = idEnterField.getText().toString();
                        if (validator.isValid(newId)) {
                            changeId(newId);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        IdDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void changeId(String newId) {
         Prefs.writeString(USER_DETAILS_FILE, UserDetails.KEEP_AN_EYE_ID, newId);
    }
}
