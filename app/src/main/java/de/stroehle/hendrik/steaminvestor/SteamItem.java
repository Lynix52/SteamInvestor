package de.stroehle.hendrik.steaminvestor;

import java.io.IOException;
import java.net.URLDecoder;


import java.io.UnsupportedEncodingException;



public class SteamItem {
    private String ItemName;
    private String ItemNameReadable;
    private Double CurrentPriceCached = 0.0;


    public SteamItem(String name){
        this.ItemName = name;
    }



    public double getCurrentPriceCached(){
        double price;
        if (this.CurrentPriceCached.equals(0.0)){
            price = this.getCurrentPrice();
        }
        else{
            price = this.CurrentPriceCached;
        }

        return price;
    }

    public double getCurrentPrice(){
        double price;
        try{
            price = DataGrabber.GetCurrentPriceFromItemName(this.ItemName);
            this.CurrentPriceCached = price;
        }
        catch (IOException e){
            price = 999;
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
