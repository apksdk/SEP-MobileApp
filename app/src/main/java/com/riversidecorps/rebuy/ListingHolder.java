package com.riversidecorps.rebuy;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        Toast testToast = Toast.makeText(mItemImagePreviewIV.getContext(), "Hello!", Toast.LENGTH_LONG);
        testToast.show();
    }
}
