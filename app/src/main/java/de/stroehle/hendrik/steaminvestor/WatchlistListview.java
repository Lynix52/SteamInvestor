package de.stroehle.hendrik.steaminvestor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class WatchlistListview extends ArrayAdapter<SteamItem>{

    private final Activity context;


    public WatchlistListview(Activity context, List<SteamItem> list_item) {
        super(context, R.layout.list_single_watchlist,list_item);

        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single_watchlist, null, true);

        SteamItem steamItem = getItem(position);

        try {
            if (steamItem.getItemName().equals("")){}
            else{
                TextView nameTitle = (TextView) rowView.findViewById(R.id.itemname);
                nameTitle.setText(steamItem.getItemNameReadable());

                TextView priceTitle = (TextView) rowView.findViewById(R.id.price);
                priceTitle.setText(String.format("%.2f", steamItem.getCurrentPriceCached()) + "€");

                ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
                Bitmap b = new ImageSaver(getContext()).setFileName(steamItem.getItemName() + ".png").
                        setDirectoryName("images").
                        load();
                //b = new ImageSaver(getContext()).addWhiteOutline(b); //TODO weißer rand?
                imageView.setImageBitmap(b);


            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println("ArrayIndexOutOfBoundsException: " + e);
        }
        catch (NullPointerException e){
            System.out.println(" HIER!! NullPointerException: " + e);
            rowView= inflater.inflate(R.layout.list_single_empty, null, true);
        }

        return rowView;
    }



}