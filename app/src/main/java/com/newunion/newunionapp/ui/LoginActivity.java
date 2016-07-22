package com.newunion.newunionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.newunion.newunionapp.R;
import com.newunion.newunionapp.pojo.LoginResult;
import com.newunion.newunionapp.pojo.User;
import com.newunion.newunionapp.rest.RestClient;
import com.newunion.newunionapp.rest.api.UsersService;
import com.newunion.newunionapp.rx.SimpleSubscriber;
import com.newunion.newunionapp.utils.LoginPreferences;
import com.newunion.newunionapp.utils.NetworkUtils;
import com.newunion.newunionapp.utils.NewUnionLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p> Login Activity
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText mEmailEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;

    private MaterialDialog mLoadingDialog;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LoginPreferences.isLogged(this)) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_login)
    public void login(View view) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.getTrimmedLength(email) == 0) {
            Toast.makeText(this, getString(R.string.error_email_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password) || TextUtils.getTrimmedLength(password) == 0) {
            Toast.makeText(this, getString(R.string.error_password_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.error_no_network_connection), Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        unsubscribe();

        User user = new User("", email, password);
        UsersService userService = RestClient.getInstance().getUserService();
        mSubscription = userService.signIn(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<LoginResult>() {
                    @Override
                    public void onNext(LoginResult loginResult) {
                        hideProgressDialog();
                        if (loginResult == null) {
                            Toast.makeText(LoginActivity.this, getString(R.string.error_email_not_yet_registered), Toast.LENGTH_SHORT).show();
                        } else {
                            String token = loginResult.getToken();
                            if (TextUtils.isEmpty(token) || TextUtils.getTrimmedLength(token) == 0) {
                                Toast.makeText(LoginActivity.this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                            } else {
                                NewUnionLog.d(token);
                                LoginPreferences.saveToken(getApplicationContext(), token);
                                goToMainActivity();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        Toast.makeText(LoginActivity.this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.button_signup)
    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void showProgressDialog() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.please_wait))
                .content(getString(R.string.loading))
                .widgetColor(getResources().getColor(R.color.colorPrimary))
                .progress(true, 0)
                .show();
    }

    private void hideProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
        unsubscribe();
    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);

        LoginActivity.this.finish();
    }

    private void unsubscribe() {
        if (mSubscription != null) {
            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
            mSubscription = null;
        }
    }
}
