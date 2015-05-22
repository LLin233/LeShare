package androidpath.ll.leshare.Utils;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Le on 2015/5/7.
 */
public class MyAlert {

    /**
     * @param context
     * @param title
     * @param msg
     * shows AlertDialog instance which creates by builder.
     */
    public static void showAlertDialog(Context context, String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }
}
