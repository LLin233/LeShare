package androidpath.ll.leshare.View.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import androidpath.ll.leshare.Adapter.MessageAdapter;
import androidpath.ll.leshare.Helper.ParseConstants;
import androidpath.ll.leshare.R;
import androidpath.ll.leshare.View.ViewImageActivity;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Le on 2015/5/8.
 */
public class InboxFragment extends ListFragment {

    @InjectView(R.id.progressBar)
    CircleProgressBar circleProgressBar;
    @InjectView(R.id.inbox_list_container)
    LinearLayout container;

    protected List<ParseObject> mMessages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        ButterKnife.inject(this, rootView);
        circleProgressBar.setColorSchemeResources(android.R.color.holo_blue_bright);
        circleProgressBar.setShowArrow(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        circleProgressBar.setVisibility(CircleProgressBar.VISIBLE);
        container.setVisibility(LinearLayout.GONE);

        //get message from server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());  //get all the msg under current user
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT); //latest shows first
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                container.setVisibility(LinearLayout.VISIBLE);
                circleProgressBar.setVisibility(CircleProgressBar.GONE);
                if (e == null) {
//                    //msg is found
                    mMessages = messages;
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);

//                  String[] usernames = new String[mMessages.size()];
//                  int i = 0;
//                  for (ParseObject msg : mMessages) {
//                      usernames[i] = msg.getString(ParseConstants.KEY_SENDER_NAME);
//                      i++;
//                  }
//                  ArrayAdapter<String> adapter = new ArrayAdapter<>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    } else {
                        ((MessageAdapter) getListView().getAdapter()).update(mMessages);
                    }
                }
            }
        });
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            //view Image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            intent.putExtra(ParseConstants.KEY_ROTATION, message.getInt(ParseConstants.KEY_ROTATION));
            startActivity(intent);
        } else {
            //view Video with implicit intent
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        //delete item after click it.
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if (ids.size() == 1) {
            //message is only sent to current user, delete it from server.
            message.deleteInBackground();
        } else {
            //remove recipient from list
            //ids.remove(ParseUser.getCurrentUser().getObjectId());
            //save recipient ids to message over server.
            ArrayList<String> idsToRemove = new ArrayList<String>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }
}
