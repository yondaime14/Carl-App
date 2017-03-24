package com.carllewis14.recyclerview.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carllewis14.recyclerview.Activities.ProfileActivity;
import com.carllewis14.recyclerview.R;
import com.carllewis14.recyclerview.utils.Constants;

import rx.subscriptions.CompositeSubscription;


public class ChangePasswordDialog extends DialogFragment {

    /*
    Callback interface
     will notify parent activity when process is complete
     */

    public interface Listener {
        void onPasswordChanged();
    }

    public static final String TAG = ChangePasswordDialog.class.getSimpleName();

    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private Button mBtChangedPassword;
    private Button mBtcancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldPassword;
    private TextInputLayout mTiNewPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription compositeSubscription;
    private String mToken;
    private String mEmail;
    private Listener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_password, container, false);
        compositeSubscription = new CompositeSubscription();
        getData();
        initViews(view);

        return view;
    }


    private void getData() {

        Bundle bundle = getArguments();

        mToken = bundle.getString(Constants.TOKEN);
        mEmail = bundle.getString(Constants.EMAIL);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ProfileActivity)context;
    }

    private void initViews(View view) {

        mEtOldPassword = (EditText) view.findViewById(R.id.et_old_password);
        mEtNewPassword = (EditText) view.findViewById(R.id.et_new_password);
        mTiOldPassword = (TextInputLayout) view.findViewById(R.id.ti_old_password);
        mTiNewPassword = (TextInputLayout) view.findViewById(R.id.ti_new_password);

        mTvMessage = (TextView) view.findViewById(R.id.tv_message);
        mBtChangedPassword = (Button) view.findViewById(R.id.btn_change_password);
        mBtcancel = (Button) view.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);

        mBtChangedPassword.setOnClickListener(view -> changePassword());
        mBtcancel.setOnClickListener(view -> dismiss());

    }

    private void changePassword() {

        setError();

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();

        int invalid = 0;

        
    }

    private void setError() {
    }


}
