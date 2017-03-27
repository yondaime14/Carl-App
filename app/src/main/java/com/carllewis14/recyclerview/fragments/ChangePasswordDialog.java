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
import com.carllewis14.recyclerview.datamodel.Response;
import com.carllewis14.recyclerview.datamodel.User;
import com.carllewis14.recyclerview.network.NetworkUtil;
import com.carllewis14.recyclerview.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.carllewis14.recyclerview.utils.Validation.validateFields;


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

    /*
    Initialize Views
     */
    private void initViews(View v) {

        mEtOldPassword = (EditText) v.findViewById(R.id.et_old_password);
        mEtNewPassword = (EditText) v.findViewById(R.id.et_new_password);
        mTiOldPassword = (TextInputLayout) v.findViewById(R.id.ti_old_password);
        mTiNewPassword = (TextInputLayout) v.findViewById(R.id.ti_new_password);

        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangedPassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtcancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        mBtChangedPassword.setOnClickListener(view -> changePassword());
        mBtcancel.setOnClickListener(view -> dismiss());

    }

    private void changePassword() {

        setError();

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();

        int invalid = 0;

        if (!validateFields(oldPassword)) {

            invalid++;
            mTiOldPassword.setError("Please enter your old password");
        }

        if (!validateFields(newPassword)) {
            invalid++;
            mTiNewPassword.setError("Please enter the new passowrd");
        }

        if (invalid == 0) {

            User user = new User();
            user.setPassword(oldPassword);
            user.setNewPassword(newPassword);
            changePasswordProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }



    private void setError() {

        mTiOldPassword.setError(null);
        mTiNewPassword.setError(null);
    }

    private void changePasswordProgress(User user) {
        compositeSubscription.add(NetworkUtil.getRetrofit(mToken).changePassword(mEmail,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));

    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);
        mListener.onPasswordChanged();
        dismiss();

    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();


            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showMessage(response.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            showMessage("Error!");
        }

    }

    /*
    Display message for Success or Error
     */
    private void showMessage(String message) {

        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText(message);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
