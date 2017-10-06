package com.riversidecorps.rebuy.models;

import java.util.ArrayList;

/**
 * This is the listing model that contains all relevant data regarding to the listing.
 * <p>
 * Created by Joshua on 9/12/2017.
 */
public class Listing {
    private String mItemSeller;
    private String mItemName;
    private Integer mItemQuantity;
    private String mItemPrice;
    private String mItemDescription;
    private ArrayList<String> mItemImages;
    private String mItemImage;
    private Boolean mItemDeleted;
    private Boolean mItemCompleted;
    private String mCreatedDate;
    private String mItemId;
    private String mItemSellerId;
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
     * @param itemDate        the item date
     */
    public Listing(String itemSeller, String itemName, Integer itemQuantity, String itemPrice, String itemDescription, String itemDate) {
        mItemSeller = itemSeller;
        mItemName = itemName;
        mItemQuantity = itemQuantity;
        mItemPrice = itemPrice;
        mItemDescription = itemDescription;
        mItemDeleted = false;
        mItemCompleted = false;
        mCreatedDate = itemDate;
    }

    /**
     * Instantiates a new Listing. Used for MyAccountActivity's RV Listings
     *
     * @param itemName  the item name
     * @param itemPrice the item price
     * @param itemDate  the item date
     */
    public Listing(String itemName, String itemPrice, String itemDate) {
        mItemName = itemName;
        mItemPrice = itemPrice;
        mItemDeleted = false;
        mItemCompleted = false;
        mCreatedDate = itemDate;
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

    /**
     * Gets item deleted.
     *
     * @return the item deleted
     */
    public Boolean getItemDeleted() {
        return mItemDeleted;
    }

    /**
     * Sets item deleted.
     *
     * @param itemDeleted the item deleted
     */
    public void setItemDeleted(Boolean itemDeleted) {
        mItemDeleted = itemDeleted;
    }

    /**
     * Gets item completed.
     *
     * @return the item completed
     */
    public Boolean getItemCompleted() {
        return mItemCompleted;
    }

    /**
     * Sets item completed.
     *
     * @param itemCompleted the item completed
     */
    public void setItemCompleted(Boolean itemCompleted) {
        mItemCompleted = itemCompleted;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public String getCreatedDate() { return mCreatedDate; }

    /**
     * Sets created date.
     *
     * @param mCreatedDate the m created date
     */
    public void setCreatedDate(String mCreatedDate) { this.mCreatedDate = mCreatedDate; }

    /**
     * Gets item image.
     *
     * @return the item image
     */
    public String getItemImage() {
        return mItemImage;
    }

    /**
     * Sets item image.
     *
     * @param mItemImage the m item image
     */
    public void setItemImage(String mItemImage) {
        this.mItemImage = mItemImage;
    }

    /**
     * Gets item seller ID.
     *
     * @return the item seller ID
     */
    public String getItemSellerId() { return mItemSellerId; }
    public String getmItemSellerId() { return mItemSellerId; }
    /**
     * Sets item seller ID.
     *
     * @param mItemSellerId the m item seller id
     */
    public void setItemSellerId(String mItemSellerId) { this.mItemSellerId = mItemSellerId; }

    /**
     * Gets item item ID.
     *
     * @return the item ID
     */
    public String getItemId() {

        return mItemId;
    }

    /**
     * Sets item ID.
     *
     * @param mItemId the m item id
     */
    public void setItemId(String mItemId) {

        this.mItemId = mItemId;
    }

    public ArrayList<String> getItemImages() {
        return mItemImages;
    }

    public void setItemImages(ArrayList<String> mItemImages) {
        this.mItemImages = mItemImages;
    }
}
