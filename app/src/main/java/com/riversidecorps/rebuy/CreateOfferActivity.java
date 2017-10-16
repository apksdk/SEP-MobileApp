package com.riversidecorps.rebuy;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.riversidecorps.rebuy.models.Offer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.riversidecorps.rebuy.R.id.descriptionET;
import static com.riversidecorps.rebuy.R.id.itemImagePreviewIV;

public class CreateOfferActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static final String DB_OFFER = "Offers";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private String mItemName;
    private String mItemPrice;
    private Integer mItemQuantity;
    private String userEmail;
    private TextView itemNameTV;
    private TextView itemOriginalPriceTV;
    private TextView itemQuanitityTV;
    private TextView offerAuthorTV;
    private Button makeOfferBtn;
    private Button cancelBtn;
    private EditText offerPriceET;
    private EditText offerQuantityET;
    private EditText offerDescriptionET;
    private String mItemId;
    private ImageView mimageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        userEmail = mUser.getEmail();
        mItemName = getIntent().getStringExtra("itemName");
        mItemPrice = getIntent().getStringExtra("itemPrice");
        mItemQuantity = getIntent().getIntExtra("itemQuantity", 0);
        mItemId = getIntent().getStringExtra("itemId");

        makeOfferBtn = (Button) findViewById(R.id.makeOfferBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        offerPriceET = (EditText) findViewById(R.id.itemOfferPriceET);
        offerQuantityET = (EditText) findViewById(R.id.offerQuantityET);
        offerDescriptionET = (EditText) findViewById(R.id.descriptionET);
        makeOfferBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        itemNameTV = (TextView) findViewById(R.id.itemNameTV);
        itemNameTV.setText(mItemName);

        itemOriginalPriceTV = (TextView) findViewById(R.id.itemOriginalPriceTV);
        itemOriginalPriceTV.setText(mItemPrice);

        itemQuanitityTV = (TextView) findViewById(R.id.itemQuantityTV);
        itemQuanitityTV.setText(mItemQuantity.toString());

        offerAuthorTV = (TextView) findViewById(R.id.offerAuthorTV);
        offerAuthorTV.setText(userEmail);

        mimageView = findViewById(itemImagePreviewIV);
        String imagePath = "itemImageListings/" + mItemId + ".png";
        //Upload image(s)
        Log.i("imagePath", imagePath);

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

        StorageReference itemImageRef = mStorage.getReference(imagePath);
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(itemImageRef)
                .into(mimageView);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
                // ...
            }
        };
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
        getMenuInflater().inflate(R.menu.create_offer, menu);
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
            startActivity(new Intent(this, MessageInboxActivity.class));
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

    @Override
    public void onClick(View view) {
        if (view == makeOfferBtn) {
            makeOffer();
        } else if (view == cancelBtn) {
            startActivity(new Intent(CreateOfferActivity.this, MyAccountActivity.class));
        }
    }

    @OnClick(R.id.makeOfferBtn)
    public void makeOffer() {
        if (offerPriceET.getText().toString().isEmpty()) {
            //Display error message
            Toast.makeText(CreateOfferActivity.this, "Please enter an offer price", Toast.LENGTH_SHORT).show();
        } else if (offerQuantityET.getText().toString().isEmpty()) {
            //Display error message
            Toast.makeText(CreateOfferActivity.this, "Please enter the amount of items you would like to make an offer for", Toast.LENGTH_LONG).show();
        } else {
            if (offerDescriptionET.getText().toString().isEmpty()) {
                offerDescriptionET.setText("");
            }
            Integer offerQuantity = Integer.parseInt(offerQuantityET.getText().toString());
            if (offerQuantity > mItemQuantity) {
                new AlertDialog.Builder(CreateOfferActivity.this)
                        .setTitle("Not Enough Items")
                        .setMessage("You have requested to buy more items than the seller is selling.")
                        .setIcon(R.drawable.ic_dialog_warning)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            } else {
                String itemName = itemNameTV.getText().toString().trim();
                String originalPrice = itemOriginalPriceTV.getText().toString();
                NumberFormat formattedP1 = NumberFormat.getCurrencyInstance(Locale.US);
                String offerPrice = formattedP1.format(Double.parseDouble(offerPriceET.getText().toString()));
                String itemDes = offerDescriptionET.getText().toString();
                String buyerName = mUser.getDisplayName();
                String buyerId = mUser.getUid();
                //Variable rename to keep database consistent
                String itemId = mItemId;

                FirebaseUser myFirebaseUser = mAuth.getCurrentUser();
                Date date = new Date();
                Date newDate = new Date(date.getTime() + 604800000L * 2 + 24 * 60 * 60);
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                String stringDate = dt.format(newDate);
                String sellerId = getIntent().getStringExtra("itemSellerID");

                Offer newOffer = new Offer(buyerId, buyerName, itemName, offerQuantity, originalPrice, offerPrice, stringDate, itemDes, sellerId, itemId);
                databaseReference.child(DB_OFFER).push().setValue(newOffer);
                Toast.makeText(this, "Please wait for making offer ...", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CreateOfferActivity.this, MyAccountActivity.class));
            }
        }

    }
}

