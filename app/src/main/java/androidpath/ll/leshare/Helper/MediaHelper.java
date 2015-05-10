package androidpath.ll.leshare.Helper;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidpath.ll.leshare.R;
import androidpath.ll.leshare.View.MainActivity;

/**
 * Created by Le on 2015/5/9.
 */
public class MediaHelper {
    public static final String TAG = MediaHelper.class.getSimpleName();
    // media type code
    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    /**
     * @param context        to access res/strings.xml
     * @param mediaTypeImage
     * @return Uri
     * @see <a href="http://developer.android.com/guide/topics/media/camera.html">Camera guide</a>
     */

    public static Uri getOutputMediaFileUri(Context context, int mediaTypeImage) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (isExternalStorageAvailable()) {
            //1. get the external storage directory
            String appname = context.getString(R.string.app_name);
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), appname);
            //2. Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(appname, "failed to create directory");
                    return null;
                }
            }
            //3. create a file name
            //4. create file
            File mediaFile;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            if (mediaTypeImage == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
            } else if (mediaTypeImage == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_" + timeStamp + ".mp4");
            } else {
                return null;
            }
            Log.d(TAG, "File : " + Uri.fromFile(mediaFile));
            //5. return file's uri
            return Uri.fromFile(mediaFile);
        } else {
            return null;
        }
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
