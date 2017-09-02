package com.studio.classlog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AccountSetup extends AppCompatActivity {

    private static final String TAG = "AccountSetup";

    private static final int GALLERY_REQUEST = 1;

    private Context mContext;

    private ImageView profilePicture;
    private EditText profileName;
    private Spinner deptSpinner;
    private Spinner classSpinner;
    private Button updateButton;

    private Uri mImageUri = null;

    private ProgressDialog mProgress;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        mContext = AccountSetup.this;

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorage = FirebaseStorage.getInstance().getReference().child("profile_picture");

        profilePicture = (ImageView) findViewById(R.id.profilePictureImgVw);
        profileName = (EditText) findViewById(R.id.profileNameTxtVw);

        deptSpinner = (Spinner) findViewById(R.id.spinner);
        classSpinner = (Spinner) findViewById(R.id.spinner2);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, )

        updateButton = (Button) findViewById(R.id.updateBtn);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateAccount();

            }
        });
    }

    private void updateAccount() {

        final String username = profileName.getText().toString();
        final String user_id = mAuth.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(username) && mImageUri != null){

            mProgress.setMessage("Updating account details.");
            mProgress.show();

            StorageReference filepath = mStorage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabase.child(user_id).child("username").setValue(username);
                    mDatabase.child(user_id).child("profilePicture").setValue(downloadUri);

                    mProgress.dismiss();

                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);

                }
            });

        } else {

            Toast.makeText(mContext, "Please enter all your details to complete your  profile.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            profilePicture.setImageURI(mImageUri);
        }

    }
}
