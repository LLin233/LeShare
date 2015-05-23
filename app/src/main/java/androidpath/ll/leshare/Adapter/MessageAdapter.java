package androidpath.ll.leshare.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import androidpath.ll.leshare.R;
import androidpath.ll.leshare.Model.ParseConstants;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Le on 2015/5/12.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mMessages;


    public MessageAdapter(Context context, List<ParseObject> messages) {
        super(context, R.layout.message_list_item, messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ParseObject message = mMessages.get(position);

        //create relative time window
        Date createAt = message.getCreatedAt();
        long now = new Date().getTime();
        String convertedDate = DateUtils.getRelativeTimeSpanString(
                createAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS
        ).toString();

        //set icon
        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(R.mipmap.ic_picture);
        } else {
            holder.iconImageView.setImageResource(R.mipmap.ic_video);
        }
        holder.senderNameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        holder.timeLabel.setText(convertedDate);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.message_Icon)
        ImageView iconImageView;
        @InjectView(R.id.message_sender_label)
        TextView senderNameLabel;
        @InjectView(R.id.message_time_label)
        TextView timeLabel;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void update(List<ParseObject> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }
}


