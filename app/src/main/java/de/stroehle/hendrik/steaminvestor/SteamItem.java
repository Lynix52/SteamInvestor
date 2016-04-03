package de.stroehle.hendrik.steaminvestor;

import java.net.URLDecoder;

import java.io.UnsupportedEncodingException;

public class SteamItem {
    public String ItemName;
    private String ItemNameReadable;


    public SteamItem(String name){
        this.ItemName = name;
    }

    public String getNameReadable(){
        if ("".equals(ItemName)){
            try {
                ItemNameReadable = URLDecoder.decode(ItemName, "UTF-8");
            }
            catch(UnsupportedEncodingException e){
                ItemNameReadable = "Failed to convert";
            }
        }
        return ItemNameReadable;
    }
}
