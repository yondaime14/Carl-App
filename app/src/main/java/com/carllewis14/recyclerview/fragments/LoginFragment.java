package com.carllewis14.recyclerview.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.carllewis14.recyclerview.network.NetworkUtil;
import com.carllewis14.recyclerview.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.carllewis14.recyclerview.utils.Validation.validateEmail;
import static com.carllewis14.recyclerview.utils.Validation.validateFields;

/**
 * Login fragment class handler
 */
public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private TextView mTVRegister;
    private TextView mTVForgotPassword;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription compositeSubscription;
    private SharedPreferences mSharedPreferences;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        compositeSubscription = new CompositeSubscription();
        initViews(view);
        initSharedPrefs();
        return view;

    }

    /**
     *
     * @param v
     *
     * Assign member variables to fields and Views from Layout
     * set onclick action for buttons
     */

    private void initViews(View v) {

        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtLogin = (Button) v.findViewById(R.id.btn_login);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mTVRegister = (TextView) v.findViewById(R.id.tv_register);
        mTVForgotPassword = (TextView) v.findViewById(R.id.tv_forgot_password);

        mBtLogin.setOnClickListener(view -> login());
        mTVRegister.setOnClickListener(view -> goToRegister());
        mTVForgotPassword.setOnClickListener(view -> showDialog());

    }

    /**
     * Manage SharedPrefs Here
     */

    private void initSharedPrefs() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    /**
    Validate fields if empty
     **/
    private void login() {

        setError();

        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int invalid = 0;

        if (!validateEmail(email)) {
            invalid++;
            mEtEmail.setError("Email is invalid!");
        }

        if (!validateFields(password)){
            invalid++;
            mEtPassword.setError("Password should not be empty!");
        }

        if (invalid == 0){
            loginProcess(email,password);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            showSnackBarMessage("Enter Valid Details");
        }

    }


    /**
     *
     * @param email
     * @param password
     *
     */
    private void loginProcess(String email, String password) {

        compositeSubscription.add(NetworkUtil.getRetrofit(email, password).login()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse,this::handleError));

    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.EMAIL, response.getMessage());
        editor.apply();

        mEtEmail.setText(null);
        mEtPassword.setText(null);

        Intent i = new Intent(getActivity(), ProfileActivity.class);
        startActivity(i);
    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();


            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBarMessage("Network Error!");
        }

    }

    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }


    private void showSnackBarMessage(String message) {

        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /*
    Transition to the Register fragment if Register button clicked
     */

    private void goToRegister() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment regFragment = new RegisterFragment();
        ft.replace(R.id.fragmentFrame,regFragment,RegisterFragment.TAG);
        ft.commit();

    }


    private void showDialog() {

        ResetPasswordDialog fragment = new ResetPasswordDialog();

        fragment.show(getFragmentManager(), ResetPasswordDialog.TAG);

    }

    /*
    This will help prevent memory leak
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
