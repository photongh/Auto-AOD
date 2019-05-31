package com.tjhost.autoaod.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.tjhost.autoaod.R;
import com.tjhost.autoaod.ui.base.BaseSupportDIalogFragment;

public class TipsDialog extends BaseSupportDIalogFragment {
    public static final String DIALOG_TAG = "TipsDialog";
    private static final String KEY_TITLE = "title";
    private static final String KEY_MSG = "msg";

    public static TipsDialog getInstance(int titleRes, int msgRes) {
        TipsDialog dialog = new TipsDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE, titleRes);
        bundle.putInt(KEY_MSG, msgRes);
        dialog.setArguments(bundle);
        return dialog;
    }

    public static void show(@NonNull FragmentActivity activity, int titleRes, int msgRes) {
        getInstance(titleRes, msgRes).show(activity.getSupportFragmentManager(), DIALOG_TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int titleRes = getArguments().getInt(KEY_TITLE, 0);
        int msgRes = getArguments().getInt(KEY_MSG, 0);
        if (titleRes != 0)
            builder.setTitle(titleRes);
        if (msgRes != 0)
            builder.setMessage(msgRes);
        builder.setPositiveButton(R.string.dialog_common_pos, null);
        return builder.create();
    }
}
