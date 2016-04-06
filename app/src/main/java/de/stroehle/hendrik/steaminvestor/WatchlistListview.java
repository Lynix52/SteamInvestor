package de.stroehle.hendrik.steaminvestor;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchlistListview extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] realname;
    private final String[] name;
    private final Double[] price;
    private final Integer[] imageId;
    public WatchlistListview(Activity context, String[] realname, String[] name, Double[] price, Integer[] imageId) {
        super(context, R.layout.list_single_watchlist, name);
        this.context = context;
        this.realname = realname;
        this.name = name;
        this.price = price;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_watchlist, null, true);



        if (realname[position].equals("")){}
        else{
            TextView nameTitle = (TextView) rowView.findViewById(R.id.itemname);
            nameTitle.setText(name[position]);

            TextView priceTitle = (TextView) rowView.findViewById(R.id.price);
            priceTitle.setText(String.format("%.2f", price[position]) + "€");

            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            imageView.setImageResource(imageId[position]);
        }




        return rowView;
    }


}