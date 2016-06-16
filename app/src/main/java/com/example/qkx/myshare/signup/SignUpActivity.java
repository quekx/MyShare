package com.example.qkx.myshare.signup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.utils.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qkx on 16/5/30.
 */
public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.edtTxt_username_sign_up)
    EditText edtTxtUsername;

    @Bind(R.id.edtTxt_password_sign_up)
    EditText edtTxtPassword;

    @Bind(R.id.edtTxt_nickname_sign_up)
    EditText edtTxtNickname;

    @Bind(R.id.btn_do_sign_up)
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_do_sign_up)
    void signUp() {
        btnSignUp.setEnabled(false);

        final String username = edtTxtUsername.getText().toString();
        final String password = edtTxtPassword.getText().toString();
        final String nickname = edtTxtNickname.getText().toString();
        if (!isValid(username, password, nickname)) {
            ToastUtil.showToastShort(this, "format error!");
            btnSignUp.setEnabled(true);
            return;
        }

        AVQuery<AVObject> query = new AVQuery<>(Constants.TABLE_NAME_USER);
        query.whereEqualTo(Constants.KEY_USERNAME, username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    ToastUtil.showToastShort(SignUpActivity.this, "network error!");
                    onSignUpFailed();
                } else if (!list.isEmpty()) {
                    ToastUtil.showToastShort(SignUpActivity.this, "username exist!");
                    onSignUpFailed();
                } else {
                    doSignUp(username, password, nickname);
                }
            }
        });
    }

    private void doSignUp(String username, String password, String nickname) {
        AVObject userObject = new AVObject(Constants.TABLE_NAME_USER);
        userObject.put(Constants.KEY_USERNAME, username);
        userObject.put(Constants.KEY_PASSWORD, password);
        userObject.put(Constants.KEY_NICKNAME, nickname);
        userObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    onSignUpSuccess();
                } else {
                    ToastUtil.showToastShort(SignUpActivity.this, "sign up failed!");
                    onSignUpFailed();
                }
                btnSignUp.setEnabled(true);
            }
        });
    }


    private void onSignUpSuccess() {
        ToastUtil.showToastShort(this, "sign up success!");
        finish();
    }

    private void onSignUpFailed() {

    }

    private boolean isValid(String username, String password, String nickname) {
        boolean isValid = true;
        if (username == null || username.length() == 0) {
            edtTxtUsername.setError("username is empty!");
            isValid = false;
        } else {
            edtTxtUsername.setError(null);
        }
        if (nickname == null || nickname.length() == 0) {
            edtTxtNickname.setError("nickname is empty!");
            isValid = false;
        } else {
            edtTxtUsername.setError(null);
        }
        if (password == null || password.length() < 6) {
            edtTxtPassword.setError("password should be more than 6 characters!");
            isValid = false;
        } else {
            edtTxtPassword.setError(null);
        }
        return isValid;
    }
}
