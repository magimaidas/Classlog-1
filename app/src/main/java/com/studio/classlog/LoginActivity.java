package com.studio.classlog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Context mContext;
    private ProgressDialog mProgressDialog;

    private EditText mLoginEmail, mLoginPassword;
    private Button mLoginButton, mSignUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "eventLog: onCreate: Login Activity started.");

        mContext = LoginActivity.this;
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginEmail = (EditText) findViewById(R.id.loginEmailEditTxt);
        mLoginPassword = (EditText) findViewById(R.id.loginPasswordEditTxt);

        mLoginButton = (Button) findViewById(R.id.loginBtn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login();
            }
        });

        mSignUpButton = (Button) findViewById(R.id.lgnSignUpBtn);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "eventLog: onClick: Navigating user to the Register Activity.");

                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login() {
        String email = mLoginEmail.getText().toString();
        String password = mLoginPassword.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Logging in...");
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        mProgressDialog.dismiss();
                        checkUserExist();

                    }else {

                        mProgressDialog.dismiss();

                        Toast.makeText(mContext, "Failed to Login.", Toast.LENGTH_LONG).show();
                    }
                }
            });


        }else {
            Toast.makeText(mContext, "Please enter the email and password to login.", Toast.LENGTH_LONG).show();
        }
    }

    private void checkUserExist() {


            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {

                        Log.d(TAG, "eventLog: onDataChange: Navigating user to the Main Activity.");
                        Toast.makeText(mContext, "Welcome back to Classlog.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);

                    } else {

                        Toast.makeText(mContext, "Please complete your account setup to login.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(mContext, AccountSetup.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

}
