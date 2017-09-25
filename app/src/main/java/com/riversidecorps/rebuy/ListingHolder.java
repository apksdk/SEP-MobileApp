package com.riversidecorps.rebuy;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Joshua on 9/12/2017.
 */

public class ListingHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.itemImagePreviewIV)
    ImageView mItemImagePreviewIV;

    @BindView(R.id.itemNameTV)
    TextView mItemNameTV;

    @BindView(R.id.itemPriceTV)
    TextView mItemPriceTV;

    private String mItemID;
    private String mUserID;

    public ListingHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ImageView getItemImagePreviewIV() {
        return mItemImagePreviewIV;
    }

    public void setItemImagePreviewIV(ImageView itemImagePreviewIV) {
        mItemImagePreviewIV = itemImagePreviewIV;
    }

    public TextView getItemNameTV() {
        return mItemNameTV;
    }

    public void setItemNameTV(String itemName) {
        mItemNameTV.setText(itemName);
    }

    public TextView getItemPriceTV() {
        return mItemPriceTV;
    }

    public void setItemPriceTV(String itemPrice) {
        mItemPriceTV.setText(itemPrice);
    }

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
                        Toast listingRemoved = Toast.makeText(mItemNameTV.getContext(), "Your have removed your listing.", Toast.LENGTH_LONG);
                        listingRemoved.show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public String getItemID() {
        return mItemID;
    }

    public void setItemID(String itemID) {
        mItemID = itemID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
