package com.riversidecorps.rebuy.AccountManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.riversidecorps.rebuy.ListingManagement.CreateListingActivity;
import com.riversidecorps.rebuy.ListingManagement.ListingHolder;
import com.riversidecorps.rebuy.ListingManagement.SearchListingsActivity;
import com.riversidecorps.rebuy.ListingManagement.ViewListingsActivity;
import com.riversidecorps.rebuy.OfferManagement.OffersActivity;
import com.riversidecorps.rebuy.R;
import com.riversidecorps.rebuy.models.Listing;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * The type My account activity.
 */
public class
  MyAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseAuth.AuthStateListener mAuthListener;

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

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Prevents being required to login every time
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            //User is logged in;
        }

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

        //Display user details
        //userAvatarIV.setImageURI(mUser.getPhotoUrl());
        //userIDTV.setText(mUser.getDisplayName());
        //userEmailTV.setText(mUser.getEmail());

        //Set up recyclerview
        currentListingsRV.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        currentListingsRV.addItemDecoration(dividerItemDecoration);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Listings");
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
                Log.i("GetItemID", getRef(position).getKey());
            }
        };
        currentListingsRV.setAdapter(mAdapter);


        //Listing listing = new Listing("testuser", "Test Item", 1, "$4.99", "Default Description");
        //ref.child("Listings").push().setValue(listing);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
//                    Listing listing = listingSnapshot.getValue(Listing.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast failedToast = Toast.makeText(getBaseContext(), "Failed to retrieve listings...", Toast.LENGTH_LONG);
//                failedToast.show();
//            }
//        });

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
        } else if (id == R.id.nav_offers) {
            startActivity(new Intent(this, OffersActivity.class));
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
