package androidpath.ll.leshare.View;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ViewImageActivity extends Activity {

    @InjectView(R.id.imageView)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        ButterKnife.inject(this);

        Uri imageUri = getIntent().getData();
        Picasso.with(this).load(imageUri.toString()).into(mImageView);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // after delay close imageView
                finish();
            }
        }, 10 * 1000);

    }

}
