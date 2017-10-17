package com.riversidecorps.rebuy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.adapter.OfferAdapter;
import com.riversidecorps.rebuy.models.Message;
import com.riversidecorps.rebuy.models.Offer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class ViewOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView mRecyclerView;
    private OfferAdapter mAdapter;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdatabaseReference;

    private ProgressDialog mProgressDialog;
    private ArrayList<Offer> mOfferList = new ArrayList<>();

    private static final String DB_LISTING = "Listings";
    private static final String DB_USERS = "Users";
    private static final String DB_MESSAGES = "Messages";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String DB_OFFER = "Offers";

    @BindView(R.id.noOffersTV)
    TextView noOffersTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mdatabaseReference = mDatabase.getReference();
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
        }

        final String userId = mUser.getUid();

        //Setup loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading offers...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Attach a listener to read the data at our posts reference
        mdatabaseReference.child(DB_OFFER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mOfferList.removeAll(mOfferList);
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    //Set the fields required
                    Offer offer = messageSnapshot.getValue(Offer.class);
                    String mOfferID = (String) messageSnapshot.getKey();
                    Boolean isDeleted = offer.getOfferDeleted();
                    Boolean isCompleted = offer.getOfferCompleted();

                    //If the item is marked as deleted or completed skip to the next item
                    if (isDeleted || isCompleted) {
                        continue;
                    }
                    offer.setOfferID(mOfferID);

                    //Fix to prevent all users from seeing offers that are not their own
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Objects.equals(offer.getSellerId(), userId)){
                            mOfferList.add(offer);
                        }
                    } else {
                        mOfferList.add(offer);
                    }
                }
                progressDialog.dismiss();
                mAdapter.notifyDataSetChanged();

                if(noOffersTV.getVisibility() == View.VISIBLE && !mOfferList.isEmpty()) {
                    noOffersTV.setVisibility(View.GONE);
                }
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
            }
        };
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

    public void viewDetailedOffer(View v) {
        //Create alert dialog with the layout group_dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewOffersActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_view_offer, null);

        //Initialises elements
        Button acceptOfferBtn = (Button) mView.findViewById(R.id.accept_offer_btn);
        Button rejectOfferBtn = (Button) mView.findViewById(R.id.reject_offer_btn);
        TextView itemName = (TextView) mView.findViewById(R.id.item_name);
        final TextView itemBuyer = (TextView) mView.findViewById(R.id.item_buyer);
        final TextView originalPrice = (TextView) mView.findViewById(R.id.item_original_price);
        TextView offerPrice = (TextView) mView.findViewById(R.id.item_offer_price);
        TextView offerQuantity = (TextView) mView.findViewById(R.id.offer_quantity);
        TextView offerDescriptionPre = (TextView) mView.findViewById(R.id.description_pre);
        TextView offerDescription = (TextView) mView.findViewById(R.id.offer_description);
        TextView offerDate = (TextView) mView.findViewById(R.id.offer_date);

        TextView itemIDRV = (TextView) v.findViewById(R.id.offer_id);
        Offer selectedOffer = null;

        String currOffer = itemIDRV.getText().toString();
        for (Offer offer : mOfferList) {
            if (offer.getOfferID().equals(currOffer)) {
                selectedOffer = offer;
                itemName.setText(offer.getItemName());
                itemBuyer.setText(offer.getItemBuyer());
                originalPrice.setText(offer.getItemOriginalPrice());
                offerPrice.setText(offer.getOfferPrice());
                offerQuantity.setText(offer.getOfferQuantity().toString());
                String desCheck = offer.getOfferDescription();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(desCheck, "")) {
                        offerDescriptionPre.setText("");
                    }
                }
                offerDescription.setText(desCheck);
                offerDate.setText(offer.getOfferDate());
            }
        }

        //Allows us to find offerID of the selected offer
        final Offer finalSelectedOffer = selectedOffer;
        final String itemID = finalSelectedOffer.getItemID();
        final String mBuyerID = finalSelectedOffer.getItemBuyerID();
        final Integer mItemQuantity = finalSelectedOffer.getItemQuantity();

        //Get the User ID, Offered Quantity, Item Name and Item Buyer
        //Used in the Accept Offer button ~ Seb
        final String mUserId = mUser.getUid();
        final Integer mOfferedQuantity = Integer.parseInt(offerQuantity.getText().toString());
        final String mBuyerId = itemBuyer.getText().toString();
        final String mItemName = itemName.getText().toString();
        final String mOfferID = itemIDRV.getText().toString();

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        //Sets listener for the click of the accept offer button in alert dialog
        acceptOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////Close dialog box
                dialog.dismiss();
                //Show buying is in progress
                mProgressDialog.setMessage("Accepting Offer...");
                mProgressDialog.show();
                //Get current time
                String datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                //Set the user's message
                String userMessage = mUser.getDisplayName() + " has accepted your offer for " + mOfferedQuantity + " " + mItemName + "(s)";
                //Get a message id from Firebase Database
                final String messageID = mdatabaseReference.child(DB_USERS).child(mBuyerID).child(DB_MESSAGES).push().getKey();
                //Create a new message
                Message message = new Message(userMessage, mUser.getDisplayName(), datetime, mItemName, messageID, mUserId);
                //Save the message
                mdatabaseReference.child(DB_USERS).child(mBuyerId).child(DB_MESSAGES).child(messageID).setValue(message).addOnSuccessListener(ViewOffersActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Thank you for your purchase", Toast.LENGTH_LONG).show();
                        //Close the progress dialog
                        mProgressDialog.dismiss();
                        finish();
                    }
                });

                //If the amount bought sets listing's quantity to 0
                if (mItemQuantity <= mOfferedQuantity) {
                    //Set the listing's quantity to 0 and set it to complete in both location (under listings and user's listings)
                    mdatabaseReference.child(DB_LISTING).child(itemID).child("itemCompleted").setValue(true);
                    mdatabaseReference.child(DB_LISTING).child(itemID).child("itemQuantity").setValue(0);
                    mdatabaseReference.child(DB_OFFER).child(mOfferID).child("itemQuantity").setValue(0);
                    mdatabaseReference.child(DB_OFFER).child(mOfferID).child("offerCompleted").setValue(true);
                } else {
                    //Sets listing's quantity to new quantity
                    mdatabaseReference.child(DB_LISTING).child(itemID).child("itemQuantity").setValue(mItemQuantity - mOfferedQuantity);
                    mdatabaseReference.child(DB_OFFER).child(mOfferID).child("itemQuantity").setValue(mItemQuantity - mOfferedQuantity);
                    mdatabaseReference.child(DB_OFFER).child(mOfferID).child("offerCompleted").setValue(true);
                }
            }
        });

        //Sets listener for the click of the reject offer button in alert dialog
        rejectOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Close dialog box
                dialog.dismiss();
                //Show buying is in progress
                mProgressDialog.setMessage("Accepting Offer...");
                mProgressDialog.show();
                //Get current time
                String datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                //Set the user's message
                String userMessage = mUser.getDisplayName() + " has declined your offer for " + mOfferedQuantity + " " + mItemName + "(s)";
                //Get a message id from Firebase Database
                final String messageID = mdatabaseReference.child(DB_USERS).child(mBuyerID).child(DB_MESSAGES).push().getKey();
                //Create a new message
                Message message = new Message(userMessage, mUser.getDisplayName(), datetime, mItemName, messageID, mUserId);
                //Save the message
                mdatabaseReference.child(DB_USERS).child(mBuyerId).child(DB_MESSAGES).child(messageID).setValue(message).addOnSuccessListener(ViewOffersActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "Thank you for your purchase", Toast.LENGTH_LONG).show();
                        //Close the progress dialog
                        mProgressDialog.dismiss();
                        finish();
                    }
                });
                //Set offer as completed so it will be removed later
                mdatabaseReference.child(DB_OFFER).child(mOfferID).child("offerCompleted").setValue(true);

            }
        });
    }

}