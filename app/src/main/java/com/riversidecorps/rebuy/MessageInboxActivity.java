package com.riversidecorps.rebuy;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.adapter.messageAdapter;
import com.riversidecorps.rebuy.models.Message;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;
/**
 * Receiving messages when other user send message to the login user,
 * also when other user click 'buy it now' the login user will receive
 * a message, login user can delete and reply the message.
 * @author Yijun Gai
 * @version 1.0
 * @since 23.08.2017
 */
public class MessageInboxActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    private static final String DB_USERS = "Users";
    private static final String DB_MESSAGES = "Messages";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private ProgressDialog mProgressDialog;
    private int mMessageCount;
    private ArrayList<Message> mMessageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set up navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // get firebaseauth instance and database instance
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mEmptyView = findViewById(R.id.empty_view);
        // set up navigation view
        final View navView = navigationView.getHeaderView(0);
        final TextView usernameNavTV = navView.findViewById(R.id.userNavIDTV);
        TextView emailNavTV = navView.findViewById(R.id.userNavEmailTV);
        ImageView userNavAvatarIV = navView.findViewById(R.id.userNavAvatarIV);
        usernameNavTV.setText(mUser.getDisplayName());
        //Setup Loading dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        // set up drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Set up nav menu
        emailNavTV.setText(mUser.getEmail());
        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(userNavAvatarIV);
        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, AUTH_IN + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, AUTH_OUT);
                }
            }
        };
        init();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    /**
     *  Set message list to message adapter, send notification when receiving new message
     *
     * @param
     */
    private void init() {
        mRecyclerView = findViewById(R.id.message_recycler_view);
        final messageAdapter messageAdapter = new messageAdapter(mMessageList, this);
        String userId = mUser.getUid();
        mRecyclerView.setAdapter(messageAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                messageAdapter.setScrollingMenu(null);
            }
        });
        // get origin message count from firebase
        mDatabaseReference.child(DB_USERS).child(userId).child(DB_MESSAGES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mMessageCount = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(getString(R.string.read_failed) + databaseError.getCode());
            }
        });
        // message count minus one after one message has been removed
        mDatabaseReference.child(DB_USERS).child(userId).child(DB_MESSAGES).addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {}

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mMessageCount--;
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}

            public void onCancelled() {}
        });
        // Set empty view for message box, when got one new message send notification and message count plus one
        mDatabaseReference.child(DB_USERS).child(userId).child(DB_MESSAGES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mMessageList.removeAll(mMessageList);
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    mMessageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                if (messageAdapter.getItemCount() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                if (messageAdapter.getItemCount() > mMessageCount) {
                    NotificationManager mNotifyMgr =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            getApplicationContext(), 0, new Intent(getApplicationContext(), MessageInboxActivity.class), 0);

                    NotificationCompat.Builder mNotifyBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setContentTitle(getString(R.string.new_message))
                                    .setContentText(getString(R.string.received_new_message))
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setFullScreenIntent(contentIntent, false);
                    mNotifyMgr.notify(id, mNotifyBuilder.build());
                    mMessageCount = messageAdapter.getItemCount();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(getString(R.string.read_failed) + databaseError.getCode());
            }
        });
    }

    //On start method
    @Override
    public void onStart() {
        super.onStart();
        //Sets a listener to catch when the user is signing in.
        mAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Creates the options menu on the action bar.
     *
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.message_inbox, menu);
        return true;
    }

    /**
     * Sets a listener that triggers when an option from the taskbar menu is selected.
     *
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch (item.getItemId()) {
            //If item is logout
            case R.id.action_logout:
                //Sign out of the authenticator and return to login activity.
                mAuth.signOut();
                this.startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                this.startActivity(new Intent(this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_account) {
            startActivity(new Intent(this, MyAccountActivity.class));
        } else if (id == R.id.nav_message_inbox) {
            //Do Nothing
        } else if (id == R.id.nav_view_offers) {
            startActivity(new Intent(this, ViewOffersActivity.class));
        } else if (id == R.id.nav_create_listing) {
            startActivity(new Intent(this, CreateListingActivity.class));
        } else if (id == R.id.nav_view_listings) {
            startActivity(new Intent(this, ViewListingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
