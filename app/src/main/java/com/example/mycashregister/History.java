package com.example.mycashregister;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class History implements Serializable {

    String productName, date, productPrice, quantityPurchased;
    Double total;

    public History(String productName, String productPrice, String productQty, String date, Double total){
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantityPurchased = productQty;
        this.date = date;
        this.total = total;
    }

    @NonNull
    @Override
    public String toString() {
        return "\nDate purchased: " + date +
                "\nProduct Name: " + productName +
                "\nPrice: $" + productPrice +
                "\nQty: " + quantityPurchased +
                "\nTotal: $" + total + "\n";
    }

}
