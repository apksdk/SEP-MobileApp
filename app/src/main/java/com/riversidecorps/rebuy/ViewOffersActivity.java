package com.riversidecorps.rebuy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.adapter.OfferAdapter;
import com.riversidecorps.rebuy.models.Offer;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.NumberFormat;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class ViewOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private OfferAdapter mAdapter;
    private ArrayList<Offer> mOfferList = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdatabaseReference;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private SwipeRefreshLayout swipeContainer;
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String DB_OFFER = "Offers";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
        }

        String userId = mUser.getUid();

        // Attach a listener to read the data at our posts reference
        mdatabaseReference.child(DB_OFFER).addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mOfferList.removeAll(mOfferList);
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {

                    String mOfferID = (String) messageSnapshot.getKey();
                    String buyer = (String) messageSnapshot.child("itemBuyer").getValue();
                    String itemName = (String) messageSnapshot.child("itemName").getValue();
                    String originalPrice = (String) messageSnapshot.child("itemOriginalPrice").getValue();
                    String offerPrice = (String) messageSnapshot.child("offerPrice").getValue();
                    String offerDescription = (String) messageSnapshot.child("offerDescription").getValue();
                    String quantity = (String) messageSnapshot.child("offerQuantity").getValue();
                    //String itemId =  messageSnapshot.getKey().toString();
                    String offerDate = (String) messageSnapshot.child("offerDate").getValue();
                    Offer newOffer = new Offer(buyer,itemName , quantity,
                            originalPrice, offerPrice, offerDate, offerDescription);
                    newOffer.setOfferID(mOfferID);
                    mOfferList.add(newOffer);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        mRecyclerView = findViewById(R.id.offer_recycler_view);
        mAdapter = new OfferAdapter(this, mOfferList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

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


//        swipeContainer = findViewById(R.id.swipeContainer);
//        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeContainer.setRefreshing(true);
//                // Your code to refresh the list here.
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//                getResultsFromApi();
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
            //Do Nothing
        } else if (id == R.id.nav_search_listings) {
            startActivity(new Intent(this, SearchListingsActivity.class));
        } else if (id == R.id.nav_create_listing) {
            startActivity(new Intent(this, CreateListingActivity.class));
        } else if (id == R.id.nav_view_listings) {
            startActivity(new Intent(this, ViewListingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
     * @param item Which item on the menu was selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Finds which item was selected
        switch(item.getItemId()){
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
    private void getResultsFromApi() {
    }

    public void viewDetailedOffer(View v){
        //Create alert dialog with the layout group_dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewOffersActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_view_offer, null);

        //Initialises elements
        Button acceptOfferBtn = (Button) mView.findViewById(R.id.accept_offer_btn);
        Button rejectOfferBtn = (Button) mView.findViewById(R.id.reject_offer_btn);
        TextView itemName = (TextView) mView.findViewById(R.id.item_name);
        TextView itemBuyer = (TextView) mView.findViewById(R.id.item_buyer);
        TextView originalPrice = (TextView) mView.findViewById(R.id.item_original_price);
        TextView offerPrice = (TextView) mView.findViewById(R.id.item_offer_price);
        TextView offerQuantity = (TextView) mView.findViewById(R.id.offer_quantity);
        TextView offerDescription = (TextView) mView.findViewById(R.id.offer_description);
        TextView offerDate = (TextView) mView.findViewById(R.id.offer_date);

        TextView itemIDRV = (TextView) v.findViewById(R.id.offer_id);

        String currOffer = itemIDRV.getText().toString();
        for (Offer offer : mOfferList) {
            if (offer.getOfferID().equals(currOffer)){
                itemName.setText(offer.getItemName());
                itemBuyer.setText(offer.getItemBuyer());
                originalPrice.setText(offer.getItemOriginalPrice());
                offerPrice.setText(offer.getOfferPrice());
                offerQuantity.setText(offer.getOfferQuantity());
                offerDescription.setText(offer.getOfferDescription());
                offerDate.setText(offer.getOfferDate());
            }
        }

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        //Sets listener for the click of the accept offer button in alert dialog
        acceptOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When a seller accepts the buyer's offer, do the following
                //int currentQuantity = mdatabaseReference.child("")
            }
        });

        //Sets listener for the click of the reject offer button in alert dialog
        rejectOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close dialog box
                dialog.dismiss();
            }
        });
    }

}