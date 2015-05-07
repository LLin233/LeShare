package androidpath.ll.leshare.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidpath.ll.leshare.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends Activity {
    @InjectView(R.id.login_username_input)
    EditText mUsernameInput;
    @InjectView(R.id.login_password_input)
    EditText mPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }


    //test the input for development purpose for now.

    @OnClick(R.id.btn_login)
    void login() {
        String msg = "Username: " + mUsernameInput.getText() +
                "\nPassword: " + mPasswordInput.getText();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        //TODO implement login later on
        //TODO design layout
    }

    @OnClick(R.id.login_signUp_label)
    void goToSignUp(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
