package com.riversidecorps.rebuy.models;

import java.util.ArrayList;

/**
 * Created by steven on 24/09/2017.
 */

public class Offer {
    private String mOfferID;
    private String mItemBuyer;
    private String mItemName;
    private String mOfferQuantity;
    private String mItemOriginalPrice;
    private String mOfferPrice;
    private String mOfferDescription;
    private ArrayList<String> mItemImages;
    private Boolean mOfferDeleted;
    private Boolean mOfferCompleted;
    private String mOfferDate;
    private String mItemSeller;

        /* Instantiates a new Offer.*/

    public Offer() {
    }

    public Offer(String mItemName) {
        this.mItemName = mItemName;
    }

    public Offer(String mItemBuyer, String mItemName, String mOfferQuantity, String mItemOriginalPrice, String mOfferPrice, String mOfferDate, String mOfferDescription, String mItemSeller) {
        this.mItemBuyer = mItemBuyer;
        this.mItemName = mItemName;
        this.mOfferQuantity = mOfferQuantity;
        this.mItemOriginalPrice = mItemOriginalPrice;
        this.mOfferPrice = mOfferPrice;
        this.mOfferDate = mOfferDate;
        this.mOfferDescription = mOfferDescription;
        this.mItemSeller = mItemSeller;
        mOfferDeleted = false;
        mOfferCompleted = false;
    }

    public String getItemBuyer() {
        return mItemBuyer;
    }

    public void setItemBuyer(String itemBuyer) {
        mItemBuyer = itemBuyer;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public String getOfferQuantity() {
        return mOfferQuantity;
    }

    public void setOfferQuantity(String offerQuantity) {
        mOfferQuantity = offerQuantity;
    }

    public String getItemOriginalPrice() {
        return mItemOriginalPrice;
    }

    public void setItemOriginalPrice(String itemOriginalPrice) {
        mItemOriginalPrice = itemOriginalPrice;
    }

    public String getOfferPrice() {
        return mOfferPrice;
    }

    public void setOfferPrice(String offerPrice) {
        mOfferPrice = offerPrice;
    }

    public String getOfferDescription() {
        return mOfferDescription;
    }

    public void setOfferDescription(String offerDescription) {
        mOfferDescription = offerDescription;
    }

    /**
     * Gets item deleted.
     *
     * @return the item deleted
     */
    public Boolean getOfferDeleted() {
        return mOfferDeleted;
    }

    /**
     * Sets item deleted.
     *
     * @param offerDeleted the item deleted
     */
    public void setOfferDeleted(Boolean offerDeleted) {
        mOfferDeleted = offerDeleted;
    }

    /**
     * Gets item completed.
     *
     * @return the item completed
     */
    public Boolean getOfferCompleted() {
        return mOfferCompleted;
    }

    /**
     * Sets item completed.
     *
     * @param offerCompleted the item completed
     */
    public void setOfferCompleted(Boolean offerCompleted) {
        mOfferCompleted = offerCompleted;
    }

    public String getOfferDate() {
        return mOfferDate;
    }

    public void setOfferDate(String offerDate) {
        mOfferDate = offerDate;
    }

    public void setOfferID(String mOfferID) {
        this.mOfferID = mOfferID;
    }

    public String getOfferID() {
        return mOfferID;
    }

    public String getSellerId() {
        return mItemSeller;
    }

    public void setSellerId(String mListingId) {
        this.mItemSeller = mListingId;
    }
}
