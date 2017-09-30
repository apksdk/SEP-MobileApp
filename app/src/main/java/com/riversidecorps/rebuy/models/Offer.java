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
    private Boolean ItemSoldout;
    private String OfferDate;

        /* Instantiates a new Offer.*/
    public Offer() {
    }

    public Offer(String mItemName) {
        this.ItemName = mItemName;
    }

    public Offer(String mItemBuyer,String mItemName,Double mItemOriginalPrice,Double mOfferPrice,String mOfferDate,String mItemDescription) {
        this.ItemBuyer = mItemBuyer;
        this.ItemName = mItemName;
        this.ItemOriginalPrice = mItemOriginalPrice;
        this.OfferPrice = mOfferPrice;
        this.OfferDate = mOfferDate;
        this.ItemDescription = mItemDescription;
        this.ItemSoldout = false;
    }

    public String getItemBuyer() {
        return ItemBuyer;
    }

    public void setItemBuyer(String itemBuyer) {
        ItemBuyer = itemBuyer;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public Integer getItemQuantity() {
        return ItemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        ItemQuantity = itemQuantity;
    }

    public Double getItemOriginalPrice() {
        return ItemOriginalPrice;
    }

    public void setItemOriginalPrice(Double itemOriginalPrice) {
        ItemOriginalPrice = itemOriginalPrice;
    }

    public Double getOfferPrice() {
        return OfferPrice;
    }

    public void setOfferPrice(Double offerPrice) {
        OfferPrice = offerPrice;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public void setItemDescription(String itemDescription) {
        ItemDescription = itemDescription;
    }

    public Boolean getItemSoldout() {
        return ItemSoldout;
    }

    public void setItemSoldout(Boolean itemSoldout) {
        ItemSoldout = itemSoldout;
    }

    public String getOfferDate() {
        return OfferDate;
    }

    public void setOfferDate(String offerDate) {
        OfferDate = offerDate;
    }
}
