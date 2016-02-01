package ca.uqac.florentinth.speakerauthentication.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import ca.uqac.florentinth.speakerauthentication.R;

/**
 * Created by FlorentinTh on 1/13/2016.
 */
public abstract class AndroidUtils {

    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1) {
            return false;
        }

        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showAlertDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setNeutralButton(context.getString(R.string.ok_label), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showAlertDialogFinish(final Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);

        builder.setNeutralButton(context.getString(R.string.ok_label), new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
