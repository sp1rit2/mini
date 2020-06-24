package com.example.cc;

import java.util.ArrayList;
import java.util.List;

public class ItemnPrice {
    private String item,price;
    public static int count=0,checkForOrder=0;                              //you can order from 1 shop at a time
    public static List<String> x=new ArrayList<>();
    public static List<String> y=new ArrayList<>();
    String shoplist12[]=new String[100];
    public static ArrayList<String> shoplist=new ArrayList<>();
    public int tot=0;

    public ItemnPrice()
    {
    }
    public ItemnPrice(String item, String price) {
        this.item = item;
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
