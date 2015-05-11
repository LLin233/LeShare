package androidpath.ll.leshare.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import androidpath.ll.leshare.Helper.MyAlert;
import androidpath.ll.leshare.Helper.ParseConstants;
import androidpath.ll.leshare.Helper.ProcessBarHelper;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditFriendsActivity extends AppCompatActivity {
    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @InjectView(R.id.edit_friends_progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.edit_friends_list)
    ListView mFriendsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        ButterKnife.inject(this);
        mFriendsListView.setEmptyView(mProgressBar);
        ProcessBarHelper.startProcess(mProgressBar);

        //make list checkable to implement "select" / "deselect"
        mFriendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFriendsListView.isItemChecked(position)) {
                    //add Friend
                    mFriendsRelation.add(mUsers.get(position));
                } else {
                    //remove Friend
                    mFriendsRelation.remove(mUsers.get(position));
                }

                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //setup current user as the one who Logged in
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION);

        //load list of user
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    //success
                    ProcessBarHelper.completeProcess(mProgressBar);
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i++] = user.getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    mFriendsListView.setAdapter(adapter);

                    addFriendCheckmarks();


                } else {
                    Log.e(TAG, e.getMessage());
                    MyAlert.showAlertDialog(EditFriendsActivity.this, getString(R.string.error_title), e.getMessage());
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    //list returned
                    for (int i = 0; i < mUsers.size(); i++) {
                        ParseUser user = mUsers.get(i);
                        for (ParseUser friend : friends) {
                            //if person is one of user's friends, check the item.
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                mFriendsListView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
