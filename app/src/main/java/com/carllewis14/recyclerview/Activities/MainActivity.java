package com.carllewis14.recyclerview.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.carllewis14.recyclerview.R;
import com.carllewis14.recyclerview.fragments.LoginFragment;
import com.carllewis14.recyclerview.fragments.ResetPasswordDialog;

/**
 * This Main Activity is a controller for fragments
 * uses onNewIntent method (called when URI is opened)
 */

public class MainActivity extends AppCompatActivity implements ResetPasswordDialog.Listener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private LoginFragment mLoginFragment;
    private ResetPasswordDialog mResetPasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadFragment();
        }
    }

    private void loadFragment() {

        if (mLoginFragment == null) {

            mLoginFragment = new LoginFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentFrame, mLoginFragment,LoginFragment.TAG).commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String data = intent.getData().getLastPathSegment();
        Log.d(TAG, "onNewIntent: "+data);

        mResetPasswordDialog = (ResetPasswordDialog) getSupportFragmentManager().findFragmentByTag(ResetPasswordDialog.TAG);

        if (mResetPasswordDialog != null)
            mResetPasswordDialog.setToken(data);

    }

    @Override
    public void onPasswordReset(String message) {

        showSnackBarMessage(message);

    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.activity_main),message,Snackbar.LENGTH_LONG).show();
    }


}
