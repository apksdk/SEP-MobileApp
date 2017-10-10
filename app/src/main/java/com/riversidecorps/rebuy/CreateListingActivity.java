package com.riversidecorps.rebuy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.riversidecorps.rebuy.models.Listing;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.content.ContentValues.TAG;

public class CreateListingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static final String DB_LISTING = "Listings";
    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1000;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private ProgressDialog progressDialog;

    private EditText itemNameET;
    private EditText itemQantityET;
    private EditText itemPriceET;
    private EditText itemDescriptionET;
    private Button confirmListingBTN;
    private Button cancelListingBTN;

    private String userName;
    private ArrayList<String> imageList = new ArrayList<>();
    private int requestingIV;

    @BindView(R.id.itemIV)
    ImageView itemIV;

    @BindView(R.id.item2IV)
    ImageView item2IV;

    @BindView(R.id.item3IV)
    ImageView item3IV;

    @BindView(R.id.addImageBTN)
    Button addImageBTN;

    @BindView(R.id.createListingLayout)
    RelativeLayout createListingLayout;

    @BindView(R.id.itemImagesLayout)
    LinearLayout itemImagesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialises progress dialog
        progressDialog = new ProgressDialog(this);

        itemNameET = (EditText) findViewById(R.id.itemNameET);
        itemQantityET = (EditText) findViewById(R.id.itemQuantityET);
        itemPriceET = (EditText) findViewById(R.id.itemPriceET);
        itemDescriptionET = (EditText) findViewById(R.id.itemDescriptionET);
        confirmListingBTN = (Button) findViewById(R.id.confirmListingBTN);
        cancelListingBTN = (Button) findViewById(R.id.cancelListingBTN);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        confirmListingBTN.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String userID = mUser.getUid();
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
            //Confirm exit if there's change
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
            //Do Nothing
        } else if (id == R.id.nav_view_listings) {
            startActivity(new Intent(this, ViewListingsActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cancelListingBtnHandler(View view) {
        Boolean isEdited = false;
        for (int i = 0; i < createListingLayout.getChildCount(); i++) {
            if (createListingLayout.getChildAt(i) instanceof EditText) {
                EditText currentET = (EditText) createListingLayout.getChildAt(i);
                if (!currentET.getText().toString().isEmpty()) {
                    isEdited = true;
                    new AlertDialog.Builder(CreateListingActivity.this)
                            .setTitle("Exit Confirmation")
                            .setMessage("Are you sure you want to exit without saving your changes?")
                            .setIcon(R.drawable.ic_dialog_warning)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                } else {
                    isEdited = false;
                }
            }

        }
        if (!isEdited) {
            finish();
        }
    }

    // TO DO: Get the current time if possible
    private int imageCount;

    private void saveListing() {
        //Check if all required fields are filled out
        if (validateForm()) {
            imageCount = 0;
            //Show a progress
            progressDialog.setMessage(getString(R.string.creating_listing_message));
            progressDialog.show();
            // Retrieve all relevant data for the listing
            String name = itemNameET.getText().toString().trim();
            Integer quantity = Integer.parseInt(itemQantityET.getText().toString().trim());
            NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);
            String price = numFormat.format(Double.parseDouble(itemPriceET.getText().toString()));
            String description = itemDescriptionET.getText().toString().trim();
            final String sellerID = mUser.getUid();
            //Get current date
            String stringDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            //Create new listing
            final Listing newListing = new Listing(userName, name, quantity, price, description, stringDate);
            //Create new listing w/ minimal information - used for seller view overall listings
            final Listing newMinListing = new Listing(name, price, stringDate);
            for (int i = 0; i < itemImagesLayout.getChildCount(); i++) {
                if (itemImagesLayout.getChildAt(i) instanceof ImageView && itemImagesLayout.getChildAt(i).getVisibility() != View.GONE) {
                    imageCount++;
                }
            }

            for (int i = 0; i < imageCount; i++) {
                ImageView currentIV = (ImageView) itemImagesLayout.getChildAt(i);
                final String uniqueId = UUID.randomUUID().toString();
                //Get the image from imageView
                currentIV.setDrawingCacheEnabled(true);
                currentIV.buildDrawingCache();
                Bitmap itemImage = currentIV.getDrawingCache();
                //Convert image to byte array
                ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                itemImage.compress(Bitmap.CompressFormat.PNG, 100, bAOS);
                byte[] itemImageBytes = bAOS.toByteArray();
                //Create image path for storage
                String imagePath = "itemImageListings/" + uniqueId + ".png";
                //Upload image(s)
                StorageReference itemImageRef = mStorage.getReference(imagePath);
                UploadTask uploadTask = itemImageRef.putBytes(itemImageBytes);
                uploadTask.addOnSuccessListener(CreateListingActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageList.add(taskSnapshot.getDownloadUrl().toString());

                        if (imageList.size() == imageCount) {
                            //Add image URL to listing
                            newListing.setItemImages(imageList);
                            newListing.setItemId(uniqueId);
                            newListing.setItemSellerId(sellerID);
                            newMinListing.setItemImage(imageList.get(0));
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
                                            //Close the progress dialog
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
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
        } else if (view instanceof ImageView) {
            changeItemImage(view);
        }
    }

    private int mItemIVCount = 1;

    @OnClick(R.id.addImageBTN)
    public void addImageBTNHandler(View view) {
        if (mItemIVCount == 1) {
            item2IV.setVisibility(View.VISIBLE);
            mItemIVCount++;
        } else if (mItemIVCount == 2) {
            item3IV.setVisibility(View.VISIBLE);
            mItemIVCount++;
            addImageBTN.setVisibility(View.GONE);
        }
    }

    @OnLongClick({R.id.itemImagesLayout, R.id.itemIV, R.id.item2IV, R.id.item3IV})
    public boolean removeLastImageHandler(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Image")
                .setMessage("Would you like to remove the last added image?")
                .setPositiveButton("Remove Image", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mItemIVCount == 3) {
                            item3IV.setVisibility(View.GONE);
                            addImageBTN.setVisibility(View.VISIBLE);
                            mItemIVCount--;
                        } else if (mItemIVCount == 2) {
                            item2IV.setVisibility(View.GONE);
                            mItemIVCount--;
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
        return false;
    }

    /**
     * Checks if app has permission to read external storage, and requests permission if it doesn't. Otherwise, open image gallery
     *
     * @param view ImageView
     */
    //TO DO: Maybe allow users to take images using their camera?
    public void changeItemImage(View view) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");

            for (int i = 0; i < itemImagesLayout.getChildCount(); i++) {
                if (itemImagesLayout.getChildAt(i) == view) {
                    requestingIV = i;
                    startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
                }
            }
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
                    switch (requestingIV) {
                        case 0:
                            itemIV.setImageBitmap(selectedImage);
                            break;
                        case 1:
                            item2IV.setImageBitmap(selectedImage);
                            break;
                        case 2:
                            item3IV.setImageBitmap(selectedImage);
                            break;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}