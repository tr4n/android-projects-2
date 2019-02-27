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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buinam.mapchatdemo.R;
import com.example.buinam.mapchatdemo.design.ReferenceUrl;
import com.example.buinam.mapchatdemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText edt_NameRegister;
    private EditText edt_UserNameRegister;
    private EditText edt_PassWordRegister;
    private EditText edt_PassWordRegister2;
    private Button bt_Register;
    private Button bt_LoginUser;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    DatabaseReference dataBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //
        edt_NameRegister = (EditText) findViewById(R.id.edtNameRegister);
        edt_UserNameRegister = (EditText) findViewById(R.id.edtuserNameRegister);
        edt_PassWordRegister = (EditText) findViewById(R.id.edtpassWordRegister);
        edt_PassWordRegister2 = (EditText) findViewById(R.id.edtpassWordRegister2);
        bt_Register = (Button) findViewById(R.id.btRegister);
        bt_LoginUser = (Button) findViewById(R.id.btLoginUser);
        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        //
        mAuth = FirebaseAuth.getInstance();
        dataBaseUser = FirebaseDatabase.getInstance().getReference();
        //
        edt_NameRegister.addTextChangedListener(mTextWatcher);
        edt_PassWordRegister.addTextChangedListener(mTextWatcher);
        edt_PassWordRegister2.addTextChangedListener(mTextWatcher);
        edt_UserNameRegister.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues();
        //
        bt_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = edt_NameRegister.getText().toString();
                String userName = edt_UserNameRegister.getText().toString();
                String userPassword = edt_PassWordRegister.getText().toString();
                String userPassword2 = edt_PassWordRegister2.getText().toString();

                // Omit space
                displayName = displayName.trim();
                userName = userName.trim();
                userPassword = userPassword.trim();
                userPassword2 = userPassword2.trim();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (!userPassword.equals(userPassword2)) {
                    Toast.makeText(getApplication(), "Hai mật khẩu không trùng nhau!",
                            Toast.LENGTH_LONG).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    RegisterUser(userName, userPassword, displayName);

                }
            }
        });
        bt_LoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        String s1 = edt_PassWordRegister.getText().toString();
        String s2 = edt_PassWordRegister2.getText().toString();
        String s3 = edt_UserNameRegister.getText().toString();
        String s4 = edt_NameRegister.getText().toString();

        if (s1.equals("") || s2.equals("") || s3.equals("") || s4.equals("")) {
            bt_Register.setEnabled(false);
            bt_Register.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorButtonDisEnable));
        } else {
            bt_Register.setEnabled(true);
            bt_Register.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.textColorButtonEnable));
        }
    }
    //

    private void RegisterUser(final String username, final String password, final String displayName) {
        String emailUser = username + "@gmail.com";
        mAuth.createUserWithEmailAndPassword(emailUser, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                User u = new User();
//
                                u.setIdUser(user.getUid());
                                u.setUrlAvatar(User.getUrlAvatarDefault());
                                u.setUrlCover(User.getUrlCoverDefault());
                                u.setDisplayName(displayName);
                                u.setUserName(username);
                                u.setDayBirthdayUser(String.valueOf(11));
                                u.setMonthBirthdayUser(String.valueOf(06));
                                u.setYearBirthdayUser(String.valueOf(1995));
                                u.setGenderUser("");
                                u.setConnection(ReferenceUrl.KEY_ONLINE);
                                long createTime = new Date().getTime();
                                u.setTimeRegisterUser(String.valueOf(createTime));
                                u.setToken("0");

                                getDataFriend(user);
                                dataBaseUser.child(ReferenceUrl.CHILD_USERS).child(user.getUid()).setValue(u);
                            } else {
                                Log.d("NamBV", "onAuthStateChanged:signed_out");
                            }

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            showErrorMessageToUser(getString(R.string.title_errorRegister));
                        }

                    }
                });
    }

    private void getDataFriend(final FirebaseUser cUser) {
        dataBaseUser.child(ReferenceUrl.CHILD_USERS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    final User user = dataSnapshot.getValue(User.class);
                    if (!dataSnapshot.getKey().equals(cUser.getUid())) {
                        dataBaseUser.child(ReferenceUrl.CHILD_USERS).child(user.getIdUser()).child("checkListFriend")
                                .child(cUser.getUid()).child("idUser").setValue(cUser.getUid());

                        dataBaseUser.child(ReferenceUrl.CHILD_USERS).child(user.getIdUser()).child("checkListFriend")
                                .child(cUser.getUid()).child("status").setValue("NoFriend");
                    } else {
                        Log.d("NAmBV", "Ok");
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showErrorMessageToUser(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(errorMessage)
                .setTitle("Đăng kí không thành công!")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

