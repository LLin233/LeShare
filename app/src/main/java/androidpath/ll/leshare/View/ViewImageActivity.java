package androidpath.ll.leshare.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import androidpath.ll.leshare.Utils.ParseConstants;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewImageActivity extends Activity {

    @InjectView(R.id.imageView)
    ImageView mImageView;

    protected int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.inject(this);

        Uri imageUri = getIntent().getData();
        int rotate = getIntent().getExtras().getInt(ParseConstants.KEY_ROTATION);

        Picasso.with(this).load(imageUri.toString()).rotate(rotate).into(mImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // after delay close imageView
                        finish();
                    }
                }, 10 * 1000);

            }

            @Override
            public void onError() {

            }
        });
    }

}
