package com.example.buinam.mapchatdemo.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.buinam.mapchatdemo.R;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_UserNameLogin;
    private EditText edt_PassWordLogin;
    private Button bt_Login;
    private Button bt_ForgotPassword;
    private SignInButton bt_LoginGoogle;
    private LoginButton bt_LoginFacebook;
    private Button bt_Register;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Login facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        //
        bt_LoginFacebook = (LoginButton) findViewById(R.id.btLoginFacebook);
        bt_LoginGoogle = (SignInButton) findViewById(R.id.btLoginGoogle);
        edt_UserNameLogin = (EditText) findViewById(R.id.edtuserNameLogin);
        edt_PassWordLogin = (EditText) findViewById(R.id.edtpassWordLogin);
        bt_Login = (Button) findViewById(R.id.btLogin);
        bt_ForgotPassword = (Button) findViewById(R.id.btForgotPassword);
        bt_Register = (Button) findViewById(R.id.btregisterUser);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        //
        //Login google.com
        bt_LoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        edt_PassWordLogin.addTextChangedListener(mTextWatcher);
        edt_UserNameLogin.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues();
        bt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edt_UserNameLogin.getText().toString();
                String passWord = edt_PassWordLogin.getText().toString();
                username.trim();
                passWord.trim();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                progressBar.setVisibility(View.VISIBLE);
                LoginUser(username + "@gmail.com", passWord);
            }
        });
        bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    //
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues() {

        String s1 = edt_UserNameLogin.getText().toString();
        String s2 = edt_PassWordLogin.getText().toString();

        if (s1.equals("") || s2.equals("")) {
            bt_Login.setEnabled(false);
            bt_Login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorButtonDisEnable));
        } else {
            bt_Login.setEnabled(true);
            bt_Login.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorButtonEnable));
        }
    }
    //

    private void LoginUser(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                showErrorMessageToUser(getString(R.string.login_error_message));


                            }
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            showErrorMessageToUser(getString(R.string.login_error_message));
                        }

                    }
                });
    }
    private void showErrorMessageToUser(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(getString(R.string.login_error_title))
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

