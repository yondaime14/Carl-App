package com.carllewis14.recyclerview.fragments;

import android.os.Bundle;
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

/**
 * Handles the Register Activity of user
 */
public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtRegister;
    private TextView mTVLogin;
    private TextInputLayout mTiName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressbar;

    private CompositeSubscription compositeSubscription;


    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        compositeSubscription = new CompositeSubscription();
        initViews(view);
        return view;
    }

    private void initViews(View v) {

        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtName = (EditText) v.findViewById(R.id.et_name);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mBtRegister = (Button) v.findViewById(R.id.btn_register);
        mTVLogin = (TextView) v.findViewById(R.id.tv_login);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiName = (TextInputLayout) v.findViewById(R.id.ti_name);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mProgressbar = (ProgressBar) v.findViewById(R.id.progress);

        mBtRegister.setOnClickListener(view -> register());
        mTVLogin.setOnClickListener(view -> goToLogin());
    }

    /**
     * Register Actions
     *
     */

    private void register() {

        setError();

        //assign the fields to named variables

        String name = mEtName.getText().toString();
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        /**
         * If fields are empty error messages
         */

        int invalid = 0;

        if (!validateFields(name)) {

            invalid++;
            mTiName.setError("Name should not be empty!");
        }

        if (!validateEmail(email)) {

            invalid++;
            mTiEmail.setError("Email should be valid!");
        }

        if (validateFields(password)) {

            invalid++;
            mTiPassword.setError("Password shold not be empty!");
        }

        /**
         * if fields are fine
         */
        if (invalid == 0) {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);

            mProgressbar.setVisibility(View.VISIBLE);
            registerProcess(user);
        } else {
            showSnackBarMessage("Enter Valid Details");
        }


    }

    /**
     * if Fields are empty validation fail
     */
    private void setError(){

        mTiName.setError(null);
        mTiEmail.setError(null);
        mTiPassword.setError(null);

    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {
            Snackbar.make(getView(), message,Snackbar.LENGTH_LONG).show();
        }
    }

    private void registerProcess(User user) {

        compositeSubscription.add(NetworkUtil.getRetrofit().register(user)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressbar.setVisibility(View.GONE);
        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        mProgressbar.setVisibility(View.GONE);

        if (error instanceof HttpException) {
            Gson gson = new GsonBuilder().create();

            try {
                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody, Response.class);
                showSnackBarMessage(response.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }

    }

    private void goToLogin() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
        ft.commit();

    }

    /**
     * this unsubscribes the subscription to prevent memory leak
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }
}
