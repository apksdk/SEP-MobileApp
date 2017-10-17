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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

public class ModifyListingActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_STORAGE = 1000;
    private static final int REQUEST_GALLERY_IMAGE = 2;

    private static final String DB_LISTING = "Listings";
    private static final String DB_USERS = "Users";

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    private int mItemIVCount = 1;
    private String mItemID;
    private int requestingIV;
    private ArrayList<String> mItemImages = new ArrayList<>();

    @BindView(R.id.item1IV)
    ImageView item1IV;

    @BindView(R.id.item2IV)
    ImageView item2IV;

    @BindView(R.id.item3IV)
    ImageView item3IV;

    @BindView(R.id.itemQuantityET)
    EditText itemQuantityET;

    @BindView(R.id.itemNameET)
    EditText itemNameET;

    @BindView(R.id.itemPriceET)
    EditText itemPriceET;

    @BindView(R.id.itemDescriptionET)
    EditText itemDescriptionET;

    @BindView(R.id.addImageBTN)
    Button addImageBTN;

    @BindView(R.id.itemImagesLayout)
    LinearLayout itemImagesLayout;

    @BindView(R.id.modifyListingLayout)
    RelativeLayout modifyListingLayout;

    @BindView(R.id.cancelChangesBTN)
    Button cancelChangesBTN;

    @BindView(R.id.saveChangesBTN)
    Button saveChangesBTN;
    private int imageCount;
    private ProgressDialog progressDialog;
    private ArrayList<String> imageList = new ArrayList<>();

    private String mItemName;
    private String mItemQuantity;
    private String mItemPrice;
    private String mItemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_listing);
        ButterKnife.bind(this);

        //Setup Loading dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading listing information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent intent = getIntent();
        mItemID = intent.getStringExtra("itemID");

        DatabaseReference ref = mDatabase.getReference().child(DB_LISTING).child(mItemID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Listing listing = dataSnapshot.getValue(Listing.class);
                mItemName = listing.getItemName();
                mItemQuantity = String.valueOf(listing.getItemQuantity());
                mItemPrice = String.valueOf(listing.getItemPrice()).substring(1);
                mItemDescription = listing.getItemDescription();

                itemNameET.setText(mItemName);
                itemQuantityET.setText(mItemQuantity);
                itemPriceET.setText(mItemPrice);
                itemDescriptionET.setText(mItemDescription);
                mItemImages = listing.getItemImages();

                //Set images for each preview IV & makes them visible
                for (int i = 0; i < mItemImages.size(); i++) {
                    int pos = i;
                    pos++;
                    int currentIVID = getResources().getIdentifier("item" + pos + "IV", "id", getPackageName());
                    ImageView currentIV = findViewById(currentIVID);
                    //Make the ImageView visible
                    currentIV.setVisibility(View.VISIBLE);
                    //Load the image from the array list according to the loop's current position
                    Glide.with(ModifyListingActivity.this)
                            .load(mItemImages.get(i))
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Toast.makeText(ModifyListingActivity.this, "Listing failed to load properly due to a network issue.", Toast.LENGTH_LONG).show();
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
                //Hide button if there's 3 images
                if (mItemImages.size() == 3) {
                    addImageBTN.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnLongClick({R.id.itemImagesLayout, R.id.item1IV, R.id.item2IV, R.id.item3IV})
    public boolean removeLastImageHandler(View v) {
        if (mItemIVCount != 1) {
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
        }
        return false;
    }

    public void addImageHandler(View view) {
        if (mItemIVCount == 1) {
            item2IV.setVisibility(View.VISIBLE);
            mItemIVCount++;
        } else if (mItemIVCount == 2) {
            item3IV.setVisibility(View.VISIBLE);
            mItemIVCount++;
            addImageBTN.setVisibility(View.GONE);
        }
    }

    public void saveChangesHandler(View view) {
        saveListing();
    }

    private void saveListing() {
        //Check if all required fields are filled out
        if (validateForm()) {
            imageCount = 0;
            //Show a progress
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving your changes...");
            progressDialog.show();
            // Retrieve all relevant data for the listing
            String name = itemNameET.getText().toString().trim();
            Integer quantity = Integer.parseInt(itemQuantityET.getText().toString().trim());
            NumberFormat numFormat = NumberFormat.getCurrencyInstance(Locale.US);
            String price = numFormat.format(Double.parseDouble(itemPriceET.getText().toString()));
            String description = itemDescriptionET.getText().toString().trim();
            final String sellerID = mUser.getUid();
            //Get current date
            String stringDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            //Create new listing
            final Listing newListing = new Listing(mUser.getDisplayName(), name, quantity, price, description, stringDate);
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
                uploadTask.addOnSuccessListener(ModifyListingActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                            final DatabaseReference ref = mDatabase.getReference();
                            ref.child(DB_LISTING).child(mItemID).setValue(newListing).addOnSuccessListener(ModifyListingActivity.this, new OnSuccessListener<Void>() {
                                /**
                                 * Create a toast when listing has been stored to inform the user that their listing has been successfully created
                                 * @param aVoid void
                                 */
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ref.child(DB_USERS).child(sellerID).child(DB_LISTING).child(mItemID).setValue(newMinListing).addOnSuccessListener(ModifyListingActivity.this, new OnSuccessListener<Void>() {
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

    public void cancelChangesHandler(View view) {
        Boolean isEdited = false;
        for (int i = 0; i < modifyListingLayout.getChildCount(); i++) {
            if (modifyListingLayout.getChildAt(i) instanceof EditText) {
                EditText currentET = (EditText) modifyListingLayout.getChildAt(i);
                if (!currentET.getText().toString().isEmpty()) {
                    isEdited = true;
                    new AlertDialog.Builder(ModifyListingActivity.this)
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


    /**
     * Returns a boolean regarding to the form completion status.
     *
     * @return true if form is filled, otherwise false.
     */
    private boolean validateForm() {
        boolean validForm = true;
        //Loop through all children inside the layout
        for (int i = 0; i < modifyListingLayout.getChildCount(); i++) {
            //Check if current child is an EditText
            if (modifyListingLayout.getChildAt(i) instanceof EditText) {
                EditText currentET = (EditText) modifyListingLayout.getChildAt(i);
                //Check if the edit text is empty
                if (TextUtils.isEmpty(currentET.getText().toString())) {
                    //Display an error if it's empty
                    currentET.setError("This field cannot be empty!");
                    validForm = false;
                }
                //Check if the ET has been changed
                if (itemNameET.getText().toString().equals(mItemName) && itemPriceET.getText().toString().equals(mItemPrice)
                        && itemQuantityET.getText().toString().equals(mItemQuantity) && itemDescriptionET.getText().toString().equals(mItemDescription)) {
                    validForm = false;
                    Toast.makeText(ModifyListingActivity.this, "You have not made any changes.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return validForm;
    }

    /**
     * Checks if app has permission to read external storage, and requests permission if it doesn't. Otherwise, open image gallery
     *
     * @param view ImageView
     */
    //TO DO: Maybe allow users to take images using their camera?
    @OnClick({R.id.item1IV, R.id.item2IV, R.id.item3IV})
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
                            item1IV.setImageBitmap(selectedImage);
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
