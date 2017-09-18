package com.riversidecorps.rebuy.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is the listing model that contains all relevant data regarding to the listing.
 * <p>
 * Created by Joshua on 9/12/2017.
 */
public class Listing {
    private String mItemID;
    private String mItemSeller;
    private String mItemName;
    private Integer mItemQuantity;
    private String mItemPrice;
    private String mItemDescription;
    private ArrayList<String> mItemImages;
    private Boolean mItemDeleted;
    private Boolean mItemCompleted;
    private Date mCreatedDate;


    /**
     * Instantiates a new Listing.
     * <p>
     * Firebase requires a blank model so do not remove this.
     */
    public Listing() {

    }

    /**
     * Instantiates a new Listing.
     *
     * @param itemSeller      the item seller
     * @param itemName        the item name
     * @param itemQuantity    the item quantity
     * @param itemPrice       the item price
     * @param itemDescription the item description
     */
    public Listing(String itemSeller, String itemName, Integer itemQuantity, String itemPrice, String itemDescription, Date itemDate) {
        mItemSeller = itemSeller;
        mItemName = itemName;
        mItemQuantity = itemQuantity;
        mItemPrice = itemPrice;
        mItemDescription = itemDescription;
        mItemDeleted = false;
        mItemCompleted = false;
        mCreatedDate=itemDate;
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
     * @param itemID the m item id
     */
    public void setItemID(String itemID) {
        mItemID = itemID;
    }

    /**
     * Gets item seller.
     *
     * @return the item seller
     */
    public String getItemSeller() {
        return mItemSeller;
    }

    /**
     * Sets item seller.
     *
     * @param itemSeller the m item seller
     */
    public void setItemSeller(String itemSeller) {
        mItemSeller = itemSeller;
    }

    /**
     * Gets item name.
     *
     * @return the item name
     */
    public String getItemName() {
        return mItemName;
    }

    /**
     * Sets item name.
     *
     * @param itemName the m item name
     */
    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    /**
     * Gets item quantity.
     *
     * @return the item quantity
     */
    public Integer getItemQuantity() {
        return mItemQuantity;
    }

    /**
     * Sets item quantity.
     *
     * @param itemQuantity the m item quantity
     */
    public void setItemQuantity(Integer itemQuantity) {
        mItemQuantity = itemQuantity;
    }

    /**
     * Gets item price.
     *
     * @return the item price
     */
    public String getItemPrice() {
        return mItemPrice;
    }

    /**
     * Sets item price.
     *
     * @param itemPrice the m item price
     */
    public void setItemPrice(String itemPrice) {
        mItemPrice = itemPrice;
    }

    /**
     * Gets item description.
     *
     * @return the item description
     */
    public String getItemDescription() {
        return mItemDescription;
    }

    /**
     * Sets item description.
     *
     * @param itemDescription the m item description
     */
    public void setItemDescription(String itemDescription) {
        mItemDescription = itemDescription;
    }

    public Boolean getItemDeleted() {
        return mItemDeleted;
    }

    public void setItemDeleted(Boolean itemDeleted) {
        mItemDeleted = itemDeleted;
    }

    public Boolean getItemCompleted() {
        return mItemCompleted;
    }

    public void setItemCompleted(Boolean itemCompleted) {
        mItemCompleted = itemCompleted;
    }

    public Date getmCreatedDate() { return mCreatedDate; }

    public void setmCreatedDate(Date mCreatedDate) { this.mCreatedDate = mCreatedDate; }
}
