package androidpath.ll.leshare.View;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import androidpath.ll.leshare.Helper.MyAlert;
import androidpath.ll.leshare.Helper.ParseConstants;
import androidpath.ll.leshare.Helper.ProcessBarHelper;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipientsActivity extends AppCompatActivity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mMenuItem_Send;

    @InjectView(R.id.recipients_progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.recipients_friends_list)
    ListView mFriendsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);
        ButterKnife.inject(this);
        mFriendsListView.setEmptyView((TextView) findViewById(android.R.id.empty)); //if list is empty
        mFriendsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //add click item event
        mFriendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFriendsListView.getCheckedItemCount() > 0) {
                    mMenuItem_Send.setVisible(true);
                } else {
                    mMenuItem_Send.setVisible(false);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_RELATION);
        ProcessBarHelper.startProcess(mProgressBar);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();

        //get list of friends, in AscendingOrder
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                ProcessBarHelper.completeProcess(mProgressBar);
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i++] = user.getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    mFriendsListView.setAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    MyAlert.showSignUpAlertDialog(RecipientsActivity.this, getString(R.string.error_title), e.getMessage());
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mMenuItem_Send = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            //TODO send message to back-end
            ParseObject message = createMessgae();
            //send(message);
            Log.i(TAG, "Send msg");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ParseObject createMessgae() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());

        return message;

    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mFriendsListView.getCount(); i++) {
            if (mFriendsListView.isItemChecked(i)) { // the one who user decide to send msg to
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

}
