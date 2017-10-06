package com.riversidecorps.rebuy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.models.Listing;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Currency;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * The type My account activity.
 */
public class 
  MyAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private static final String AUTH_IN = "onAuthStateChanged:signed_in:";
    private static final String AUTH_OUT = "onAuthStateChanged:signed_out";

    @BindView(R.id.userAvatarIV)
    ImageView userAvatarIV;

    @BindView(R.id.userIDTV)
    TextView userIDTV;

    @BindView(R.id.userEmailTV)
    TextView userEmailTV;

    @BindView(R.id.currentListingsRV)
    RecyclerView currentListingsRV;

    // TO DO - CHECK OFFLINE & DISPLAY ERROR IF SO, LOAD IMAGES
    // ALSO MAYBE CREATE NEW SECTION FOR LISTING PREVIEWS IN FIREBASE TO AVOID LOADING OTHER INFOS
    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);


        MultiDex.install(this);
        ButterKnife.bind(this);

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
        final TextView usernameNavTV = (TextView) navView.findViewById(R.id.userNavIDTV);
        TextView emailNavTV = (TextView) navView.findViewById(R.id.userNavEmailTV);

        //Prevents being required to login every time
        if (mUser == null) {
            startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
            finish();
            return;
        } else {
            //User is logged in;
        }

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

        userAvatarIV.setClickable(true);

        final String userID = mUser.getUid();

        DatabaseReference userRef = mDatabase.getReference().child("users").child(userID).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userIDTV.setText(dataSnapshot.getValue(String.class));
                usernameNavTV.setText(userIDTV.getText().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Display user details
        //userAvatarIV.setImageURI(mUser.getPhotoUrl());
        userEmailTV.setText(mUser.getEmail());

        //Set up nav menu
        emailNavTV.setText(mUser.getEmail());

        //Set up recyclerview
        currentListingsRV.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        currentListingsRV.addItemDecoration(dividerItemDecoration);

        DatabaseReference ref = mDatabase.getReference().child("users").child(userID).child("Listings");
//        Listing listing = new Listing("testuser", "Test Item", 1, "$4.99", "Default Description", "22/09/2017");
//        ref.push().setValue(listing);
        Query query = ref.orderByChild("itemDeleted").equalTo(false);
        FirebaseRecyclerAdapter<Listing, ListingHolder> mAdapter = new FirebaseRecyclerAdapter<Listing, ListingHolder>(
                Listing.class,
                R.layout.item_listing_overview,
                ListingHolder.class,
                query) {
            /**
             * Each time the data at the given Firebase location changes, this method will be called for
             * each item that needs to be displayed. The first two arguments correspond to the mLayout and
             * mModelClass given to the constructor of this class. The third argument is the item's position
             * in the list.
             * <p>
             * Your implementation should populate the view using the data contained in the model.
             *
             * @param viewHolder The view to populate
             * @param model      The object containing the data used to populate the view
             * @param position   The position in the list of the view being populated
             */
            @Override
            protected void populateViewHolder(ListingHolder viewHolder, Listing model, int position) {
                viewHolder.setItemNameTV(model.getItemName());
                viewHolder.setItemPriceTV(model.getItemPrice());
                //Get the primary key of the item
                viewHolder.setItemID(getRef(position).getKey());
                Glide.with(MyAccountActivity.this).load(model.getItemImage()).into(viewHolder.getItemImagePreviewIV());
                Log.i("GetItemID", getRef(position).getKey());
                viewHolder.setUserID(userID);
            }
        };
        currentListingsRV.setAdapter(mAdapter);
    }

    public void changeAvatarImage(View view) {
        //Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Could not retrieve image." + "\n" + "Reason: Permission Denied.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /**
     * Set the avatar image to the selected image from gallery/photos
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    userAvatarIV.setImageBitmap(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
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
            //Do nothing
        } else if (id == R.id.nav_message_inbox) {
            startActivity(new Intent(this, MessageInboxActivity.class));
        } else if (id == R.id.nav_view_offers) {
            startActivity(new Intent(this, ViewOffersActivity.class));
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
}
