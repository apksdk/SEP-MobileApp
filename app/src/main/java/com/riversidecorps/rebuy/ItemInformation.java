package com.riversidecorps.rebuy;

/**
 * Created by steven on 19/09/2017.
 */

public class ItemInformation {

    private String name;
    private int quantity;
    private double price;
    private String description;
    private String sellerId;
    private String date;
    private String sellerName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public ItemInformation(String name, int quantity, double price, String description, String sellerId, String sellerName, String createDate) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.sellerId =sellerId;
        this.date=createDate;
        this.sellerName=sellerName;


    }
}
