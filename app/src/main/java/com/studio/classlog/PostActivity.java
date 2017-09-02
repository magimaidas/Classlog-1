package com.studio.classlog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private Context mContext;

    private static final int GALLERY_REQUEST = 1;

    private ImageButton mImage;
    private EditText postTitle, postDescription;
    private Button postButton;

    private Uri mImageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseUser;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Log.d(TAG, "evetLog: onCreate: Starting Post Activity.");

        mContext = PostActivity.this;

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

        mImage = (ImageButton) findViewById(R.id.postImageBtn);

        postTitle = (EditText) findViewById(R.id.postTitleEditText);
        postDescription = (EditText) findViewById(R.id.postDescEditText);

        postButton = (Button) findViewById(R.id.postButton);

        mProgress = new ProgressDialog(this);

        //Select an Image to Upload.
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "evetLog: onClick: Opening Gallery to select a Image.");
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        //Call the method postData to submit post.
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "evetLog: onClick: Checking fields to submit post.");
                postData();
            }
        });

    }

    private void postData() {

        final String post_title = postTitle.getText().toString();
        final String post_Desc = postDescription.getText().toString();

        if (!TextUtils.isEmpty(post_title) && !TextUtils.isEmpty(post_Desc) && mImageUri != null){

            mProgress.setMessage("Posting...");
            mProgress.show();

            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl  = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(post_title);
                            newPost.child("description").setValue(post_Desc);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mUser.getUid());

                            newPost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Log.d(TAG, "evetLog: onSuccess: Navigating user to the Home Activity.");
                                        finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Log.d(TAG, "evetLog: onSuccess: Post successfully submitted to the database.");
                    mProgress.dismiss();

                }
            });
        } else {
            Log.d(TAG, "evetLog: postData: User hasn't completed all the fields.");
            Toast.makeText(mContext, "Please fill all the information to submit the post.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            mImage.setImageURI(mImageUri);
        }
    }
}
