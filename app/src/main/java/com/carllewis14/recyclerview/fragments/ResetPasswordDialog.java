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

import com.carllewis14.recyclerview.Activities.MainActivity;
import com.carllewis14.recyclerview.R;
import com.carllewis14.recyclerview.datamodel.Response;
import com.carllewis14.recyclerview.datamodel.User;
import com.carllewis14.recyclerview.network.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.carllewis14.recyclerview.utils.Validation.validateEmail;
import static com.carllewis14.recyclerview.utils.Validation.validateFields;


public class ResetPasswordDialog extends DialogFragment {

    public interface Listener {

        void onPasswordReset(String message);
    }


    public static final String TAG = ResetPasswordDialog.class.getSimpleName();

    private EditText mEtEmail;
    private EditText mEtToken;
    private EditText mEtPassword;
    private Button mBtResetPassword;
    private TextView mTVMessage;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiToken;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription compositeSubscription;

    private String mEmail;
    /*
    checks if process is running
     */
    private boolean isInit = true;
    private Listener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_reset_password, container, false);
        compositeSubscription = new CompositeSubscription();
        initView(view);
        return view;

    }

    private void initView(View v) {

        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtToken = (EditText) v.findViewById(R.id.et_token);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtResetPassword = (Button) v.findViewById(R.id.btn_reset_password);
        mTVMessage = (TextView) v.findViewById(R.id.tv_message);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiToken = (TextInputLayout) v.findViewById(R.id.ti_token);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);

        mBtResetPassword.setOnClickListener(view -> {
            if (isInit) resetPasswordInit();
            else resetPasswordFinish();
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (MainActivity)context;
    }

    private void setEmptyFields(){
        mTiEmail.setError(null);
        mTiToken.setError(null);
        mTiPassword.setError(null);
        mTVMessage.setError(null);
    }


    /*
    set Reset token for user
     */
    public void setToken(String token) {
        mEtToken.setText(token);
    }

    private void resetPasswordInit() {

        setEmptyFields();

        mEmail = mEtEmail.getText().toString();

        int invalid = 0;

        /*
        if email field is null
         */

        if (!validateEmail(mEmail)) {

            invalid++;
            mTiEmail.setError("Please fill out email address");

        }

        if (invalid == 0) {

            mProgressBar.setVisibility(View.VISIBLE);
            resetPasswordInitProgress(mEmail);

        }

    }


    private void resetPasswordFinish() {

        setEmptyFields();

        String token = mEtToken.getText().toString();
        String password = mEtToken.getText().toString();

        int invalid = 0;

        if (!validateFields(token)) {

            invalid++;
            mTiToken.setError("Token should not be empty!");
        }

        if (!validateFields(password)) {
            invalid++;
            mTiEmail.setError("Please set a new Password");
        }

        if (invalid == 0) {

            User user = new User();
            user.setPassword(password);
            user.setToken(token);
            mProgressBar.setVisibility(View.VISIBLE);
            resetPasswordFinishProgress(user);

        }

    }


    private void resetPasswordInitProgress(String email) {

        compositeSubscription.add(NetworkUtil.getRetrofit().resetPasswordInit(email)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse,this::handleError));

    }


    private void resetPasswordFinishProgress(User user) {

        compositeSubscription.add(NetworkUtil.getRetrofit().resetPasswordFinish(mEmail, user)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        if (isInit) {
            isInit = false;
            showMessage(response.getMessage());
            mTiEmail.setVisibility(View.GONE);
            mTiToken.setVisibility(View.VISIBLE);
            mTiPassword.setVisibility(View.VISIBLE);
        } else {
            mListener.onPasswordReset(response.getMessage());
            dismiss();
        }

    }




    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();

            try {
                String errorbody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorbody, Response.class);
                showMessage(response.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showMessage("Network Error!");
        }
    }



    private void showMessage(String message) {

        mTVMessage.setVisibility(View.VISIBLE);
        mTVMessage.setText(message);
    }


}
