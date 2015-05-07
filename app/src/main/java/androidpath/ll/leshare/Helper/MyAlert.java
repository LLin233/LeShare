package androidpath.ll.leshare.Helper;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Le on 2015/5/7.
 */
public class MyAlert {
    public static void showSignUpAlertDialog(Context context, String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}
