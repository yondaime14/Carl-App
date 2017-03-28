package com.carllewis14.recyclerview.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carllewis14.recyclerview.R;
import com.carllewis14.recyclerview.datamodel.User;
import com.carllewis14.recyclerview.fragments.ChangePasswordDialog;
import com.carllewis14.recyclerview.network.NetworkUtil;
import com.carllewis14.recyclerview.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileActivity extends AppCompatActivity implements ChangePasswordDialog.Listener {

    public static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView mTvName;
    private TextView mTvEmail;
    private TextView mTvDate;
    private Button mBtChangePassword;
    private Button mBtLogout;

    private ProgressBar mProgressbar;

    private SharedPreferences mSharedpref;
    private String mToken;
    private String mEmail;

    private CompositeSubscription compositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        compositeSubscription = new CompositeSubscription();
        initViews();
        initSharedPref();
        loadProfile();
    }


    /*
    initialize views
     */
    private void initViews(){

        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mBtChangePassword = (Button) findViewById(R.id.btn_change_password);
        mBtLogout = (Button) findViewById(R.id.btn_logout);

        mBtChangePassword.setOnClickListener(view -> showDialog());
        mBtLogout.setOnClickListener(view -> logout());

    }

    /*
    initialize shared prefs
     */
    private void initSharedPref(){

        mSharedpref = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedpref.getString(Constants.TOKEN, "");
        mEmail = mSharedpref.getString(Constants.EMAIL, "");

    }


    private void loadProfile(){

        compositeSubscription.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void logout(){

        SharedPreferences.Editor editor = mSharedpref.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.TOKEN, "");


    }

    private void handleResponse(User user) {



    }

    private void handleError(Throwable error) {

    }


    @Override
    public void onPasswordChanged() {

    }




    private void showDialog(){

    }


}
