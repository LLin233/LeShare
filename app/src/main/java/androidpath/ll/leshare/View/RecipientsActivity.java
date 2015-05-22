package androidpath.ll.leshare.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import androidpath.ll.leshare.Utils.FileHelper;
import androidpath.ll.leshare.Utils.MediaHelper;
import androidpath.ll.leshare.Utils.MyAlert;
import androidpath.ll.leshare.Utils.ParseConstants;
import androidpath.ll.leshare.Utils.ProcessBarHelper;
import androidpath.ll.leshare.R;
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

        //get file
        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

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

            int orientation = MediaHelper.getExifOrientation(mMediaUri, this);
            Log.d(TAG, orientation + "");
            message.put(ParseConstants.KEY_ROTATION, orientation);

            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);

            return message;
        }
    }


    protected void sendMessage(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientsActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                } else {
                    MyAlert.showAlertDialog(RecipientsActivity.this, "There was a error sending message, please try again.", getString(R.string.msg_error_selecting_file_title));
                }
            }
        });
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
