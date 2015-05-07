package androidpath.ll.leshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidpath.ll.leshare.View.LoginActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
