package com.ioi.lib.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by henhuang on 10/20/16.
 */
public class OkDialog extends DialogFragment {

    protected OnOkListener onOkListener;
    private int title = 0;
    private int message= 0;
    private int ok = 0;

    public static OkDialog newInstance(@Nullable OnOkListener onOkListener,
                                          @NonNull int title /* string resource id */,
                                          @NonNull int message /* string resource id */,
                                          @NonNull int ok) /* string resource id */ {
        OkDialog dialog = new OkDialog();
        dialog.onOkListener = onOkListener;
        dialog.title = title;
        dialog.message = message;
        dialog.ok = ok;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(title)
                .setMessage(getActivity().getResources().getString(message))
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onOkListener != null)
                            onOkListener.onOk();
                    }
                })
                .create();
    }

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        try {
            if (isAdded()) {
                Log.d("OkDialog", "OkDialog is already added");
                return;
            }

            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(this, tag);

            // avoid IllegalStateException: Can not perform this action after onSaveInstanceState
            ft.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    public interface OnOkListener {
        void onOk();
    }
}
