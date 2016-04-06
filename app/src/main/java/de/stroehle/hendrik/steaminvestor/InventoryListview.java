package de.stroehle.hendrik.steaminvestor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InventoryListview extends ArrayAdapter<SteamItem>{

    private final Activity context;


    public InventoryListview(Activity context, List<SteamItem> list_item) {
        super(context, R.layout.list_single_inventory,list_item);

        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_inventory, null, true);

        SteamItem steamItem = getItem(position);
        System.out.println(steamItem);

        try {
            if (steamItem.getItemName().equals("")){}
            else{
                TextView nameTitle = (TextView) rowView.findViewById(R.id.itemname);
                nameTitle.setText(steamItem.getCountBoughtItems() + "   " + steamItem.getItemNameReadable());

                TextView priceTitle = (TextView) rowView.findViewById(R.id.price);
                priceTitle.setText(String.format("%.2f", steamItem.getCurrentPriceCached()) + "€");
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("ArrayIndexOutOfBoundsException: " + e);
        }
        catch (NullPointerException e){
            System.out.println("NullPointerException: " + e);
            rowView= inflater.inflate(R.layout.list_single_empty, null, true);
        }

        return rowView;
    }


}