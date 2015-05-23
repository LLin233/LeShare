package androidpath.ll.leshare.View.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import androidpath.ll.leshare.Adapter.UserAdapter;
import androidpath.ll.leshare.Model.ParseConstants;
import androidpath.ll.leshare.R;
import androidpath.ll.leshare.Utils.MyAlert;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Le on 2015/5/8.
 */
public class FriendsFragment extends Fragment {
    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;

    @InjectView(R.id.friendGrid)
    protected GridView mGridView;
    @InjectView(android.R.id.empty)
    TextView mEmptyTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.inject(this, rootView);
        mGridView.setEmptyView(mEmptyTextView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        //getListView().setEmptyView(mProgressBar);
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION);


        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();

        //get list of friends, in AscendingOrder
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i++] = user.getUsername();
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).update(mFriends);
                    }


                } else {
                    Log.e(TAG, e.getMessage());
                    MyAlert.showAlertDialog(getActivity(), getString(R.string.error_title), e.getMessage());
                }

            }
        });
    }
}
