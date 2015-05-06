package androidpath.ll.leshare;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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

    @OnClick(R.id.btn_login)
    void login() {
        String msg = "Username: " + mUsernameInput.getText() +
                "\nPassword: " + mPasswordInput.getText();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
