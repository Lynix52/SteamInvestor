package de.stroehle.hendrik.steaminvestor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InventoryListview extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] realname;
    private final String[] name;
    private final Double[] price;
    private final Integer[] imageId;
    private final Integer[] itemCountOwn;
    private final Double[] averagePriceBought;

    public InventoryListview(Activity context, String[] realname, String[] name, Double[] price, Integer[] imageId, Integer[] itemCountOwn,Double[] averagePriceBought) {
        super(context, R.layout.list_single_inventory, name);
        this.context = context;
        this.realname = realname;
        this.name = name;
        this.price = price;
        this.imageId = imageId;
        this.itemCountOwn = itemCountOwn;
        this.averagePriceBought = averagePriceBought;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_inventory, null, true);



        if (realname[position].equals("")){}
        else{
            TextView nameTitle = (TextView) rowView.findViewById(R.id.itemname);
            nameTitle.setText(itemCountOwn[position] + " " + name[position]);

            TextView priceTitle = (TextView) rowView.findViewById(R.id.price);
            priceTitle.setText(String.format("%.2f", averagePriceBought[position]) + "€" + " / " + String.format("%.2f", price[position]) + "€");

            //ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            //imageView.setImageResource(imageId[position]);
        }




        return rowView;
    }


}