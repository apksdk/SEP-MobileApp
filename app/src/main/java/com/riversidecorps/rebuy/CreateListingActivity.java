package com.riversidecorps.rebuy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.riversidecorps.rebuy.models.Listing;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class CreateListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser myFirebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static final String DB_LISTING = "Listings";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private EditText itemNameET;
    private EditText itemQantityET;
    private EditText itemPriceET;
    private EditText itemDescriptionET;
    private Button confirmListingBTN;
    private Button cancelListingBTN;

    private String userName;
    private ArrayList<Bitmap> imageList;

    @BindView(R.id.itemIV)
    ImageView itemIV;

    @BindView(R.id.createListingLayout)
    RelativeLayout createListingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        itemNameET = (EditText) findViewById(R.id.itemNameET);
        itemQantityET = (EditText) findViewById(R.id.itemQuantityET);
        itemPriceET = (EditText) findViewById(R.id.itemPriceET);
        itemDescriptionET = (EditText) findViewById(R.id.itemDescriptionET);
        confirmListingBTN = (Button) findViewById(R.id.confirmListingBTN);
        cancelListingBTN = (Button) findViewById(R.id.cancelListingBTN);

        myFirebaseAuth = FirebaseAuth.getInstance();
        myFirebaseUser = myFirebaseAuth.getCurrentUser();
        confirmListingBTN.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String userID = myFirebaseUser.getUid();
        DatabaseReference userRef = mDatabase.getReference().child("users").child(userID).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = (dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
     *
     * @param menu Menu at the top right of the screen
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the menu menu_other which includes logout and quit functions.
        getMenuInflater().inflate(R.menu.create_listing, menu);
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

    public void cancelListingBtnHandler(View view) {
        //Needs to check if there's changes, then ask if they want to confirm
        finish();
    }

    // TO DO: Get the current time if possible
    // TO DO: Multiple image upload support
    private void saveListing() {
        //Check if all required fields are filled out
        if (validateForm()) {
            // Retrieve all relevant data for the listing
            String name = itemNameET.getText().toString().trim();
            Integer quantity = Integer.parseInt(itemQantityET.getText().toString().trim());
            String price = itemPriceET.getText().toString().trim();
            String description = itemDescriptionET.getText().toString().trim();
            final String sellerID = myFirebaseUser.getUid();
            //Get current date
            Date date = new Date();
            Date newDate = new Date(date.getTime() + (604800000L * 2) + (24 * 60 * 60));
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            String stringDate = dt.format(newDate);

            //Create new listing
            final Listing newListing = new Listing(userName, name, quantity, price, description, stringDate);
            //Create new listing w/ minimal information - used for seller view overall listings
            final Listing newMinListing = new Listing(name, price, stringDate);
            //Get the image from imageView
            itemIV.setDrawingCacheEnabled(true);
            itemIV.buildDrawingCache();
            Bitmap itemImage = itemIV.getDrawingCache();
            //Convert image to byte array
            ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
            itemImage.compress(Bitmap.CompressFormat.PNG, 100, bAOS);
            byte[] itemImageBytes = bAOS.toByteArray();
            //Create image path for storage
            String imagePath = "itemImageListings/" + UUID.randomUUID() + ".png";
            //Upload image(s)
            StorageReference itemImageRef = mStorage.getReference(imagePath);
            UploadTask uploadTask = itemImageRef.putBytes(itemImageBytes);
            uploadTask.addOnSuccessListener(CreateListingActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Add image URL to listing
                    newListing.setItemImage(taskSnapshot.getDownloadUrl().toString());
                    //Save listing on Firebase
                    final String listingID = databaseReference.child(DB_LISTING).push().getKey();
                    databaseReference.child(DB_LISTING).child(listingID).setValue(newListing).addOnSuccessListener(CreateListingActivity.this, new OnSuccessListener<Void>() {
                        /**
                         * Create a toast when listing has been stored to inform the user that their listing has been successfully created
                         * @param aVoid void
                         */
                        @Override
                        public void onSuccess(Void aVoid) {
                            databaseReference.child("users").child(sellerID).child("Listings").child(listingID).setValue(newMinListing).addOnSuccessListener(CreateListingActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getBaseContext(), "Your listing has been successfully created!", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * Returns a boolean regarding to the form completion status.
     *
     * @return true if form is filled, otherwise false.
     */
    private boolean validateForm() {
        boolean validForm = true;
        //Loop through all children inside the layout
        for (int i = 0; i < createListingLayout.getChildCount(); i++) {
            //Check if current child is an EditText
            if (createListingLayout.getChildAt(i) instanceof EditText) {
                EditText currentET = (EditText) createListingLayout.getChildAt(i);
                //Check if the edit text is empty
                if (TextUtils.isEmpty(currentET.getText().toString())) {
                    //Display an error if it's empty
                    currentET.setError("This field cannot be empty!");
                    validForm = false;
                }
            }
        }
        return validForm;
    }

    @Override
    public void onClick(View view) {
        if (view == confirmListingBTN) {
            saveListing();
        } else if (view == cancelListingBTN) {
            finish();
        }
    }

    /**
     * Checks if app has permission to read external storage, and requests permission if it doesn't. Otherwise, open image gallery
     *
     * @param view ImageView
     */
    //TO DO: Maybe allow users to take images using their camera?
    @OnClick(R.id.itemIV)
    public void changeItemImage(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
        }
    }

    /**
     * Checks whether user has given the application permission to read external storage, and either shows a permission denied message
     * or launches a new intent to pick an image
     *
     * @param requestCode  request code for reading gallery image
     * @param permissions  permission
     * @param grantResults request permission's result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            //Check if user has given permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Read external storage for image media
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
            } else {
                //Display a permission denied error to the user
                Toast.makeText(getApplicationContext(), "Could not retrieve images." + "\n" + "Reason: Permission Denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Set the item image to the selected image from gallery/photos
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check request type
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            //Check if result was successful
            if (resultCode == RESULT_OK) {
                try {
                    //Grab image uri from intent
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    //Convert image stream to bitmap
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //Set item image
                    itemIV.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
