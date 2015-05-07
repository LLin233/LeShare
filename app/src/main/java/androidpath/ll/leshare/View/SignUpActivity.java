package androidpath.ll.leshare.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import androidpath.ll.leshare.Helper.MyAlert;
import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignUpActivity extends Activity {
    public static final String TAG = SignUpActivity.class.getSimpleName();

    @InjectView(R.id.signup_input_name)
    protected EditText mUsername;
    @InjectView(R.id.signup_input_password)
    protected EditText mPassword;
    @InjectView(R.id.signup_input_email)
    protected EditText mEmail;

    //TODO May be some animation here to the button clicking
    @InjectView(R.id.signup_btn_signup)
    protected Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.signup_btn_signup)
    void signUp() {
        //white space should not be considered as legit character.
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            //alert user that sign up info is not completed
            MyAlert.showSignUpAlertDialog(SignUpActivity.this, getString(R.string.signup_error_title), getString(R.string.signup_error_msg));

        } else {
            //TODO create new user;
            // Doc: https://www.parse.com/docs/android/guide#users
            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        MyAlert.showSignUpAlertDialog(SignUpActivity.this, getString(R.string.signup_error_title), e.getMessage());
                    }
                }
            });
        }

    }
}
