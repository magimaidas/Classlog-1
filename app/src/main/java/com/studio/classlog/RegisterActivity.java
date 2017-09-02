package com.studio.classlog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private EditText mUsername, mEmail, mPassword;
    private Button mSignUp;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsername = (EditText) findViewById(R.id.usernameEditText);
        mEmail = (EditText) findViewById(R.id.emailEditText);
        mPassword = (EditText) findViewById(R.id.passwordEditTxt);

        mSignUp = (Button) findViewById(R.id.signUpBtn);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAcc();
            }
        });

    }

    private void signUpAcc() {
        final String username = mUsername.getText().toString();
        final String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgress.setMessage("Signing up new user.");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user = mDatabase.child(user_id);
                        current_user.child("username").setValue(username);
                        current_user.child("email").setValue(email);

                        mProgress.dismiss();

                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);

                    }
                }
            });
        } else {

            Toast.makeText(mContext, "Please enter all the fields to complete sign-up process.", Toast.LENGTH_LONG).show();
        }
    }
}
