package androidpath.ll.leshare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidpath.ll.leshare.R;
import androidpath.ll.leshare.Utils.MD5Util;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Le on 2015/5/12.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mUsers;


    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_items, users);
        mContext = context;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.user_items, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        ParseUser user = mUsers.get(position);
        String email = user.getEmail().toLowerCase();
        if (email.equals("")) {
            holder.userImageView.setImageResource(R.mipmap.avatar_empty);
        } else {
            // http://en.gravatar.com/site/implement/images/java/
            //get profile pic from gravatar base on email
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash
                    + "?s=204&d=404";
            Picasso.with(mContext)
                    .load(gravatarUrl)
                    .placeholder(R.mipmap.avatar_empty)  //if 404 code returned, show default image.
                    .into(holder.userImageView);
        }

        //set icon
//        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
//            holder.iconImageView.setImageResource(R.mipmap.ic_picture);
//        } else {
//            holder.iconImageView.setImageResource(R.mipmap.ic_video);
//        }
        holder.userNameLabel.setText(user.getUsername());
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.userImageView)
        ImageView userImageView;
        @InjectView(R.id.name_label)
        TextView userNameLabel;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public void update(List<ParseUser> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }
}


