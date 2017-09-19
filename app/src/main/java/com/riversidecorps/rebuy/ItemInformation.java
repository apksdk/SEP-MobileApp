package com.riversidecorps.rebuy;

/**
 * Created by steven on 19/09/2017.
 */

public class ItemInformation {

    public String name;
    public int quantity;
    public double price;
    public String description;

    public ItemInformation(String name, int quantity, double price, String description) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }
}
