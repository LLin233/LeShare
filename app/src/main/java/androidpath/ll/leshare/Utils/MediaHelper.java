package androidpath.ll.leshare.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidpath.ll.leshare.R;

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

    public static int getExifOrientation(Uri uri, Context context) {
        Cursor cursor = null;
        try {
            String[] CONTENT_ORIENTATION = {MediaStore.Images.Media.ORIENTATION};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&& DocumentsContract.isDocumentUri(context, uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                id = id.split(":")[1];
                cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, CONTENT_ORIENTATION, MediaStore.Images.Media._ID + " = ?", new String[]{id}, null);
            } else {

                cursor = context.getContentResolver().query(uri, CONTENT_ORIENTATION, null, null, null);
            }
            if (cursor == null || !cursor.moveToFirst()) {
                return 0;
            }
            int orientation = cursor.getInt(cursor.getColumnIndex(CONTENT_ORIENTATION[0]));
            return orientation;
        } catch (RuntimeException ignored) {
            // If the orientation column doesn't exist, assume no rotation.
        }
        return -1;
    }

    public static String getImagePath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = context.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

}
