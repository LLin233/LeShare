package androidpath.ll.leshare.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;

import androidpath.ll.leshare.Adapter.SectionsPagerAdapter;
import androidpath.ll.leshare.Utils.MediaHelper;
import androidpath.ll.leshare.Utils.ParseConstants;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    //request code
    public static final int REQUEST_TAKE_PHOTO = 0;
    public static final int REQUEST_TAKE_VIDEO = 1;
    public static final int REQUEST_CHOOSE_PHOTO = 2;
    public static final int REQUEST_CHOOSE_VIDEO = 3;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; //10 MB


    //view component
    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;


    //member variable
    protected PagerAdapter mPagerAdapter;
    protected Uri mMediaUri;


    protected DialogInterface.OnClickListener mCameraDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: //take pic
                    takePhoto();
                    break;
                case 1: //take video
                    takeVideo();
                    break;
                case 2: //choose pic
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, REQUEST_CHOOSE_PHOTO);
                    break;
                case 3: //choose video
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");
                    Toast.makeText(MainActivity.this, "Video must be less than 10 MB", Toast.LENGTH_SHORT).show();
                    startActivityForResult(chooseVideoIntent, REQUEST_CHOOSE_VIDEO);
                    break;
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);


        //User has been logined
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        // https://www.parse.com/docs/android/guide#users-logging-in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, currentUser.getUsername() + ", welcome back!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, currentUser.getUsername());
        } else {
            // show the signup or login screen
            navigateToLogin();
        }

        //Tab
        //https://guides.codepath.com/android/Sliding-Tabs-with-PagerSlidingTabStrip#install-pagerslidingtabstrip
        mPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mTabs.setViewPager(mViewPager);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CHOOSE_PHOTO || requestCode == REQUEST_CHOOSE_VIDEO) {
                if (data == null) {
                    Toast.makeText(this, getString(R.string.msg_general_error), Toast.LENGTH_SHORT).show();
                } else {
                    mMediaUri = data.getData();

                }

                if (requestCode == REQUEST_CHOOSE_VIDEO) { //filter data by size < 10 MB
                    int size = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        size = inputStream.available();
                    } catch (IOException e) {
                        Toast.makeText(this, getString(R.string.msg_error_opening_file), Toast.LENGTH_SHORT).show();
                        return;
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                        }
                    }
                    if (size > FILE_SIZE_LIMIT) {
                        Toast.makeText(this, "file is too large to select.", Toast.LENGTH_SHORT);
                        return;
                    }
                }
            } else {
                //add content to Gallery if take pic or video
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
                Log.d(TAG, "added to Gallery");
            }

            //after uri is ready, start recipientsActivity
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);
            String fileType;
            if (requestCode == REQUEST_CHOOSE_PHOTO || requestCode == REQUEST_TAKE_PHOTO) {
                fileType = ParseConstants.TYPE_IMAGE;
            } else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);


        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.msg_general_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_edit_friends:
                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mCameraDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = MediaHelper.getOutputMediaFileUri(this, MediaHelper.MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            //display a error
            Toast.makeText(MainActivity.this, "Can't accress external storage in your device.", Toast.LENGTH_SHORT).show();
        } else {
            //TODO rotate photo to fix it.
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);

        }
    }

    private void takeVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mMediaUri = MediaHelper.getOutputMediaFileUri(this, MediaHelper.MEDIA_TYPE_VIDEO);
        if (mMediaUri == null) {
            //display a error
            Toast.makeText(MainActivity.this, getString(R.string.msg_error_external_storage), Toast.LENGTH_SHORT).show();
        } else {
            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            // @see http://developer.android.com/reference/android/provider/MediaStore.html#EXTRA_VIDEO_QUALITY
            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // value 0 means low quality, suitable for MMS messages, and value 1 means high quality.
            startActivityForResult(videoIntent, REQUEST_TAKE_VIDEO);
        }
    }


}
