package de.stroehle.hendrik.steaminvestor;

import android.provider.ContactsContract;

import java.io.IOException;
import java.net.URLDecoder;


import java.io.UnsupportedEncodingException;



public class SteamItem {
    private String ItemName;
    private String ItemNameReadable;
    private Double CurrentPriceCached = 0.0;

    private int ItemCountOwn = 0;
    private double AveragePriceBought = 0.0;

    private byte[] img;



    public SteamItem(String name){
        this.ItemName = name;
    }


    public void setImg(byte[] img_set){
        this.img = img_set;
    }

    public byte[] getImg(){
        return this.img;
    }


    //TODO fix method and try it out
    public void addBoughtItems(double price, int count){
        double price_old = this.AveragePriceBought;
        int count_old = this.ItemCountOwn;
        this.AveragePriceBought = ((count_old*price_old + count*price) / (count_old + count));
        this.ItemCountOwn = count_old + count;
    }

    public int getCountBoughtItems(){return this.ItemCountOwn;}

    public double getAveragePriceBoughtItem(){return this.AveragePriceBought;}


    public double getCurrentPriceCached(){
        double price;
        price = this.CurrentPriceCached;

        return price;
    }

    public double getCurrentPrice(){
        double price;
        try{
            price = DataGrabber.GetCurrentPriceFromItemName(ItemName);
            this.CurrentPriceCached = price;
        }
        catch (IOException e){
            price = 999;
            System.out.println("IOExeption: " + e);
        }
        return price;
    }


    public String getItemName(){
        return this.ItemName;
    }


    public String getItemNameReadable(){
        try {
            ItemNameReadable = URLDecoder.decode(ItemName, "UTF-8");
        }
        catch(UnsupportedEncodingException e){
            ItemNameReadable = "Failed to convert";
        }
        return ItemNameReadable;
    }
}
