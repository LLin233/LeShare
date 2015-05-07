package androidpath.ll.leshare.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignUpActivity extends Activity {

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
            showSignUpAlertDialog();

        } else {
            //TODO create new user;
            // Doc: https://www.parse.com/docs/android/guide#users
        }

    }

    private void showSignUpAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle(getString(R.string.signup_error_title))
                .setMessage(getString(R.string.signup_error_msg))
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
    }

}
