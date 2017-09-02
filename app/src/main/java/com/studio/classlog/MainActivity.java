package com.studio.classlog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    public FloatingActionButton fab;

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "eventLog: onCreate: Starting Main Activity.");
        mContext = MainActivity.this;

        mAuth = FirebaseAuth.getInstance();
        //comment by das to check GitHub commit
        //Check if user is logged in.
        //if not navigate to the login activity.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){

                    Log.d(TAG, "eventLog: onAuthStateChanged: Navigating user to the login activity.");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blogs");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mBlogList = (RecyclerView) findViewById(R.id.blogList);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        fab = (FloatingActionButton) findViewById(R.id.fabBtn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "eventLog: onClick: Navigating user to the Post Activity.");
                Intent intent = new Intent(mContext, PostActivity.class);
                startActivity(intent);
            }
        });

        checkUserExist();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        //Initialzie Recycler Adapter to display the post's from the database.
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(

                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());

            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.cardViewImage);
            Picasso.with(ctx).load(image).into(post_image);
        }

        public void setTitle(String title){

            TextView postTitle = (TextView) mView.findViewById(R.id.cardViewPostTitle);
            postTitle.setText(title);

        }

        public void setDesc(String desc){

            TextView postDesc = (TextView) mView.findViewById(R.id.cardViewPostDesc);
            postDesc.setText(desc);
        }

        public void setUsername(String username){

            TextView postUsername = (TextView) mView.findViewById(R.id.cardViewPostAuthor);
            postUsername.setText(username);

        }
    }


    //Menu options.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.signOut){
            mAuth.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Log.d(TAG, "eventLog: onDataChange: Navigating user to update account.");
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
}
