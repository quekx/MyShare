package com.example.qkx.myshare.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.home.HomeActivity;
import com.example.qkx.myshare.setting.Setting;
import com.example.qkx.myshare.signup.SignUpActivity;
import com.example.qkx.myshare.utils.PreferenceUtil;
import com.example.qkx.myshare.utils.ToastUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qkx on 16/5/29.
 */
public class LoginActivity extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getSimpleName();


    private EditText edtTxtUsername;

    private EditText edtTxtPassword;

    private Button btnLogin;

    @Bind(R.id.tv_link_sign_up)
    TextView tvLinkSignUp;

//    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        edtTxtUsername = (EditText) findViewById(R.id.edtTxt_username);
        edtTxtPassword = (EditText) findViewById(R.id.edtTxt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
//        btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @OnClick(R.id.tv_link_sign_up)
    void startSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void login() {
        btnLogin.setEnabled(false);

        String username = edtTxtUsername.getText().toString();
        String password = edtTxtPassword.getText().toString();
        if (!isValid(username, password)) {
            onLoginFailed();
            return;
        }

        AVQuery<AVObject> usernameQuery = new AVQuery<>(Constants.TABLE_NAME_USER);
        usernameQuery.whereEqualTo(Constants.KEY_USERNAME, username);
        AVQuery<AVObject> passwordQuery = new AVQuery<>(Constants.TABLE_NAME_USER);
        passwordQuery.whereEqualTo(Constants.KEY_PASSWORD, password);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(usernameQuery, passwordQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    onLoginFailed();
                } else if (!list.isEmpty()) {
                    AVObject avObject = list.get(0);
                    String userId = avObject.getObjectId();
                    String username = avObject.getString(Constants.KEY_USERNAME);
                    String password = avObject.getString(Constants.KEY_PASSWORD);
                    String nickname = avObject.getString(Constants.KEY_NICKNAME);

                    PreferenceUtil.putString(LoginActivity.this, Constants.KEY_USER_ID, userId);
                    PreferenceUtil.putString(LoginActivity.this, Constants.KEY_USERNAME, username);
                    PreferenceUtil.putString(LoginActivity.this, Constants.KEY_PASSWORD, password);
                    PreferenceUtil.putString(LoginActivity.this, Constants.KEY_NICKNAME, nickname);

                    Setting.userId = userId;
                    onLoginSuccess(userId, nickname);
                } else {
                    onUserNotExist();
                }
            }
        });
    }

    private void onUserNotExist() {
        ToastUtil.showToastShort(this, "user do not exist!!");
        btnLogin.setEnabled(true);
    }

    private void onLoginSuccess(String userId, String nickname) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.KEY_USER_ID, userId);
        intent.putExtra(Constants.KEY_NICKNAME, nickname);
        startActivity(intent);

        btnLogin.setEnabled(true);
    }

    private void onLoginFailed() {
        ToastUtil.showToastShort(this, "login failed!");
        btnLogin.setEnabled(true);
    }

    private boolean isValid(String username, String password) {
        boolean isValid = true;
        if (username == null || username.length() == 0) {
            edtTxtUsername.setError("username can not be empty!");
            isValid = false;
        } else {
            edtTxtUsername.setError(null);
        }
        if (password == null || password.length() < 6) {
            edtTxtPassword.setError("more than 6 characters");
            isValid = false;
        } else {
            edtTxtPassword.setError(null);
        }
        return isValid;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
