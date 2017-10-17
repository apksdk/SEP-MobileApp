package com.riversidecorps.rebuy;

import android.app.Activity;
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
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.riversidecorps.rebuy.models.Listing;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The homepage / user account activity
 */

public class
MyAccountActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String DB_LISTING = "Listings";
    private static final String DB_USERS = "Users";
    private static final String DB_USERNAME = "username";
    public static final String ITEM_DELETED = "itemDeleted";
    public static final String USERS_PATH = "users/";
    public static final String AVATAR_PNG = "/avatar.png";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private static final int PERMISSION_REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    @BindView(R.id.userAvatarIV)
    ImageView userAvatarIV;

    @BindView(R.id.userIDTV)
    TextView userIDTV;

    @BindView(R.id.userEmailTV)
    TextView userEmailTV;

    @BindView(R.id.currentListingsRV)
    RecyclerView currentListingsRV;

    @BindView(R.id.noListingTV)
    TextView noListingTV;

    private ImageView mUserNavAvatarIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        MultiDex.install(this);
        ButterKnife.bind(this);
        //Setup UI
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Setup Nav View
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View navView = navigationView.getHeaderView(0);
        final TextView usernameNavTV = navView.findViewById(R.id.userNavIDTV);
        TextView emailNavTV = navView.findViewById(R.id.userNavEmailTV);
        mUserNavAvatarIV = navView.findViewById(R.id.userNavAvatarIV);

        //Setup Loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading your account details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Prevents being required to login every time
        if (mUser == null) {
            startActivity(new Intent(MyAccountActivity.this, LoginActivity.class));
            finish();
            return;
        }
        // UI Data Initialization
        userAvatarIV.setClickable(true);
        final String userID = mUser.getUid();
        userIDTV.setText(mUser.getDisplayName());
        usernameNavTV.setText(userIDTV.getText().toString());

        //Display user details
        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .into(userAvatarIV);
        userEmailTV.setText(mUser.getEmail());

        //Set up nav menu
        emailNavTV.setText(mUser.getEmail());
        Glide.with(this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(mUserNavAvatarIV);

        //Set up recyclerview
        currentListingsRV.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        currentListingsRV.addItemDecoration(dividerItemDecoration);

        DatabaseReference ref = mDatabase.getReference().child(DB_USERS).child(userID).child(DB_LISTING);
        //Create query to filter out deleted items
        Query query = ref.orderByChild(ITEM_DELETED).equalTo(false);
        FirebaseRecyclerAdapter<Listing, ListingPreviewHolder> mAdapter = new FirebaseRecyclerAdapter<Listing, ListingPreviewHolder>(
                Listing.class,
                R.layout.item_listing_overview,
                ListingPreviewHolder.class,
                query) {

            @Override
            public void onDataChanged() {
                //Hide no listing message & dismiss loading dialog
                noListingTV.setVisibility(View.GONE);
                progressDialog.dismiss();
                super.onDataChanged();
            }

            /**
             * Each time the data at the given Firebase location changes, this method will be called for
             * each item that needs to be displayed. The first two arguments correspond to the mLayout and
             * mModelClass given to the constructor of this class. The third argument is the item's position
             * in the list.
             * <p>
             * It populates the view using the data contained in the model.
             *
             * @param viewHolder The view to populate
             * @param model      The object containing the data used to populate the view
             * @param position   The position in the list of the view being populated
             */
            @Override
            protected void populateViewHolder(ListingPreviewHolder viewHolder, Listing model, int position) {
                //Setup UI for each listing item
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

    /**
     * Checks for permission to access external storage, starts activity to get image if it does otherwise
     * request permissions for access.
     *
     * @param view the image view being clicked
     */
    public void changeAvatarImage(View view) {
        //Check if application has read permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);
        } else {
            //Start new intent to get an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
        }
    }

    /**
     * Gets permission request result. If permission is granted then launch activity to select an image,
     * otherwise show a permission denied message.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check request code
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            //Check if permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_LONG);
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
        //Check request code
        if (requestCode == REQUEST_GALLERY_IMAGE) {
            //Check if result was successful
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //Get image from intent
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    //Convert image stream to bitmap
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //Create a new image view
                    final ImageView previewIV = new ImageView(this);
                    //Create a new relative layout & setup its' parameters
                    final RelativeLayout relativeLayout = new RelativeLayout(this);
                    RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ivParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    //Set parameters for the image view
                    previewIV.setLayoutParams(ivParams);
                    previewIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    previewIV.setImageBitmap(selectedImage);
                    //Add imageview to layout
                    relativeLayout.addView(previewIV);
                    //Create new dialog and display it
                    new AlertDialog.Builder(MyAccountActivity.this)
                            .setMessage(R.string.change_avatar_confirmation)
                            .setTitle(R.string.change_image_confirmation_title)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                //Upload Image
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final ProgressDialog progressDialog = new ProgressDialog(MyAccountActivity.this);
                                    progressDialog.setTitle(getString(R.string.save_avatar_title));
                                    progressDialog.setMessage(getString(R.string.save_avatar_message));
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    //Get the image from imageView
                                    previewIV.setDrawingCacheEnabled(true);
                                    previewIV.buildDrawingCache();
                                    Bitmap avatarImage = previewIV.getDrawingCache();
                                    //Convert image to byte array
                                    ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
                                    avatarImage.compress(Bitmap.CompressFormat.PNG, 100, bAOS);
                                    byte[] avatarImageBytes = bAOS.toByteArray();
                                    String userAvatarPath = USERS_PATH + mUser.getUid() + AVATAR_PNG;
                                    StorageReference userAvatarRef = mStorage.getReference(userAvatarPath);
                                    UploadTask uploadTask = userAvatarRef.putBytes(avatarImageBytes);
                                    uploadTask.addOnSuccessListener(MyAccountActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(taskSnapshot.getDownloadUrl())
                                                    .build();
                                            mUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(MyAccountActivity.this, R.string.avatar_saved_success, Toast.LENGTH_SHORT).show();
                                                        userAvatarIV.setImageBitmap(selectedImage);
                                                        mUserNavAvatarIV.setImageBitmap(selectedImage);

                                                    } else {
                                                        Toast.makeText(MyAccountActivity.this, R.string.avatar_saved_failed, Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .setView(relativeLayout)
                            .show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
        getMenuInflater().inflate(R.menu.my_account, menu);
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
                break;
                //If item is reset password
            case R.id.action_reset_password:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Closes the navigation drawer if it's open, otherwise exit the activity
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
     * Performs action when navigation menu item is clicked
     *
     * @param item selected item
     * @return true
     */
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
        } else if (id == R.id.nav_create_listing) {
            startActivity(new Intent(this, CreateListingActivity.class));
        } else if (id == R.id.nav_view_listings) {
            startActivity(new Intent(this, ViewListingsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
