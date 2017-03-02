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
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ioi.lib.BooleanPreference;
import com.ioi.tool.R;

/**
 * Created by henhuang on 10/21/16.
 */

/**
 * @title int: string resource id
 * @message String: message string
 * @preferenceMessage int: string resource id
 * @ok int: string resource id
 * @no int: string resource id
 * ----------------------------------
 * | Title // @title
 * |---------------------------------
 * | Xxxxx xxxxx xxxxxxx // @message
 * | xxxxx xxxxx xxxxxxx
 * |
 * | 口 Do not show again // @preferenceMessage
 * |----------------------------------
 * | CANCEL // @no | OK // @ok
 * -----------------------------------
 */
public class PreferenceCheckboxDialog extends DialogFragment {

    protected OnYesNoListener listener;
    private int title = 0;
    private SpannableString message;
    private int yes = 0;
    private int no = 0;
    private int preferenceMessage = 0;
    private BooleanPreference preference;

    public static PreferenceCheckboxDialog newInstance(@Nullable OnYesNoListener listener,
                                                       @NonNull int title /* string resource id */,
                                                       @NonNull SpannableString message,
                                                       @NonNull int yes, /* string resource id */
                                                       @NonNull int no, /* string resource id */
                                                       @NonNull int preferenceMessage /* string resource id */,
                                                       @NonNull BooleanPreference preference) {
        PreferenceCheckboxDialog dialog = new PreferenceCheckboxDialog();
        dialog.listener = listener;
        dialog.title = title;
        dialog.message = message;
        dialog.yes = yes;
        dialog.no = no;
        dialog.preferenceMessage = preferenceMessage;
        dialog.preference = preference;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_preference_checkbox, null);
        ((TextView) view.findViewById(R.id.message)).setText(message);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.preference);
        ((TextView) view.findViewById(R.id.preferenceMessage)).setText(getResources().getText(preferenceMessage));

        return builder
                .setTitle(title)
                .setView(view)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preference.edit(checkBox.isChecked());

                        if (listener != null)
                            listener.onYes();
                    }
                })
                .setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onNo();
                    }
                })
                .create();
    }

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        try {
            if (isAdded()) {
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

    public boolean getPreferenceValue() {
        return preference.getValue();
    }

    public interface OnYesNoListener {
        void onYes();
        void onNo();
    }
}
