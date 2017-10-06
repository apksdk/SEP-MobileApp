package com.riversidecorps.rebuy.models;

import java.util.ArrayList;

/**
 * Created by steven on 24/09/2017.
 */

public class Offer {
    private String mItemBuyer;
    private String mItemName;
    private Integer mItemQuantity;
    private String mItemOriginalPrice;
    private String mOfferPrice;
    private String mItemDescription;
    private ArrayList<String> mItemImages;
    private String mOfferDate;

        /* Instantiates a new Offer.*/

    public Offer() {
    }

    public Offer(String mItemName) {
        this.mItemName = mItemName;
    }

    public Offer(String mItemBuyer,String mItemName, Integer mItemQuantity ,String mItemOriginalPrice,String mOfferPrice,String mOfferDate,String mItemDescription) {
        this.mItemBuyer = mItemBuyer;
        this.mItemName = mItemName;
        this.mItemQuantity = mItemQuantity;
        this.mItemOriginalPrice = mItemOriginalPrice;
        this.mOfferPrice = mOfferPrice;
        this.mOfferDate = mOfferDate;
        this.mItemDescription = mItemDescription;
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

    public Integer getItemQuantity() {
        return mItemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        mItemQuantity = itemQuantity;
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

    public String getItemDescription() {
        return mItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        mItemDescription = itemDescription;
    }

    public String getOfferDate() {
        return mOfferDate;
    }

    public void setOfferDate(String offerDate) {
        mOfferDate = offerDate;
