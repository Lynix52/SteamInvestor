package de.stroehle.hendrik.steaminvestor;

import android.app.Activity;
import android.app.Dialog;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.os.AsyncTask;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    PreferencesUserInterface preferencesUserInterface = new PreferencesUserInterface();

    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private FragmentInventory fragmentInventory = new FragmentInventory();
    private FragmentWatchlist fragmentWatchlist = new FragmentWatchlist();

    private void addDrawerItems() {
        String[] osArray = { "Watchlist", "Inventory", "Exit"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrawerLayout mDrawerLayout;
                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.closeDrawers();
                FragmentManager fragmentManager = getSupportFragmentManager();

                //TODO fragmentWatchlist hinzufügen und hier handeln
                switch (position) {
                    case 0:
                        fragmentManager.beginTransaction().detach(fragmentInventory).commit();
                        fragmentManager.beginTransaction().attach(fragmentWatchlist).commit();
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentWatchlist).commit();
                        /*fragmentManager.beginTransaction().hide(fragmentInventory).commit();
                        fragmentManager.beginTransaction().show(fragmentWatchlist).commit();*/
                        break;
                    case 1:
                        fragmentManager.beginTransaction().detach(fragmentWatchlist).commit();
                        fragmentManager.beginTransaction().attach(fragmentInventory).commit();
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentInventory).commit();
                        /*fragmentManager.beginTransaction().hide(fragmentWatchlist).commit();
                        fragmentManager.beginTransaction().show(fragmentInventory).commit();*/
                        break;
                    case 2:
                        System.exit(0);
                        break;
                    default:
                        break;
                }


            }
        });

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO entfernen nur debug
        preferencesUserInterface.deleteItemListsAll(this);
        preferencesUserInterface.deleteSteamItemsAll(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentWatchlist).commit();
        fragmentManager.beginTransaction().show(fragmentWatchlist).commit();
        //Watchlist inizialiseiren und showen
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this,"watchlist");

            menu.setHeaderTitle(item[info.position].getItemNameReadable());
            menu.add(Menu.NONE, 0, 0, "remove");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getGroupId()){
            case 001://wenn FragmentWatchlist
                if (menuItemIndex == 0){//---wenn remove
                    preferencesUserInterface.removeSteamItemFromListByPosition(this, "watchlist", info.position);
                    RefreshFragmentWatchlist();
                }
            case 002://wenn FragmentInventory
                if (menuItemIndex == 0) {//---wenn remove
                    preferencesUserInterface.removeSteamItemFromListByPosition(this, "inventory", info.position);
                    RefreshFragmentInventory();
                }
                if (menuItemIndex == 1){//---add items
                    InventoryAddOwnPriceAndAmmount(preferencesUserInterface.getSteamItemByName(this,preferencesUserInterface.getSteamItemNameFromListByPosition(this,"inventory", info.position)));
                }

            case 003:
        }


        return true;
    }

    //TODO onclicklistener
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {


        SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this, "watchlist");

        new WatchlistPriceRefreshAssyncByName(this).execute(item[i].getItemName());

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/market/listings/730/" + item[i].getItemName()));
        startActivity(browserIntent);
    }




    public void RefreshFragmentWatchlist(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().detach(fragmentWatchlist).commit();
        fragmentManager.beginTransaction().attach(fragmentWatchlist).commit();
    }

    public void RefreshFragmentInventory(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().detach(fragmentInventory).commit();
        fragmentManager.beginTransaction().attach(fragmentInventory).commit();
    }




    public void BnWatchlistAdd(View v){
        final EditText txtSearch = new EditText(this);

        txtSearch.setHint("");

        new AlertDialog.Builder(this)
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
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];

            String search = url[0];
            String[] result_list = DataGrabber.GetItemnamesBySearching(search, 20);

            return result_list;
        }

        @Override
        protected void onPostExecute(String[] result_list) {
            if (result_list.length < 1){
                Toast toast = Toast.makeText(getApplicationContext(), "No results found :(", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            //---return wenn keine ergebnise

            WatchlistCreateSearchList(result_list);

            //new WatchlistPriceRefreshAssyncByName().execute(result_list[0]);//---strigs[0] ist name des neuen items
        }
    }

    public void WatchlistCreateSearchList(String[] result_list){
        final String[] name_list = new String[result_list.length];
        for (int i = 0; i < result_list.length; i++){
            SteamItem item = new SteamItem(result_list[i]);
            name_list[i] = item.getItemNameReadable();
        }
        //--array mit lesbaren namen erzeugen


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Results:")
                .setItems(name_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        WatchlistCreateNewListEntry(name_list[which]);
                    }
                });
        builder.show();

    }

    public void WatchlistCreateNewListEntry(String result){
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
        if (preferencesUserInterface.isItemNameInItemList(this, steamItem.getItemName(), "watchlist")){
            Toast toast = Toast.makeText(this,result_readable + " is already in the list", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //bereits existierende items nicht überschreiben
        if(!preferencesUserInterface.doesSteamItemExistByItemName(this,steamItem.getItemName())){
            preferencesUserInterface.addSteamItem(this, steamItem);
        }


        preferencesUserInterface.addSteamItemToListByItemName(this,"watchlist", result);


        new WatchlistPriceRefreshAssyncByName(this).execute(result);

        Toast toast = Toast.makeText(this, "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();
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
                        System.out.println(item[i].getCurrentPrice());
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException e){

            }

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshFragmentWatchlist();
        }

    }






    public void BnInventoryAdd(View v){
        final EditText txtSearch = new EditText(this);

        txtSearch.setHint("");

        new AlertDialog.Builder(this)
                .setTitle("Search:")
                .setMessage("")
                .setView(txtSearch)
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = txtSearch.getText().toString();
                        new InventoryAddButtonAsyncNew().execute(text);
                        //new InventoryAddButtonAsyncNew().execute(text);
                    }
                })
                .show();
    }

    public class InventoryAddButtonAsyncNew extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];

            String search = url[0];
            String[] result_list = DataGrabber.GetItemnamesBySearching(search,20);

            return result_list;
        }

        @Override
        protected void onPostExecute(String[] result_list) {
            if (result_list.length < 1){
                Toast toast = Toast.makeText(getApplicationContext(), "No results found :(", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            //---return wenn keine ergebnise

            InventoryCreateSearchList(result_list);

            //new InventoryPriceRefreshAssyncByName().execute(result_list[0]);//---strigs[0] ist name des neuen items
        }
    }

    public void InventoryCreateSearchList(String[] result_list){
        final String[] name_list = new String[result_list.length];
        for (int i = 0; i < result_list.length; i++){
            SteamItem item = new SteamItem(result_list[i]);
            name_list[i] = item.getItemNameReadable();
        }
        //--array mit lesbaren namen erzeugen


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Results:")
                .setItems(name_list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        InventoryCreateNewListEntry(name_list[which]);
                    }
                });
        builder.show();

    }

    public void InventoryCreateNewListEntry(String result){
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
        if (preferencesUserInterface.isItemNameInItemList(this, steamItem.getItemName(), "inventory")){
            Toast toast = Toast.makeText(this,result_readable + " is already in the list", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //bereits existierende items nicht überschreiben
        if(!preferencesUserInterface.doesSteamItemExistByItemName(this,steamItem.getItemName())){
            preferencesUserInterface.addSteamItem(this, steamItem);
        }

        preferencesUserInterface.addSteamItemToListByItemName(this, "inventory", result);


        InventoryAddOwnPriceAndAmmount(steamItem);


        new InventoryPriceRefreshAssyncByName(this).execute(result);

        Toast toast = Toast.makeText(this, "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();
    }

    public class InventoryPriceRefreshAssyncByName extends AsyncTask<String, Integer, String[]> {
        private Activity activity;
        public InventoryPriceRefreshAssyncByName(Activity activity){
            this.activity = activity;
        }

        @Override
        protected String[] doInBackground(String... name) {
            System.out.println("Refreshing Price");
            String[] dummy = new String[5];

            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this.activity, "inventory");
            try {
                for (int i = 0; i < item.length; i++) {
                    if(item[i].getItemName().equals(name[0])){
                        item[i].getCurrentPrice();
                        preferencesUserInterface.addSteamItem(this.activity,item[i]);
                        System.out.println(item[i].getCurrentPrice());
                    }
                }
            }
            catch (ArrayIndexOutOfBoundsException e){

            }

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshFragmentInventory();
        }

    }


    public void InventoryAddOwnPriceAndAmmount(final SteamItem steamItem){
        final Dialog d = new Dialog(MainActivity.this);
        final SteamItem steamItemFinal = steamItem;
        d.setTitle("Add Items");
        d.setContentView(R.layout.dialog_number_picker);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np1 = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np1.setMaxValue(10000);
        np1.setMinValue(0);
        np1.setValue(10);
        np1.setWrapSelectorWheel(false);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(999);
        np2.setMinValue(0);
        np2.setWrapSelectorWheel(false);
        final NumberPicker np3 = (NumberPicker) d.findViewById(R.id.numberPicker3);
        np3.setMaxValue(99);
        np3.setMinValue(0);
        np3.setWrapSelectorWheel(false);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SteamItem item = preferencesUserInterface.getSteamItemByName(getApplicationContext(),steamItemFinal.getItemName());
                item.addBoughtItems(((double)np2.getValue()*100+(double)np3.getValue())/100, np1.getValue());
                System.out.println("added " + np1.getValue() + " items for price: " + np3.getValue());
                //TODO fix value
                //TODO dialog_number_picker.xml verschönern
                preferencesUserInterface.deleteSteamItemByName(getApplicationContext(), item.getItemName());
                preferencesUserInterface.addSteamItem(getApplicationContext(), item);
                RefreshFragmentInventory();
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }




    public class PriceRefreshAssyncAll extends AsyncTask<String, Integer, String[]> {
        private Activity activity;
        public PriceRefreshAssyncAll(Activity activity){
            this.activity = activity;
        }

        @Override
        protected String[] doInBackground(String... url) {
            System.out.println("Refreshing Prices");
            String[] dummy = new String[5];

            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this.activity, "watchlist");

            for (int i = 0; i < item.length; i++) {
                item[i].getCurrentPrice();
                System.out.println(item[i].getCurrentPrice());
                preferencesUserInterface.deleteSteamItemByName(this.activity, item[i].getItemName());
                preferencesUserInterface.addSteamItem(this.activity,item[i]);
            }

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshFragmentWatchlist();
        }

    }
}




