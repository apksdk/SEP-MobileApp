package com.riversidecorps.rebuy;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.models.Listing;

import java.util.Currency;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The type My account activity.
 */
public class MyAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Display user details
        userAvatarIV.setImageURI(mUser.getPhotoUrl());
        userIDTV.setText(mUser.getDisplayName());
        userEmailTV.setText(mUser.getEmail());

        //Set up recyclerview
        currentListingsRV.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Listings");

        FirebaseRecyclerAdapter<Listing, ListingHolder> mAdapter = new FirebaseRecyclerAdapter<Listing, ListingHolder>(
                Listing.class,
                R.layout.item_listing_overview,
                ListingHolder.class,
                ref) {
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
