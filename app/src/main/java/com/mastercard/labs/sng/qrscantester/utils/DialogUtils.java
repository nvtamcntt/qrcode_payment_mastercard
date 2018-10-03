package com.mastercard.labs.sng.qrscantester.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mastercard.labs.sng.qrscantester.R;

public class DialogUtils {
    public static void showDialog(Context context, @StringRes int title, @StringRes int message) {
        customAlertDialogBuilder(context, message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private static AlertDialog.Builder customAlertDialogBuilder(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        return new AlertDialog.Builder(context);
    }

    public static AlertDialog.Builder customAlertDialogBuilder(Context context, @StringRes int message) {
        TextView messageTextView = new TextView(context);
        messageTextView.setText(message);
        messageTextView.setGravity(Gravity.CENTER_HORIZONTAL);

        int padding = context.getResources().getDimensionPixelSize(R.dimen.size_10);
        float textSize = context.getResources().getDimension(R.dimen.size_x);
        messageTextView.setPadding(padding, padding, padding, padding);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        messageTextView.setTextColor(ContextCompat.getColor(context, R.color.colorTextMainColor));

        return customAlertDialogBuilder(context).setView(messageTextView);
    }
}
