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
import static com.riversidecorps.rebuy.R.id.itemImagePreviewIV;
/**
 * Allow the user to make offer for the item. All the relevant data will be stored in the firebase
 * ViewOffer Activity can fetch data from firebase directly.
 * @author Lei Liu
 * @version 1.0
 * @since 03.09.2017
 */
public class CreateOfferActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //Constants
    private static final String DB_OFFER = "Offers";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private static final String ITEM_SELLER_ID = "itemSellerID";
    private static final String ITEM_NAME = "itemName";
    private static final String ITEM_PRICE = "itemPrice";
    private static final String ITEM_QUANTITY = "itemQuantity";
    private static final String ITEM_ID = "itemId";

    // view variables
    private String mItemName;
    private String mItemPrice;
    private Integer mItemQuantity;
    private String mUserEmail;
    private TextView mItemNameTV;
    private TextView mItemOriginalPriceTV;
    private TextView mItemQuanitityTV;
    private TextView mOfferAuthorTV;
    private Button makeOfferBtn;
    private Button cancelBtn;
    private EditText offerPriceET;
    private EditText offerQuantityET;
    private EditText offerDescriptionET;
    private String mItemId;
    private ImageView mImageView;

    @Override
    /**
     * OnClick method for when either of the listeners are triggered
     * @param v The view of the button clicked
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        // firebase get current user
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // send the content to variables in order to another activity can fetch it.
        mUserEmail = mUser.getEmail();
        mItemName = getIntent().getStringExtra(ITEM_NAME);
        mItemPrice = getIntent().getStringExtra(ITEM_PRICE);
        mItemQuantity = getIntent().getIntExtra(ITEM_QUANTITY, 0);
        mItemId = getIntent().getStringExtra(ITEM_ID);

        //Initialises the layout elements
        makeOfferBtn = (Button) findViewById(R.id.makeOfferBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        offerPriceET = (EditText) findViewById(R.id.itemOfferPriceET);
        offerQuantityET = (EditText) findViewById(R.id.offerQuantityET);
        offerDescriptionET = (EditText) findViewById(R.id.descriptionET);
        makeOfferBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mItemNameTV = (TextView) findViewById(R.id.itemNameTV);
        mItemNameTV.setText(mItemName);

        mItemOriginalPriceTV = (TextView) findViewById(R.id.itemOriginalPriceTV);
        mItemOriginalPriceTV.setText(mItemPrice);

        mItemQuanitityTV = (TextView) findViewById(R.id.itemQuantityTV);
        mItemQuanitityTV.setText(mItemQuantity.toString());

        mOfferAuthorTV = (TextView) findViewById(R.id.offerAuthorTV);
        mOfferAuthorTV.setText(mUserEmail);

        // set image path
        mImageView = findViewById(itemImagePreviewIV);
        String imagePath = "itemImageListings/" + mItemId + ".png";
        //Upload image(s)

        // set up nav view
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
                .into(mImageView);

        // set up drawer layout
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
    /**
     * Make offer or cancel
     * @param view Which button was selected.
     */
    @Override
    public void onClick(View view) {
        if (view == makeOfferBtn) {
            makeOffer();
        } else if (view == cancelBtn) {
            startActivity(new Intent(CreateOfferActivity.this, MyAccountActivity.class));
        }
    }
    /**
     * When make offer clicked checkout empty or not, and check whether quantity is enough or not
     * @param
     */
    @OnClick(R.id.makeOfferBtn)
    public void makeOffer() {
        if (offerPriceET.getText().toString().isEmpty()) {
            //Display error message
            Toast.makeText(CreateOfferActivity.this, R.string.enter_offfer_price, Toast.LENGTH_SHORT).show();
        } else if (offerQuantityET.getText().toString().isEmpty()) {
            //Display error message
            Toast.makeText(CreateOfferActivity.this, R.string.enter_items_amount, Toast.LENGTH_LONG).show();
        } else {
            if (offerDescriptionET.getText().toString().isEmpty()) {
                offerDescriptionET.setText("");
            }
            Integer offerQuantity = Integer.parseInt(offerQuantityET.getText().toString());
            if (offerQuantity > mItemQuantity) {
                new AlertDialog.Builder(CreateOfferActivity.this)
                        .setTitle(R.string.not_enough_items)
                        .setMessage(R.string.request_more_than_selling)
                        .setIcon(R.drawable.ic_dialog_warning)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            } else {
                String itemName = mItemNameTV.getText().toString().trim();
                String originalPrice = mItemOriginalPriceTV.getText().toString();
                NumberFormat formattedP1 = NumberFormat.getCurrencyInstance(Locale.US);
                String offerPrice = formattedP1.format(Double.parseDouble(offerPriceET.getText().toString()));
                String itemDes = offerDescriptionET.getText().toString();
                String buyerName = mUser.getDisplayName();
                String buyerId = mUser.getUid();
                //Variable rename to keep database consistent
                String itemId = mItemId;
                Integer itemQuantity = mItemQuantity;

                Date date = new Date();
                Date newDate = new Date(date.getTime() + 604800000L * 2 + 24 * 60 * 60);
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
                String stringDate = dt.format(newDate);
                String sellerId = getIntent().getStringExtra(ITEM_SELLER_ID);

                Offer newOffer = new Offer(buyerId, buyerName, itemName, offerQuantity, itemQuantity, originalPrice, offerPrice, stringDate, itemDes, sellerId, itemId);
                mDatabaseReference.child(DB_OFFER).push().setValue(newOffer);
                Toast.makeText(this, R.string.wait_for_offer, Toast.LENGTH_LONG).show();
                startActivity(new Intent(CreateOfferActivity.this, MyAccountActivity.class));
            }
        }

    }
}

