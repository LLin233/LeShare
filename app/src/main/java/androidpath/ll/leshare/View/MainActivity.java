package androidpath.ll.leshare.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import androidpath.ll.leshare.Adapter.SectionsPagerAdapter;
import androidpath.ll.leshare.Helper.MediaHelper;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    //request code
    public static final int REQUEST_TAKE_PHOTO = 0;
    public static final int REQUEST_TAKE_VIDEO = 1;
    public static final int REQUEST_CHOOSE_PHOTO = 2;
    public static final int REQUEST_CHOOSE_VIDEO = 3;


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
                    break;
                case 2: //choose pic

                    break;
                case 3: //choose video
                    break;
            }
        }
    };

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mMediaUri = MediaHelper.getOutputMediaFileUri(this, MediaHelper.MEDIA_TYPE_IMAGE);
        if (mMediaUri == null) {
            //display a error
            Toast.makeText(MainActivity.this, "Can't accress external storage in your device.", Toast.LENGTH_SHORT).show();
        } else {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }
    }


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

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
