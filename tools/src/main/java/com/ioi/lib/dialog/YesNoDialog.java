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
public class YesNoDialog extends DialogFragment {

    private OnYesNoListener onYesNoListener;
    private int title = 0;
    private int message = 0;
    private int yes = 0;
    private int no = 0;

    private String strMessage = null;

    public static YesNoDialog newInstance(@Nullable OnYesNoListener onYesNoListener,
                                          @NonNull int title /* string resource id */,
                                          @NonNull int message /* string resource id */,
                                          @NonNull int yes /* string resource id */,
                                          @NonNull int no /* string resource id */) {
        YesNoDialog dialog = new YesNoDialog();
        dialog.onYesNoListener = onYesNoListener;
        dialog.title = title;
        dialog.message = message;
        dialog.yes = yes;
        dialog.no = no;
        return dialog;
    }

    public static YesNoDialog newInstance(@Nullable OnYesNoListener onYesNoListener,
                                          @NonNull int title /* string resource id */,
                                          @NonNull String message,
                                          @NonNull int yes /* string resource id */,
                                          @NonNull int no /* string resource id */) {
        YesNoDialog dialog = new YesNoDialog();
        dialog.onYesNoListener = onYesNoListener;
        dialog.title = title;
        dialog.strMessage = message;
        dialog.yes = yes;
        dialog.no = no;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setTitle(title)
                .setMessage(strMessage != null ? strMessage : getActivity().getResources().getString(message))
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onYesNoListener != null)
                            onYesNoListener.onYes();
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onYesNoListener != null)
                            onYesNoListener.onNo();
                    }
                })
                .create();
    }

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        try {
            if (isAdded()) {
                Log.d("YesNoDialog", "YesNoDialog is already added");
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
    public interface OnYesNoListener {
        void onYes();
        void onNo();
    }
}
