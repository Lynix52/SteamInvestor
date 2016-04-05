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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.os.AsyncTask;
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
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentWatchlist).commit();
                        fragmentManager.beginTransaction().hide(fragmentInventory).commit();
                        fragmentManager.beginTransaction().show(fragmentWatchlist).commit();
                        getSupportActionBar().setTitle("Watchlist");
                        break;
                    case 1:
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentInventory).commit();
                        fragmentManager.beginTransaction().hide(fragmentWatchlist).commit();
                        fragmentManager.beginTransaction().show(fragmentInventory).commit();
                        getSupportActionBar().setTitle("Inventory");
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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.relativeLayout,fragmentWatchlist).commit();
        fragmentManager.beginTransaction().show(fragmentWatchlist).commit();
        getSupportActionBar().setTitle("Watchlist");
        //Watchlist inizialiseiren und showen

        //TODO fragmentWatchlist inizialisieren und hiden

        //RemoveSavedObjectsAll();//--nur zu debugzwecken

        RefreshListviewFast();
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
        if (menuItemIndex == 0){//---wenn remove
            preferencesUserInterface.removeSteamItemFromListByPosition(this, "watchlist", info.position);
            RefreshListviewFast();
        }

        return true;
    }



    //TODO methoden eventuell auslagern verallgemeinern und ListView handel irgendwie übergeben
    public void RefreshListviewFast(){
        SteamItem[] array_item = preferencesUserInterface.getSteamItemArrayFromList(this,"watchlist");


        String[] realname = new String[array_item.length];
        String[] name = new String[array_item.length];
        Integer[] imageId = new Integer[array_item.length];
        Double[] price = new Double[array_item.length];

        try{
            for (int i = 0; i < array_item.length; i++) {
                System.out.println("spam");
                realname[i] = array_item[i].getItemName();
                //System.out.println("item:  " + realname[i]);
                name[i] = array_item[i].getItemNameReadable();
                imageId[i] = 0;
                price[i] = array_item[i].getCurrentPriceCached();
                //price[i] = 0.222;
                //---ändern wenn später beim adden der preis gecached wird
            }
        }
        catch (NullPointerException e){
            realname[0] = "";
        }

        ListView list = (ListView) findViewById(R.id.listView);
        CustomListview listviewAdapter = new CustomListview(this,realname, name, price, imageId);
        list.setAdapter(listviewAdapter);
        list.setOnItemClickListener(this);
        registerForContextMenu(list);
    }


    public class AddButtonAssyncNew extends AsyncTask<String, Integer, String[]> {
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

            CreateSearchList(result_list);

            //new PriceRefreshAssyncByName().execute(result_list[0]);//---strigs[0] ist name des neuen items
        }
    }


    public void CreateSearchList(String[] result_list){
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
                        CreateNewListEntry(name_list[which]);
                    }
                });
        builder.show();

    }

    public void CreateNewListEntry(String result){
        String result_readable = result;
        try {
            result = URLEncoder.encode(result, "UTF-8").replace("+", "%20");
        }
        catch (UnsupportedEncodingException e){
            System.out.println("cant convert back to name");
            return;
        }

        SteamItem steamItem = new SteamItem(result);

        preferencesUserInterface.addSteamItem(this,steamItem);
        preferencesUserInterface.addSteamItemToListByItemName(this,"watchlist", result);


        new PriceRefreshAssyncByName(this).execute(result);

        Toast toast = Toast.makeText(this, "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();
    }


    public void BnMainAdd(View v){
        final EditText txtSearch = new EditText(this);

        txtSearch.setHint("");

        new AlertDialog.Builder(this)
                .setTitle("Search:")
                .setMessage("")
                .setView(txtSearch)
                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = txtSearch.getText().toString();
                        new AddButtonAssyncNew().execute(text);
                        //new AddButtonAssyncNew().execute(text);
                    }
                })
                .show();





    }

    //TODO --ende

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {


        SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(this,"watchlist");

        new PriceRefreshAssyncByName(this).execute(item[i].getItemName());

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/market/listings/730/" + item[i].getItemName()));
        startActivity(browserIntent);
    }


    public void RefreshButton(View v){
        new PriceRefreshAssyncAll(this).execute();
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
            RefreshListviewFast();
        }

    }

    public class PriceRefreshAssyncByName extends AsyncTask<String, Integer, String[]> {
        private Activity activity;
        public PriceRefreshAssyncByName(Activity activity){
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
            RefreshListviewFast();
        }

    }

    //TODO neue ausgelagerte methode
}




