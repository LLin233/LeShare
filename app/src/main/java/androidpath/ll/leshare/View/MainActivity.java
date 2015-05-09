package androidpath.ll.leshare.View;

import android.content.Intent;
import android.os.Bundle;
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
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    protected PagerAdapter mPagerAdapter;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;

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
        }
        return super.onOptionsItemSelected(item);
    }
}
