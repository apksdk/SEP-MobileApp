package com.riversidecorps.rebuy;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.adapter.ItemAdapter;
import com.riversidecorps.rebuy.models.Listing;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ViewListingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private ItemAdapter mAdapter;
    private ArrayList<Listing> mItemList = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdatabaseReference;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String LISTINGS = "Listings";

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

        String userId = mUser.getUid();

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
                    mItemList.add(listing);

//                        String name = listing.getItemName();
//                        String description = listing.getItemDescription();
//                        String price = listing.getItemPrice();
//                        Long quantity = Long.valueOf(listing.getItemQuantity());
//                        String sellerName = listing.getItemSeller();
//                        String itemID = messageSnapshot.getKey();
//                        String itemDate = listing.getCreatedDate();
//                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//                        String itemSellerId = listing.getItemSellerId();

//                        try {
//                            Date date = format.parse(itemDate);
//                            Listing newListing = new Listing(sellerName, name, Integer.parseInt(quantity.toString()), price, description, itemDate);
//                            newListing.setItemId(itemID);
//                            newListing.setItemSellerId(itemSellerId);
//                            mItemList.add(newListing);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
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
                // Your code to refresh the list here.
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
        getMenuInflater().inflate(R.menu.my_account, menu);
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
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                this.startActivity(new Intent(this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getResultsFromApi() {
    }

}
