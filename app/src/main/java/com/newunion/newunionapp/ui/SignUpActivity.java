package com.newunion.newunionapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.newunion.newunionapp.R;
import com.newunion.newunionapp.pojo.User;
import com.newunion.newunionapp.rest.RestClient;
import com.newunion.newunionapp.rest.api.UsersService;
import com.newunion.newunionapp.rx.SimpleSubscriber;
import com.newunion.newunionapp.utils.NetworkUtils;
import com.newunion.newunionapp.utils.NewUnionLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p> Signup Activity
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText mNameEditText;

    @BindView(R.id.email)
    EditText mEmailEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;

    private Subscription mSubscription;

    private MaterialDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_create_account)
    public void createAccount(View view) {
        String name = mNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.getTrimmedLength(name) == 0) {
            Toast.makeText(this, getString(R.string.error_name_empty), Toast.LENGTH_SHORT).show();
            return;
        }

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

        User creationInfo = new User(name, email, password);
        UsersService userService = RestClient.getInstance().getUserService();
        mSubscription = userService.signUp(creationInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<Response>() {
                    @Override
                    public void onNext(Response response) {
                        hideProgressDialog();
                        if (response.getStatus() == 200) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.message_account_created), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, getString(R.string.error_cannot_create_account), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();

                        if (e instanceof RetrofitError) {
                            String emailAlreadyInUse = getString(R.string.error_email_account_is_already_in_use);

                            String errorString = ((RetrofitError) e).getBody().toString();
                            if (errorString.contains(emailAlreadyInUse)) {
                                Toast.makeText(SignUpActivity.this, emailAlreadyInUse, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, getString(R.string.error_cannot_create_account), Toast.LENGTH_SHORT).show();
                            }
                        }
                        NewUnionLog.e(Log.getStackTraceString(e));
                    }
                });
    }

    private void showProgressDialog() {
        mLoadingDialog = new MaterialDialog.Builder(this)
                .title(getString(R.string.please_wait))
                .content(R.string.loading)
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

    private void unsubscribe() {
        if (mSubscription != null) {
            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
            mSubscription = null;
        }
    }
}
