package androidpath.ll.leshare.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import androidpath.ll.leshare.Adapter.UserAdapter;
import androidpath.ll.leshare.Model.ParseConstants;
import androidpath.ll.leshare.R;
import androidpath.ll.leshare.Utils.MyAlert;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditFriendsActivity extends Activity {
    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @InjectView(R.id.friendsGrid)
    GridView mGridView;
    @InjectView(android.R.id.empty)
    TextView mEmptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);


        //make list checkable to implement "select" / "deselect"
        mGridView.setEmptyView(mEmptyTextView);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(mOnClickListener);

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
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        usernames[i++] = user.getUsername();
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).update(mUsers);
                    }

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
                                mGridView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


    protected AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView checkImageView = ButterKnife.findById(view, R.id.checkImageView);

            if (mGridView.isItemChecked(position)) {
                // add the selected friend
                mFriendsRelation.add(mUsers.get(position));
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                //remove the selected friend
                mFriendsRelation.remove(mUsers.get(position));
                checkImageView.setVisibility(View.INVISIBLE);
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
    };


}
