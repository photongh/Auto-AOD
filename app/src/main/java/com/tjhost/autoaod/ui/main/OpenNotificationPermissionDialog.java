package com.tjhost.autoaod.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tjhost.autoaod.R;
import com.tjhost.autoaod.ui.base.BaseSupportDIalogFragment;
import com.tjhost.autoaod.utils.SettingUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OpenNotificationPermissionDialog extends BaseSupportDIalogFragment {
    public static final String DIALOG_TAG = "notification_permission_dialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.dialog_open_notification_permission_msg)
                .setPositiveButton(R.string.dialog_common_pos, (dialog, which) ->
                        SettingUtil.openNotificationPermissionSettings(getActivity()));
        return builder.create();
    }
}
