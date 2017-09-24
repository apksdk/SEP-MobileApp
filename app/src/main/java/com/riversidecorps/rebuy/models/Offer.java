package com.riversidecorps.rebuy.models;

import java.util.ArrayList;

/**
 * Created by steven on 24/09/2017.
 */

public class Offer {
    private String ItemBuyer;
    private String ItemName;
    private Integer ItemQuantity;
    private Double ItemOriginalPrice;
    private Double OfferPrice;
    private String ItemDescription;
    private ArrayList<String> ItemImages;
    private Boolean ItemDeleted;
    private Boolean ItemCompleted;
    private String OfferDate;

        /* Instantiates a new Listing.*/
    public Offer() {
    }

    public Offer(String mItemName) {
        this.ItemName = mItemName;
    }

    public Offer(String mItemName, Double mItemOriginalPrice) {
        this.ItemName = mItemName;
        this.ItemOriginalPrice = mItemOriginalPrice;
    }

    public Offer(String mItemBuyer, String mItemName, Integer mItemQuantity, Double mItemOriginalPrice, Double mOfferPrice, String mItemDescription, String mOfferDate) {
        this.ItemBuyer = mItemBuyer;
        this.ItemName = mItemName;
        this.ItemQuantity = mItemQuantity;
        this.ItemOriginalPrice = mItemOriginalPrice;
        this.OfferPrice = mOfferPrice;
        this.ItemDescription = mItemDescription;
        this.ItemDeleted = false;
        this.ItemCompleted = false;
        this.OfferDate = mOfferDate;
    }

    public String getItemBuyer() {
        return ItemBuyer;
    }

    public void setItemBuyer(String IteBuyer) {
        this.ItemBuyer = ItemBuyer;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String mItemName) {
        this.ItemName = mItemName;
    }

    public Integer getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(Integer mItemQuantity) {
        this.ItemQuantity = mItemQuantity;
    }

    public Double getItemOriginalPrice() {
        return ItemOriginalPrice;
    }

    public void setItemOriginalPrice(Double mItemOriginalPrice) {
        this.ItemOriginalPrice = mItemOriginalPrice;
    }

    public Double getOfferPrice() {
        return OfferPrice;
    }

    public void setOfferPrice(Double mOfferPrice) {
        this.OfferPrice = mOfferPrice;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String mItemDescription) {
        this.ItemDescription = mItemDescription;
    }

    public Boolean getItemDeleted() {
        return ItemDeleted;
    }

    public void setmItemDeleted(Boolean ItemDeleted) {
        this.ItemDeleted = ItemDeleted;
    }

    public Boolean getmItemCompleted() {
        return ItemCompleted;
    }

    public void setmItemCompleted(Boolean mItemCompleted) {
        this.ItemCompleted = ItemCompleted;
    }

    public String getOfferDate() {
        return OfferDate;
    }

    public void setOfferDate(String mOfferDate) {
        this.OfferDate = mOfferDate;
    }
}
