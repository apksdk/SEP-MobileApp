package com.riversidecorps.rebuy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.riversidecorps.rebuy.models.Listing;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Joshua on 9/12/2017.
 */
public class ListingPreviewHolder extends RecyclerView.ViewHolder {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @BindView(R.id.itemImagePreviewIV)
    ImageView mItemImagePreviewIV;

    @BindView(R.id.itemNameTV)
    TextView mItemNameTV;

    @BindView(R.id.itemPriceTV)
    TextView mItemPriceTV;

    private String mItemID;
    private String mUserID;

    private Context mContext;

    /**
     * Instantiates a new Listing preview holder.
     *
     * @param itemView the item view
     */
    public ListingPreviewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    /**
     * Gets item image preview iv.
     *
     * @return the item image preview iv
     */
    public ImageView getItemImagePreviewIV() {
        return mItemImagePreviewIV;
    }

    /**
     * Sets item image preview iv.
     *
     * @param itemImagePreviewIV the item image preview iv
     */
    public void setItemImagePreviewIV(ImageView itemImagePreviewIV) {
        mItemImagePreviewIV = itemImagePreviewIV;
    }

    /**
     * Gets item name tv.
     *
     * @return the item name tv
     */
    public TextView getItemNameTV() {
        return mItemNameTV;
    }

    /**
     * Sets item name tv.
     *
     * @param itemName the item name
     */
    public void setItemNameTV(String itemName) {
        mItemNameTV.setText(itemName);
    }

    /**
     * Gets item price tv.
     *
     * @return the item price tv
     */
    public TextView getItemPriceTV() {
        return mItemPriceTV;
    }

    /**
     * Sets item price tv.
     *
     * @param itemPrice the item price
     */
    public void setItemPriceTV(String itemPrice) {
        mItemPriceTV.setText(itemPrice);
    }

    /**
     * Remove listing.
     *
     * @param removeListingBTN the remove listing btn
     */
    @OnClick(R.id.removeListingBTN)
    public void removeListing(Button removeListingBTN) {
        new AlertDialog.Builder(mItemNameTV.getContext())
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to remove the selected listing?")
                .setIcon(R.drawable.ic_dialog_warning)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("Listings").child(mItemID).child("itemDeleted").setValue(true);
                        ref.child("users").child(mUserID).child("Listings").child(mItemID).child("itemDeleted").setValue(true);
                        Toast.makeText(mItemNameTV.getContext(), "Your have removed your listing.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Gets item id.
     *
     * @return the item id
     */
    public String getItemID() {
        return mItemID;
    }

    /**
     * Sets item id.
     *
     * @param itemID the item id
     */
    public void setItemID(String itemID) {
        mItemID = itemID;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserID() {
        return mUserID;
    }

    /**
     * Sets user id.
     *
     * @param mUserID the m user id
     */
    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    /**
     * Opens up the specified listing
     *
     * @param view the view
     */
    @OnClick(R.id.itemPreviewListingLayout)
    public void selectItemHandler(View view) {
        final Intent intent = new Intent(mContext, SingleListingActivity.class);

        DatabaseReference ref = mDatabase.getReference().child("Listings").child(mItemID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Listing listing = dataSnapshot.getValue(Listing.class);
                intent.putExtra("itemName", listing.getItemName());
                intent.putExtra("itemPrice", listing.getItemPrice());
                intent.putExtra("itemDes", listing.getItemDescription());
                intent.putExtra("itemQuantity", listing.getItemQuantity());
                intent.putExtra("itemUser", listing.getItemQuantity());
                intent.putExtra("image", listing.getItemQuantity());
                intent.putExtra("itemId", mItemID);
                intent.putExtra("itemSellerId", listing.getItemSellerId());
                intent.putExtra("itemImages", listing.getItemImages());
                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * @param view
     */
    @OnLongClick(R.id.itemPreviewListingLayout)
    public boolean longClickHandler(View view) {
        CharSequence entryOptions[] = {"Modify Listing"};
        new AlertDialog.Builder(mContext)
                .setTitle("Select an Option")
                .setItems(entryOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent modifyListingIntent = new Intent(mContext, ModifyListingActivity.class);
                            modifyListingIntent.putExtra("itemID", mItemID);
                            mContext.startActivity(modifyListingIntent);
                        }
                    }
                })
                .show();
        return false;
    }
}
