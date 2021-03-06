package de.stroehle.hendrik.steaminvestor;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FragmentWatchlist extends Fragment implements View.OnClickListener {
    private PreferencesUserInterface preferencesUserInterface = new PreferencesUserInterface();

    private WatchlistListview adWatchlist;
    private ListView lvWatchlist;

    private SteamItem[] array_item;
    private List<SteamItem> list_item = new ArrayList<SteamItem>();

    SwipeRefreshLayout watchlistSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_watchlist, containers, false);


        array_item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"watchlist");
        for (int i = 0; i < array_item.length; i++){
            list_item.add(array_item[i]);
        }


        lvWatchlist = (ListView)rootView.findViewById(R.id.listView);
        adWatchlist = new WatchlistListview(getActivity(),list_item);
        lvWatchlist.setAdapter(adWatchlist);


        lvWatchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemName = preferencesUserInterface.getSteamItemNameFromListByPosition(getActivity(), "watchlist", position);
                String itemUrl = "https://steamcommunity.com/market/listings/730/" + itemName;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemUrl));
                startActivity(browserIntent);

            }
        });


        FloatingActionButton bnWatchlistAdd = (FloatingActionButton) rootView.findViewById(R.id.bnWatchlistAdd);
        bnWatchlistAdd.setOnClickListener(this);

        watchlistSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.watchlistSwiperefresh);
        watchlistSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WatchlistPriceRefreshAssyncAllBySwipe(getActivity()).execute();
            }
        });

        //watchlistSwipeRefreshLayout.setRefreshing(true);
        //new WatchlistPriceRefreshAssyncAllBySwipe(getActivity()).execute();


        return rootView;
}




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bnWatchlistAdd:
                AddSteamItem();
                break;
        }
    }




    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"watchlist");

            menu.setHeaderTitle(item[info.position].getItemNameReadable());
            menu.add(001, 0, 0, "remove");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(lvWatchlist);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){//---wenn remove
            preferencesUserInterface.removeSteamItemFromListByPosition(getActivity(), "watchlist", info.position);
            RefreshLvWatchlist();
        }

        return true;
    }

    public void AddSteamItem(){
        final EditText txtSearch = new EditText(getActivity());

        txtSearch.setHint("");

        new AlertDialog.Builder(getActivity())
                .setTitle("Search:")
                .setMessage("")
                .setView(txtSearch)
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = txtSearch.getText().toString();
                        new WatchlistAddButtonAsyncNew().execute(text);
                        //new WatchlistAddButtonAsyncNew().execute(text);
                    }
                })
                .show();
    }

    public class WatchlistAddButtonAsyncNew extends AsyncTask<String, Integer, String[]> {
        private String[][] result_list_real;
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];

            String search = url[0];
            this.result_list_real = DataGrabber.GetItemnamesBySearching(search, 20);

            String[] result_list = new String[result_list_real.length];
            for (int i = 0; i < result_list_real.length; i++){
                result_list[i] = result_list_real[i][0];
            }
            return result_list;
        }
        @Override
        protected void onPostExecute(String[] result_list) {
            if (result_list.length < 1){
                Toast toast = Toast.makeText(getActivity(), "No results found :(", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            //---return wenn keine ergebnise

            WatchlistCreateSearchList(result_list_real);

            //new WatchlistPriceRefreshAssyncByName(getActivity()).execute(result_list[0]);//---strigs[0] ist name des neuen items
        }
    }


    public void WatchlistCreateSearchList(final String[][] result_list){
        final String[] name_list = new String[result_list.length];
        for (int i = 0; i < result_list.length; i++){
            SteamItem item = new SteamItem(result_list[i][0]);
            name_list[i] = item.getItemNameReadable();
        }
        //--array mit lesbaren namen erzeugen


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Results:")
                .setItems(name_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        WatchlistCreateNewListEntry(name_list[which], result_list[which][1]);
                    }
                });
        builder.show();

    }

    public void WatchlistCreateNewListEntry(String result, String img_url){
        String result_readable = result;
        try {
            result = URLEncoder.encode(result, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e){
            System.out.println("cant convert back to name");
            return;
        }

        SteamItem steamItem = new SteamItem(result);

        //return wenn item schon in liste ist
        if (preferencesUserInterface.isItemNameInItemList(getActivity(), steamItem.getItemName(), "watchlist")){
            Toast toast = Toast.makeText(getActivity(),result_readable + " is already in the list", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //bereits existierende items nicht überschreiben
        if(!preferencesUserInterface.doesSteamItemExistByItemName(getActivity(),steamItem.getItemName())){
            preferencesUserInterface.addSteamItem(getActivity(), steamItem);
        }

        preferencesUserInterface.addSteamItemToListByItemName(getActivity(), "watchlist", result);

        Toast toast = Toast.makeText(getActivity(), "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();

        //RefreshLvWatchlist();

        new WatchlistPriceRefreshAssyncByName(getActivity()).execute(result);
        new WatchlistGetImgAssyncByName(getActivity()).execute(result, img_url);
    }


    public class WatchlistGetImgAssyncByName extends AsyncTask<String, Integer, String[]>{
        private Activity activity;
        public WatchlistGetImgAssyncByName(Activity activity){
            this.activity = activity;
        }
        @Override
        protected String[] doInBackground(String... args) {
            String[] dummy = new String[5];

            String name = args[0];
            String img_url = args[1];


            Bitmap img = DataGrabber.GetitemImageByUrl(img_url);
            System.out.println("image size: " + img.getByteCount());

            new ImageSaver(getActivity()).
                    setFileName(name + ".png").
                    setDirectoryName("images").
                    save(img);

            SteamItem item = preferencesUserInterface.getSteamItemByName(this.activity, name);
            //item.setImg(img);
            preferencesUserInterface.deleteSteamItemByName(this.activity, name);
            preferencesUserInterface.addSteamItem(this.activity,item);

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshLvWatchlist();
        }

    }


    public class WatchlistPriceRefreshAssyncByName extends AsyncTask<String, Integer, String[]> {
        private Activity activity;
        public WatchlistPriceRefreshAssyncByName(Activity activity){
            this.activity = activity;
        }

        @Override
        protected String[] doInBackground(String... name) {
            System.out.println("Refreshing Price");
            String[] dummy = new String[5];

            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this.activity, "watchlist");
            try {
                for (int i = 0; i < item.length; i++) {
                    if(item[i].getItemName().equals(name[0])){
                        item[i].getCurrentPrice();
                        preferencesUserInterface.addSteamItem(this.activity,item[i]);
                        //System.out.println(item[i].getCurrentPrice());
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException e){

            }

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            //RefreshLvWatchlist();
        }

    }


    public void RefreshLvWatchlist(){
        array_item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"watchlist");
        list_item.clear();

        for (int i = 0; i < array_item.length; i++){
            list_item.add(array_item[i]);
        }

        adWatchlist.notifyDataSetChanged();
    }



    public class WatchlistPriceRefreshAssyncAllBySwipe extends AsyncTask<String, Integer, String[]> {
        private Activity activity;
        public WatchlistPriceRefreshAssyncAllBySwipe(Activity activity){
            this.activity = activity;
        }

        @Override
        protected void onPreExecute(){
            Toast toast = Toast.makeText(getActivity(), "Refresh may take a few seconds", Toast.LENGTH_LONG);
            toast.show();
        }

        @Override
        protected String[] doInBackground(String... url) {
            System.out.println("Refreshing Prices");
            String[] dummy = new String[5];

            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this.activity, "watchlist");

            for (int i = 0; i < item.length; i++) {
                if (i != 0 && i % 9 == 0){
                    try {
                        Thread.sleep(60000);
                    }
                    catch (InterruptedException e){}
                }
                item[i].getCurrentPrice();
                System.out.println(item[i].getCurrentPrice());
                preferencesUserInterface.deleteSteamItemByName(this.activity, item[i].getItemName());
                preferencesUserInterface.addSteamItem(this.activity, item[i]);
                publishProgress(i);



            }

            return dummy;
        }

        @Override
        public void onProgressUpdate(Integer... i){
            RefreshLvWatchlist();
            if (i[0] != 0 && (i[0]+1) % 9 == 0){
                Toast toast = Toast.makeText(getActivity(), "Waiting 60 secs to not ddos the server", Toast.LENGTH_LONG);
                toast.show();
            }
        }

        @Override
        protected void onPostExecute(String[] strings){
            watchlistSwipeRefreshLayout.setRefreshing(false);
            RefreshLvWatchlist();
            try {
                Toast toast = Toast.makeText(getActivity(), "Refreshed prices", Toast.LENGTH_SHORT);
                toast.show();
            }
            catch (NullPointerException e){}
        }

    }



}
