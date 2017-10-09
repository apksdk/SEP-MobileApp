package com.riversidecorps.rebuy;

import android.app.ProgressDialog;
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
import android.text.InputType;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static com.riversidecorps.rebuy.R.id.itemImageIV;

public class SingleListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private static final String DB_MESSAGES = "messages";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";
    private Button offerBTN, buyBTN, messageBTN;

    private String itemID;
    private String itemName;
    private String itemPrice;
    private String itemDes;
    private Integer itemQuantity;
    private String userID;
    private String userName;
    private String mItemSellerID;
    private ArrayList<String> itemImages = new ArrayList<>();

    private TextView loginInfor;
    private ImageView mitemImageIV;
    private TextView mNameTv;
    private TextView mPriceTv;
    private TextView mQuantityTv;

    @BindView(R.id.itemPreview1IV)
    ImageView itemPreview1IV;
    @BindView(R.id.itemPreview2IV)
    ImageView itemPreview2IV;
    @BindView(R.id.itemPreview3IV)
    ImageView itemPreview3IV;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        itemID = intent.getStringExtra("itemId");
        itemName = intent.getStringExtra("itemName");
        itemPrice = intent.getStringExtra("itemPrice");
        itemDes = intent.getStringExtra("itemDes");
        itemQuantity = intent.getIntExtra("itemQuantity", 0);
        itemImages = intent.getStringArrayListExtra("itemImages");


        mItemSellerID = getIntent().getStringExtra("itemSellerId");
        userID = mUser.getUid();
        loginInfor = findViewById(R.id.logininfor);
        mitemImageIV = findViewById(itemImageIV);
        Glide.with(this)
                .load(itemImages.get(0))
                .into(mitemImageIV);

        for(int i = 0; i < itemImages.size(); i++) {
            int pos = i;
            pos++;
            int currentIVID = getResources().getIdentifier("itemPreview" + pos + "IV", "id", getPackageName());
            ImageView currentIV = (ImageView) findViewById(currentIVID);
            currentIV.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(itemImages.get(i))
                    .into(currentIV);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mNameTv = (TextView) findViewById(R.id.itemNameTV);
        mNameTv.setText(itemName);

        mPriceTv = (TextView) findViewById(R.id.itemPriceTV);
        mPriceTv.setText(itemPrice);

        mQuantityTv = (TextView) findViewById(R.id.itemQuantityTV);
        mQuantityTv.setText(itemQuantity.toString());

        TextView iDesTv = (TextView) findViewById(R.id.descriptionTV);
        iDesTv.setText(itemDes);

        offerBTN = (Button) findViewById(R.id.offerBTN);
        buyBTN = (Button) findViewById(R.id.buyBTN);
        messageBTN = findViewById(R.id.messageBTN);
        offerBTN.setOnClickListener(this);
        buyBTN.setOnClickListener(this);
        messageBTN.setOnClickListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DatabaseReference userRef = mDatabase.getReference().child("users").child(userID).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.getValue(String.class);
                loginInfor.setText("Welcome, " + userName + "!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //Set listener that triggers when a user signs out
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //FirebaseUser user = myFirebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, AUTH_IN + mUser.getUid());
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
        getMenuInflater().inflate(R.menu.single_listing, menu);
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

        } else if (id == R.id.nav_view_offers) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == offerBTN) {

            Intent OfferActivity = new Intent(this, CreateOfferActivity.class);
            OfferActivity.putExtra("itemName", itemName);
            OfferActivity.putExtra("itemPrice", itemPrice);
            OfferActivity.putExtra("itemQuantity", itemQuantity);
            OfferActivity.putExtra("itemDes", itemDes);
            OfferActivity.putExtra("itemId", itemID);
            OfferActivity.putExtra("listingImage", itemImages.get(0));
            startActivity(OfferActivity);
        }

        if (view == messageBTN) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Message");
            builder.setIcon(R.drawable.ic_message_dialog);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.setMessage(getString(R.string.creating_listing_message));
                    progressDialog.show();
                    String datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                    String message = input.getText().toString();
                    final String messageID = databaseReference.child("users").child(mItemSellerID).child(DB_MESSAGES).push().getKey();
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("content").setValue(message);
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("title").setValue(itemName);
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("sender").setValue(userName);
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("sender_id").setValue(userID);
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("message_id").setValue(messageID);
                    databaseReference.child("users").child(mItemSellerID).child("messages").child(messageID).child("datetime").setValue(datetime).addOnSuccessListener(SingleListingActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getBaseContext(), "Your message has been sent to the seller!", Toast.LENGTH_LONG).show();
                            //Close the progress dialog
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

    }
}
