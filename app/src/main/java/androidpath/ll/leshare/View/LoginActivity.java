package androidpath.ll.leshare.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import androidpath.ll.leshare.Helper.MyAlert;
import androidpath.ll.leshare.Helper.ProcessBarHelper;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    //TODO design layout
    @InjectView(R.id.login_username_input)
    protected EditText mUsername;
    @InjectView(R.id.login_password_input)
    protected EditText mPassword;
    @InjectView(R.id.login_progressBar)
    protected ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

    }


    //test the input for development purpose for now.

    @OnClick(R.id.btn_login)
    void login() {
        String msg = "Username: " + mUsername.getText() +
                "\nPassword: " + mPassword.getText();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        //white space should not be considered as legit character.
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            //alert user that sign up info is not completed
            MyAlert.showSignUpAlertDialog(LoginActivity.this, getString(R.string.login_error_title), getString(R.string.login_error_msg));

        } else {
            //Login
            ProcessBarHelper.startProcess(progressBar);
            // Doc: https://www.parse.com/docs/android/guide#users
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {

                    ProcessBarHelper.completeProcess(progressBar);

                    if (e == null) {
                        //login successfully, go back to Inbox (MainActivity)
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        MyAlert.showSignUpAlertDialog(LoginActivity.this, getString(R.string.login_error_title), e.getMessage());
                    }
                }
            });

        }


    }

    @OnClick(R.id.login_signUp_label)
    void goToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
