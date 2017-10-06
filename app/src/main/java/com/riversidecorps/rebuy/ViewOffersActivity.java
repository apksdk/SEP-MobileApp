package com.riversidecorps.rebuy;

import android.content.Intent;
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
import android.support.v7.app.ActionBarDrawerToggle;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static com.riversidecorps.rebuy.R.id.itemImagePreviewIV;

public class ViewOffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static final String DB_OFFER = "Offers";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private String itemName,itemPrice,mitemQuantity, uid;
    private TextView itemNameTV;
    private TextView itemOriginalPriceTV;
    private TextView offerAuthorTV;
    private Button makeOfferBtn;
    private Button cancelBtn;
    private EditText itemOfferPriceET;
    private String itemId;
    private ImageView mimageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();


        uid = myFirebaseUser.getEmail();
        itemName = getIntent().getStringExtra("itemName");
        itemPrice = getIntent().getStringExtra("itemPrice");
        mitemQuantity = getIntent().getStringExtra("itemQuantity");
        itemId = getIntent().getStringExtra("itemId");

        makeOfferBtn = (Button) findViewById(R.id.makeOfferBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        itemOfferPriceET = (EditText) findViewById(R.id.itemOfferPriceET);
        makeOfferBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        itemNameTV = (TextView) findViewById(R.id.itemNameTV);
        itemNameTV.setText(itemName);

        itemOriginalPriceTV = (TextView) findViewById(R.id.itemOriginalPriceTV);
        itemOriginalPriceTV.setText(itemPrice);

        offerAuthorTV = (TextView) findViewById(R.id.offerAuthorTV);
        offerAuthorTV.setText(uid);

        mimageView=findViewById(itemImagePreviewIV);
        String imagePath = "itemImageListings/" + itemId + ".png";
        //Upload image(s)
        Log.i("imagePath",imagePath);

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
            // Handle the camera action
        } else if (id == R.id.nav_message_inbox) {

        } else if (id == R.id.nav_create_listing) {

        } else if (id == R.id.nav_search_listings) {

        } else if (id == R.id.nav_view_listings) {

        }else if (id == R.id.nav_view_offers) {

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
        myFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    //On stop method
    @Override
    public void onStop() {
        super.onStop();
        //Sets listener to catch when the user is signing out.
        if (mAuthListener != null) {

            myFirebaseAuth.removeAuthStateListener(mAuthListener);
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
                myFirebaseAuth.signOut();
                this.startActivity(new Intent(this, LoginActivity.class));
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                this.startActivity(new Intent(this, ResetPasswordActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewDetailedOffer(View v){
        //Create alert dialog with the layout group_dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewOffersActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_view_offer, null);

        //Initialises elements
        Button acceptOfferBtn = (Button) mView.findViewById(R.id.accept_offer_btn);

        alertDialog.setView(mView);
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        //Sets listener for the click of the create group button in alert dialog
        acceptOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Close dialog box
                dialog.dismiss();
            }
        });
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
            // Handle the camera action
        } else if (id == R.id.nav_message_inbox) {

        } else if (id == R.id.nav_offers) {

        } else if (id == R.id.nav_search_listings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == makeOfferBtn){
            makeOffer();
            ViewOffersActivity.this.startActivity(new Intent(ViewOffersActivity.this, MyAccountActivity.class));
        } else if(view == cancelBtn){
            ViewOffersActivity.this.startActivity(new Intent(ViewOffersActivity.this, MyAccountActivity.class));
        }
    }

    private void makeOffer() {
        String itemName =  itemNameTV.getText().toString().trim();
        /*I could catch any value of the itemQuantity in Single view listing page*/
        //Integer itemQuantity = Integer.parseInt(getIntent().getStringExtra("itemQuantity").trim());
        double orginalPrice = Double.parseDouble(itemOriginalPriceTV.getText().toString().trim());
        double offerPrice = Double.parseDouble(itemOfferPriceET.getText().toString().trim());
        String itemDes = getIntent().getStringExtra("itemDes").toString().trim();
        String buyerid = myFirebaseUser.getEmail();
        FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();

        Date date = new Date();
        Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String stringDate = dt.format(newDate);
        Offer newOffer = new Offer(buyerid,itemName,orginalPrice,offerPrice,stringDate,itemDes);
        databaseReference.child(DB_OFFER).push().setValue(newOffer);
        Toast.makeText(this,"Please wait for making offer ...",Toast.LENGTH_LONG).show();

    }
}
