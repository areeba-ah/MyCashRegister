package com.example.mycashregister;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Items implements Serializable{
    String productName, productPrice, productQty;

    public Items(String productName, String productPrice, String productQty){
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nProduct Name: " + productName + "\nPrice: $" + productPrice + "\nQty: " + productQty + "\n";
    }


    public String toStringUser() {
        return "\nProduct Name: " + productName + "\nPrice: $" + productPrice + "\n";
    }
}
