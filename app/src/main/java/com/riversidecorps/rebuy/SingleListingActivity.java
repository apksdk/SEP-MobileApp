package com.riversidecorps.rebuy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.riversidecorps.rebuy.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.riversidecorps.rebuy.R.id.itemImageIV;

public class SingleListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference mDatabaseReference;

    private ProgressDialog mProgressDialog;
    private static final String DB_LISTING = "Listings";
    private static final String DB_USERS = "Users";
    private static final String DB_MESSAGES = "Messages";

    private String mItemID;
    private String mItemName;
    private String mItemPrice;
    private String mItemDes;
    private Integer mItemQuantity;
    private String mItemSellerID;
    private String mUserId;
    private ArrayList<String> mItemImages = new ArrayList<>();

    private ImageView mItemImageIV;
    private TextView mNameTv;
    private TextView mPriceTv;
    private TextView mQuantityTv;
    private TextView mDescriptionTV;
    private Button mOfferBTN;
    private Button mBuyBTN;
    private Button mMessageBTN;

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
        //Initial Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mProgressDialog = new ProgressDialog(this);
        //Bind butterknife
        ButterKnife.bind(this);
        //Get info passed from previous activity
        Intent intent = getIntent();
        boolean isOwner = intent.getBooleanExtra("isOwner", false);
        mItemID = intent.getStringExtra("itemId");
        mItemName = intent.getStringExtra("itemName");
        mItemPrice = intent.getStringExtra("itemPrice");
        mItemDes = intent.getStringExtra("itemDes");
        mItemQuantity = intent.getIntExtra("itemQuantity", 0);
        mItemImages = intent.getStringArrayListExtra("itemImages");
        mItemSellerID = getIntent().getStringExtra("itemSellerId");
        // Get database Reference
        mDatabaseReference = mDatabase.getReference();
        mUserId = mUser.getUid();
        //Set up UI
        mNameTv = findViewById(R.id.itemNameTV);
        mNameTv.setText(mItemName);

        mPriceTv = findViewById(R.id.itemPriceTV);
        mPriceTv.setText(mItemPrice);

        mQuantityTv = findViewById(R.id.itemQuantityTV);
        mQuantityTv.setText(String.valueOf(mItemQuantity));

        mDescriptionTV = findViewById(R.id.descriptionTV);
        mDescriptionTV.setText(mItemDes);

        mOfferBTN = findViewById(R.id.offerBTN);
        mBuyBTN = findViewById(R.id.buyBTN);
        mMessageBTN = findViewById(R.id.messageBTN);

        //Set on click listeners for each button
        mOfferBTN.setOnClickListener(this);
        mBuyBTN.setOnClickListener(this);
        mMessageBTN.setOnClickListener(this);
        //Check if the item belongs to the owner
        if(isOwner) {
            mOfferBTN.setVisibility(View.INVISIBLE);
            mBuyBTN.setVisibility(View.INVISIBLE);
            mMessageBTN.setVisibility(View.INVISIBLE);
        }

        //Set up nav UI
        final View navView = navigationView.getHeaderView(0);
        final TextView usernameNavTV = navView.findViewById(R.id.userNavIDTV);
        TextView emailNavTV = navView.findViewById(R.id.userNavEmailTV);
        ImageView userNavAvatarIV = navView.findViewById(R.id.userNavAvatarIV);
        usernameNavTV.setText(mUser.getDisplayName());
        //Setup loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading listing information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Set up nav menu
        emailNavTV.setText(mUser.getEmail());
        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(userNavAvatarIV);

        mItemImageIV = findViewById(itemImageIV);
        //Load an image
        Glide.with(this)
                .load(mItemImages.get(0))
                .into(mItemImageIV);
        //Set images for each preview IV & makes them visible
        for (int i = 0; i < mItemImages.size(); i++) {
            int pos = i;
            pos++;
            int currentIVID = getResources().getIdentifier("itemPreview" + pos + "IV", "id", getPackageName());
            ImageView currentIV = findViewById(currentIVID);
            //Make the ImageView visible
            currentIV.setVisibility(View.VISIBLE);
            //Load the image from the array list according to the loop's current position
            Glide.with(this)
                    .load(mItemImages.get(i))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Toast.makeText(SingleListingActivity.this, "Listing failed to load properly due to a network issue.", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressDialog.dismiss();
                            return false;
                        }
                    })
                    .into(currentIV);
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
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;

            //If item is reset password
            case R.id.action_reset_password:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Closes the Nav Drawer if it's open, otherwise close activity
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Gets which item on the nav menu has been selected, then open it.
     *
     * @param item The selected item
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Get clicked id of the menu item
        int id = item.getItemId();

        if (id == R.id.nav_my_account) {
            //Move to the MyAccount Activity
            startActivity(new Intent(this, MyAccountActivity.class));
        } else if (id == R.id.nav_message_inbox) {
            //Move to the MessageInbox Activity
            startActivity(new Intent(this, MessageInboxActivity.class));
        } else if (id == R.id.nav_view_offers) {
            //Move to the ViewOffers Activity
            startActivity(new Intent(this, ViewOffersActivity.class));
        } else if (id == R.id.nav_create_listing) {
            //Move to the CreateListing Activity
            startActivity(new Intent(this, CreateListingActivity.class));
        } else if (id == R.id.nav_view_listings) {
            //Move to the ViewListing Activity
            finish();
        }

        //Close drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Checks which view has been selected & runs its respective functions
     *
     * @param view The clicked view
     */
    @Override
    public void onClick(View view) {
        //Check if user selected the offer button
        if (view == mOfferBTN) {
            //Create a new intent
            Intent OfferActivity = new Intent(this, CreateOfferActivity.class);
            //Add all relevant data for the offer activity
            OfferActivity.putExtra("itemName", mItemName);
            OfferActivity.putExtra("itemPrice", mItemPrice);
            OfferActivity.putExtra("itemQuantity", mItemQuantity);
            OfferActivity.putExtra("itemDes", mItemDes);
            OfferActivity.putExtra("listingImage", mItemImages.get(0));
            OfferActivity.putExtra("itemSellerID", mItemSellerID);
            OfferActivity.putExtra("itemId", mItemID);
            //Start activity
            startActivity(OfferActivity);
        }
        //Check if user selected the message button
        if (view == mMessageBTN) {
            //Create a new dialog & do initial setup
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Message");
            builder.setIcon(R.drawable.ic_message_dialog);
            builder.setCancelable(false);
            final EditText messageInput = new EditText(this);
            messageInput.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(messageInput);
            builder.setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                /**
                 * Sends the message by saving it on Firebase Database
                 *
                 * @param dialog The dialog interface
                 * @param which Which dialog
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Check if there's an input
                    if (messageInput.getText().toString().isEmpty()) {
                        //Display error message
                        Toast.makeText(SingleListingActivity.this, "Please enter a message before sending.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Set & Display a loading dialog
                        mProgressDialog.setMessage(getString(R.string.reply_message));
                        mProgressDialog.show();
                        //Get current time
                        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:MM").format(new Date());
                        //Get the user's message
                        String userMessage = messageInput.getText().toString();
                        //Get a message id from Firebase Database
                        final String messageID = mDatabaseReference.child(DB_USERS).child(mItemSellerID).child(DB_MESSAGES).push().getKey();
                        //Create a new message
                        Message message = new Message(userMessage, mUser.getDisplayName(), dateTime, mItemName,messageID, mUserId);
                        //Save the message
                        mDatabaseReference.child(DB_USERS).child(mItemSellerID).child(DB_MESSAGES).child(messageID).setValue(message).addOnSuccessListener(SingleListingActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getBaseContext(), "Your message has been sent to the seller!", Toast.LENGTH_LONG).show();
                                //Close the progress dialog
                                mProgressDialog.dismiss();
                                finish();
                            }
                        });
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                /**
                 * Closes the dialog
                 *
                 * @param dialog The dialog
                 * @param which Which dialog
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        if (view == mBuyBTN) {
            //Create a new dialog & do initial setup
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("How many items would you like to buy at " + mItemPrice + " each?");
            builder.setIcon(R.drawable.ic_message_dialog);
            builder.setCancelable(false);
            final EditText quantityInput = new EditText(this);
            quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(quantityInput);
            builder.setPositiveButton("Buy Now", new DialogInterface.OnClickListener() {
                /**
                 * Sends the message by saving it on Firebase Database
                 *
                 * @param dialog The dialog interface
                 * @param which Which dialog
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Check if there's an input
                    if (quantityInput.getText().toString().isEmpty()) {
                        //Display error message
                        Toast.makeText(SingleListingActivity.this, "Please enter a quantity before buying", Toast.LENGTH_SHORT).show();
                    } else {
                        //Grab the input
                        final Integer quantity = Integer.parseInt(quantityInput.getText().toString());
                        //Check if the user inputs more than the seller is offering
                        if(quantity > mItemQuantity){
                            //Display error message
                            Toast.makeText(SingleListingActivity.this, "Please enter a quantity smaller than the amount of listed items", Toast.LENGTH_LONG).show();
                        } else {
                            //Set & Display a loading dialog
                            mProgressDialog.setMessage("Buying...");
                            mProgressDialog.show();
                            //Get current time
                            String datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                            //Get the user's message
                            String userMessage = mUser.getDisplayName() + " has bought " + quantity + " " + mItemName + "(s)";
                            //Get a message id from Firebase Database
                            final String messageID = mDatabaseReference.child(DB_USERS).child(mItemSellerID).child(DB_MESSAGES).push().getKey();
                            //Create a new message
                            Message message = new Message(userMessage, mUser.getDisplayName(), datetime, mItemName, messageID, mUserId);
                            //Save the message
                            mDatabaseReference.child(DB_USERS).child(mItemSellerID).child(DB_MESSAGES).child(messageID).setValue(message).addOnSuccessListener(SingleListingActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //If the amount bought sets listing's quantity to 0
                                    if (mItemQuantity == quantity){
                                        //Set the listing's quantity to 0 and set it to complete in both location (under listings and user's listings)
                                        mDatabaseReference.child(DB_LISTING).child(mItemID).child("itemCompleted").setValue(true);
                                        mDatabaseReference.child(DB_USERS).child(mItemSellerID).child(DB_LISTING).child(mItemID).child("itemCompleted").setValue(true);
                                        mDatabaseReference.child(DB_LISTING).child(mItemID).child("itemQuantity").setValue(0);
                                    } else {
                                        //Sets listing's quantity to new quantity
                                        mDatabaseReference.child(DB_LISTING).child(mItemID).child("itemQuantity").setValue(mItemQuantity - quantity);
                                    }
                                    Toast.makeText(getBaseContext(), "Thank you for your purchase", Toast.LENGTH_LONG).show();
                                    //Close the progress dialog
                                    mProgressDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                /**
                 * Closes the dialog
                 *
                 * @param dialog The dialog
                 * @param which Which dialog
                 */
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    /**
     * Gets the selected preview image and sets it as the main image
     *
     * @param view The selected Preview ImageView
     */
    @OnClick({R.id.itemPreview1IV, R.id.itemPreview2IV, R.id.itemPreview3IV})
    public void imageSwitchHandler(View view) {
        //Enable caching
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //Get the bitmap of the selected view
        Bitmap selectedImage = view.getDrawingCache();
        //Set the IV to the bitmap
        mItemImageIV.setImageBitmap(selectedImage);
    }
}
