package com.riversidecorps.rebuy;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.adapter.ItemAdapter;
import com.riversidecorps.rebuy.models.Listing;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ViewListingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private ArrayList<Listing> mItemList = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdatabaseReference;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private String userID;
    private String userName;
    private String mfilter;
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String LISTINGS = "Listings";

    //TextView loginName = (TextView) findViewById(R.id.login_name);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TTT", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_listings);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
        }

        mRecyclerView = findViewById(R.id.listing_recycler_view);
        mAdapter = new ItemAdapter(this, mItemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        userID = mUser.getUid();
        userName = mUser.getDisplayName();

        // Attach listener to display welcome bar personalised for user's name
        DatabaseReference userRef = mDatabase.getReference().child("users").child(userID).child("username");

        // Attach a listener to read the data at our posts reference
        mDatabase.getReference().child(LISTINGS).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mItemList.removeAll(mItemList);
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Boolean isDeleted = (Boolean) messageSnapshot.child("itemDeleted").getValue();
                    //If the item is marked as deleted skip to the next item
                    if (isDeleted) {
                        continue;
                    }
                    Listing listing = messageSnapshot.getValue(Listing.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!Objects.equals(listing.getItemSellerId(), userID)){
                            mItemList.add(listing);
                        }
                    } else {
                        mItemList.add(listing);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View navView = navigationView.getHeaderView(0);
        final TextView usernameNavTV = navView.findViewById(R.id.userNavIDTV);
        TextView emailNavTV = navView.findViewById(R.id.userNavEmailTV);
        ImageView userNavAvatarIV = navView.findViewById(R.id.userNavAvatarIV);
        usernameNavTV.setText(mUser.getDisplayName());

        //Set up nav menu
        emailNavTV.setText(mUser.getEmail());
        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(userNavAvatarIV);

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
                // ...
            }
        };


        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getResultsFromApi();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("TTT", "onRestart");
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
            startActivity(new Intent(this, MessageInboxActivity.class));
        } else if (id == R.id.nav_view_offers) {
            startActivity(new Intent(this, ViewOffersActivity.class));
        } else if (id == R.id.nav_search_listings) {
            startActivity(new Intent(this, SearchListingsActivity.class));
        } else if (id == R.id.nav_create_listing) {
            startActivity(new Intent(this, CreateListingActivity.class));
        } else if (id == R.id.nav_view_listings) {
            //Do Nothing
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //On start method
    @Override
    public void onStart() {
        super.onStart();
        Log.i("TTT", "onStart");
        //Sets a listener to catch when the user is signing in.
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        Log.i("TTT", "onPause");
        super.onPause();
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        Log.i("TTT", "onStop");
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Log.i("TTT", "ORIENTATION_LANDSCAPE");

            // land do nothing is ok
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("TTT", "ORIENTATION_LANDSCAPE");

            // port do nothing is ok
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TTT", "onResume");
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
        getMenuInflater().inflate(R.menu.search_listings, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        changeSearchViewTextColor(searchView);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        Toast.makeText(getApplicationContext(), "onMenuItemActionCollapse", Toast.LENGTH_SHORT).show();
                      /*  datasource.open();
                        mReminderList.clear();
                        mReminderList.addAll(datasource.findAll());
                        mAdapter.notifyDataSetChanged();
                        datasource.close();*/
                        return true;
                    }
                });
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


    public void searchFromFirebase(String keywords){

        final String keyword=keywords;
        DatabaseReference userRef = mDatabase.getReference().child("Listings").child(userID).child("username");
        Query query = mdatabaseReference.child("Listings").orderByChild("itemName");
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mItemList.removeAll(mItemList);
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Boolean isDeleted = (Boolean) messageSnapshot.child("itemDeleted").getValue();
                    //If the item is marked as deleted skip to the next item
                    if (isDeleted) {
                        continue;
                    }
                    Listing listing = messageSnapshot.getValue(Listing.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (!Objects.equals(listing.getItemSellerId(), userID)){

                            if(listing.getItemName().contains(keyword)){
                                mItemList.add(listing);
                            }

                        }
                    } else {
                        mItemList.add(listing);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



        // Attach a listener to read the data at our posts reference
      //  mDatabase.getReference().child(LISTINGS).addValueEventListener(new ValueEventListener() {

    //    });
    }


    private void getResultsFromApi() {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mfilter = newText;
        searchFromFirebase(newText);
        //  datasource.open();
        //mItemList.clear();
        //      mItemList.addAll(datasource.filterReminder(newText));

        mAdapter.notifyDataSetChanged();
        //       datasource.close();
        return false;
    }

    /**
     *  Change the text colour in searchview
     * @param view
     */
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

}
