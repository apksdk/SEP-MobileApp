package com.riversidecorps.rebuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.riversidecorps.rebuy.models.Offer;

import java.text.SimpleDateFormat;
import java.util.Date;


import org.w3c.dom.Text;

import static android.content.ContentValues.TAG;

public class OffersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;
    private DatabaseReference databaseReference;

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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();


        uid = myFirebaseUser.getEmail();
        itemName = getIntent().getStringExtra("itemName");
        itemPrice = getIntent().getStringExtra("itemPrice");
        mitemQuantity = getIntent().getStringExtra("itemQuantity");


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
        getMenuInflater().inflate(R.menu.offers, menu);
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
            OffersActivity.this.startActivity(new Intent(OffersActivity.this, MyAccountActivity.class));
        } else if(view == cancelBtn){
            OffersActivity.this.startActivity(new Intent(OffersActivity.this, MyAccountActivity.class));
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
