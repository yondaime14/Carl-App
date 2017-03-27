package com.carllewis14.recyclerview.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carllewis14.recyclerview.R;
import com.carllewis14.recyclerview.fragments.ChangePasswordDialog;

public class ProfileActivity extends AppCompatActivity implements ChangePasswordDialog.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override
    public void onPasswordChanged() {

    }
}
