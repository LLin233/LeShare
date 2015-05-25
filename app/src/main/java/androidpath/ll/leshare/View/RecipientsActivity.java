package androidpath.ll.leshare.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import androidpath.ll.leshare.Adapter.UserAdapter;
import androidpath.ll.leshare.Model.ParseConstants;
import androidpath.ll.leshare.R;
import androidpath.ll.leshare.Utils.FileHelper;
import androidpath.ll.leshare.Utils.MyAlert;
import androidpath.ll.leshare.Utils.ProcessBarHelper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipientsActivity extends Activity {
    public static final String TAG = RecipientsActivity.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mMenuItem_Send;
    protected Uri mMediaUri;
    protected String mFileType;
    protected int mOrientation;


    @InjectView(R.id.recipients_progressBar)
    ProgressBar mProgressBar;
    @InjectView(R.id.friendsGrid)
    GridView mGridView;
    @InjectView(android.R.id.empty)
    TextView mEmptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.inject(this);
        mGridView.setEmptyView(mEmptyTextView); //if list is empty
        mGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //get file
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
        mOrientation = getIntent().getExtras().getInt(ParseConstants.KEY_ROTATION);

        //add click item event
        mGridView.setOnItemClickListener(mOnClickListener);
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
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter) mGridView.getAdapter()).update(mFriends);
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                    MyAlert.showAlertDialog(RecipientsActivity.this, getString(R.string.error_title), e.getMessage());
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
            //send message to back-end
            ParseObject message = createMessgae();

            if (message != null) {
                sendMessage(message);
                finish(); // close activity after send out the message.
            } else {
                //error
                MyAlert.showAlertDialog(this, getString(R.string.msg_error_selecting_file), getString(R.string.msg_error_selecting_file_title));
            }

            Log.i(TAG, "Send msg");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ParseObject createMessgae() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);

        //add data to message
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_USERNAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if (fileBytes == null || fileBytes.length == 0) {
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);  //limit 10MB
            }

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            message.put(ParseConstants.KEY_ROTATION, mOrientation);

            return message;
        }
    }


    protected void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientsActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    sendPushNotifications();
                } else {
                    MyAlert.showAlertDialog(RecipientsActivity.this, "There was a error sending message, please try again.", getString(R.string.msg_error_selecting_file_title));
                }
            }
        });
    }

    protected void sendPushNotifications() {
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds()); //targeting those users in the RecipientIds.
        //send Push Notification
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage(getString(R.string.msg_push,
                ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();

    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) { // the one who user decide to send msg to
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }


    protected AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // show send icon
            if (mGridView.getCheckedItemCount() > 0) {
                mMenuItem_Send.setVisible(true);
            } else {
                mMenuItem_Send.setVisible(false);
            }
            //show checkImage
            ImageView checkImageView = ButterKnife.findById(view, R.id.checkImageView);
            if (mGridView.isItemChecked(position)) {
                checkImageView.setVisibility(View.VISIBLE);
            } else {
                checkImageView.setVisibility(View.INVISIBLE);
            }
        }
    };

}


