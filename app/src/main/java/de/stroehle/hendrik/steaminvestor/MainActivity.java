package de.stroehle.hendrik.steaminvestor;

import android.app.Dialog;

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
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private FragmentInventory fragmentInventory = new FragmentInventory();
    //TODO fragmentWatchlist hinzufügen

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
                switch (position){
                    case 0:
                        fragmentManager.beginTransaction().hide(fragmentInventory).commit();
                        break;
                    case 1:
                        fragmentManager.beginTransaction().show(fragmentInventory).commit();
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
                getSupportActionBar().setTitle("Navigation!");
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
        fragmentManager.beginTransaction().replace(R.id.relativeLayout,fragmentInventory).commit();
        fragmentManager.beginTransaction().hide(fragmentInventory).commit();
        //Inventar inizialiseiren und hiden

        //TODO fragmentWatchlist inizialisieren und hiden

        //RemoveSavedObjectsAll();//--nur zu debugzwecken

        RefreshListviewFast();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SteamItem[] item = RestoreSavedItemObjects();

            menu.setHeaderTitle(item[info.position].getItemNameReadable());
            menu.add(Menu.NONE, 0, 0, "remove");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0){//---wenn remove
            RemoveSavedObjectByPosition(info.position);
        }

        return true;
    }






    //TODO Methoden in ObjectSaver.java auslagern dass sie von den fragmenten erreichbar sind
    public  void RemoveSavedObjectsAll(){
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();

        SharedPreferences mPrefss = getSharedPreferences("ItemList", MODE_PRIVATE);
        SharedPreferences.Editor editorr = mPrefss.edit();
        editorr.putString("Names", "");
        editorr.commit();

        SteamItem[] item = RestoreSavedItemObjects();
        SaveItemObjects(item);

        RefreshListviewFast();

        System.out.println("Removed all Entries");
    }

    public void RemoveSavedObjectByPosition(int position){
        SteamItem[] array_item = RestoreSavedItemObjects();
        if (array_item.length == 1){
            RemoveSavedObjectsAll();
            return;
        }
        else {

            SteamItem[] array_new = new SteamItem[array_item.length - 1];
            int j = 0;
            for (int i = 0; i < array_new.length; i++) {
                if (i == position) {
                    j++;
                }
                array_new[i] = array_item[j];
                j++;
            }

            SaveItemObjects(array_new);

            RefreshListviewFast();
            return;
        }
    }

    public void  SaveItemObjects(SteamItem[] array_item){
        String saved_names = "";
        for (int i = 0; i < array_item.length; i++){
            if (saved_names.equals("")){
                try {
                    saved_names = array_item[i].getItemName();
                }
                catch (NullPointerException e){
                    System.out.println("Error writing data to array");
                }
            }
            else {
                try {
                    saved_names = saved_names + "," + array_item[i].getItemName();
                }
                catch (NullPointerException e){
                    System.out.println("Error writing data to array");
                }
            }
        }

        System.out.println("saving: " + saved_names);

        SharedPreferences mPrefs = getSharedPreferences("ItemList", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("Names", saved_names);
        editor.commit();
        //---save names to list

        SharedPreferences mPrefss = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefss.edit();

        prefsEditor.clear();
        prefsEditor.commit();
        //---remove all old data

        Gson gson = new Gson();

        for (int i = 0; i < array_item.length; i++) {
            String json = gson.toJson(array_item[i]);
            try {
                prefsEditor.putString(array_item[i].getItemName(), json);
                prefsEditor.commit();
            }
            catch (NullPointerException e){
                System.out.println("Error writing data to prferences");
            }
        }
        //---save concrete objects

    }

    public SteamItem[] RestoreSavedItemObjects(){
        SharedPreferences sharedpreferences = getSharedPreferences("ItemList", MODE_PRIVATE);
        String names = sharedpreferences.getString("Names", "");
        String[] array_names = names.split(",");
        //---get list with saved itemnames

        SteamItem[] item = new SteamItem[array_names.length];

        if (array_names[0].equals("")){
            item[0] = new SteamItem("ERROR");
            return item;
        }

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();


        for (int i = 0; i < array_names.length; i++){

            Gson gson = new Gson();
            String json = mPrefs.getString(array_names[i], "");
            item[i] = gson.fromJson(json, SteamItem.class);

            //System.out.println("TATATATAT:  " + item[i].getItemNameReadable());

        }
        //--restore each object from list

        return item;
    }
    //TODO --ende




    //TODO methoden eventuell auslagern verallgemeinern und ListView handel irgendwie übergeben
    public void RefreshListviewFast(){
        SteamItem[] array_item = RestoreSavedItemObjects();


        String[] realname = new String[array_item.length];
        String[] name = new String[array_item.length];
        Integer[] imageId = new Integer[array_item.length];
        Double[] price = new Double[array_item.length];

        if (array_item[0].getItemName().equals("ERROR")){
            realname[0] = "";
        }
        else {
            for (int i = 0; i < array_item.length; i++) {
                realname[i] = array_item[i].getItemName();
                //System.out.println("item:  " + realname[i]);
                name[i] = array_item[i].getItemNameReadable();
                imageId[i] = 0;
                price[i] = array_item[i].getCurrentPriceCached();
                //price[i] = 0.222;
                //---ändern wenn später beim adden der preis gecached wird
            }
        }

        ListView list = (ListView) findViewById(R.id.listView);
        CustomListview listviewAdapter = new CustomListview(this,realname,name,price,imageId);
        list.setAdapter(listviewAdapter);
        list.setOnItemClickListener(this);
        registerForContextMenu(list);
        //list.setOnContextClickListener(this);
        /*list = (ListView) findViewById(R.id.listView);
        listviewAdapter = new CustomListview(this, realname, name, price, imageId);
        list.setAdapter(listviewAdapter);
        list.setOnItemClickListener(this);*/

    }


    public class AddButtonAssyncNew extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];

            SteamItem[] array_item = RestoreSavedItemObjects();
            SteamItem[] array_new = new SteamItem[array_item.length + 1];

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

        SteamItem[] array_item = RestoreSavedItemObjects();
        SteamItem[] array_new = new SteamItem[array_item.length + 1];


        if (array_item[0].getItemName().equals("ERROR")){
            array_item[0] = new SteamItem(result);
            SaveItemObjects(array_item);
            new PriceRefreshAssyncByName().execute(result);
            Toast toast = Toast.makeText(this, "Added: " + result_readable, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        for (int i = 0; i < array_item.length; i++) {
            if (result.equals(array_item[i].getItemName())) {
                System.out.println("item ist schon in der liste");
                Toast toast = Toast.makeText(this,result_readable + " is already in your list", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        //---wenn item schon in der liste ist nichts machen

        for (int i = 0; i < array_item.length; i++) {
            array_new[i] = array_item[i];
        }

        array_new[array_new.length - 1] = new SteamItem(result);


        //---testcode hier später sufu + .getCurrentPriceCached() erstmalig machen (AssyncTask)

        for (int i = 0; i < array_new.length; i++){
            //System.out.println(i + ". peter ist:  " + array_item[i]);
            System.out.println(i + ". name ist:  " + array_new[i]);
        }

        SaveItemObjects(array_new);


        new PriceRefreshAssyncByName().execute(result);

        Toast toast = Toast.makeText(this, "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();
    }


    public class AddButtonAssync extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];


            SteamItem[] array_item = RestoreSavedItemObjects();
            SteamItem[] array_new = new SteamItem[array_item.length + 1];


            String search = url[0];
            String[] result = DataGrabber.GetItemnamesBySearching(search,5);

            System.out.println("suche: " + result.length);

            try {
                System.out.println("suche: " + result[0]);
            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("SEARCH FAILED");
                return test;
            }

            if (array_item[0].getItemName().equals("ERROR")){
                array_item[0] = new SteamItem(result[0]);
                test[0] = array_item[0].getItemName();//---itemname von neuem item wird weitergegeben
                SaveItemObjects(array_item);
                return test;
            }



            for (int i = 0; i < array_item.length; i++) {
                if (result[0].equals(array_item[i].getItemName())) {
                    System.out.println("item ist schon in der liste");
                    return test;
                }
            }
            //---wenn item schon in der liste ist nichts machen







            for (int i = 0; i < array_item.length; i++) {
                array_new[i] = array_item[i];
            }

            array_new[array_new.length - 1] = new SteamItem(result[0]);


            //---testcode hier später sufu + .getCurrentPriceCached() erstmalig machen (AssyncTask)






            for (int i = 0; i < array_new.length; i++){
                //System.out.println(i + ". peter ist:  " + array_item[i]);
                System.out.println(i + ". name ist:  " + array_new[i]);
            }

            SaveItemObjects(array_new);

            test[0] = array_new[array_new.length-1].getItemName();//---itemname von neuem item wird weitergegeben

            return test;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            new PriceRefreshAssyncByName().execute(strings[0]);//---strigs[0] ist name des neuen items
        }
    }

    public void AddButton(View v){
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


        SteamItem[] item = RestoreSavedItemObjects();

        new PriceRefreshAssyncByName().execute(item[i].getItemName());

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/market/listings/730/" + item[i].getItemName()));
        startActivity(browserIntent);

        //new GetitemImageAssync().execute(item[i].getItemName());

        //RemoveSavedObjectByPosition(i);



    }


    public void RefreshButton(View v){
        new PriceRefreshAssyncAll().execute();
    }


    public class GetitemImageAssync extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... itemname) {
            String[] dummy = new String[5];

            byte[] img_byte = DataGrabber.GetitemImageByName(itemname[0]);
            System.out.println("data: " + img_byte);

            return dummy;
        }
    }


    public class PriceRefreshAssyncAll extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            System.out.println("Refreshing Prices");
            String[] dummy = new String[5];

            SteamItem[] item = RestoreSavedItemObjects();

            for (int i = 0; i < item.length; i++) {
                item[i].getCurrentPrice();
                System.out.println(item[i].getCurrentPrice());
            }
            SaveItemObjects(item);
            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshListviewFast();
        }

    }

    public class PriceRefreshAssyncByName extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... name) {
            System.out.println("Refreshing Price");
            String[] dummy = new String[5];

            SteamItem[] item = RestoreSavedItemObjects();

            for (int i = 0; i < item.length; i++) {
                if(item[i].getItemName().equals(name[0])){
                    item[i].getCurrentPrice();
                    System.out.println(item[i].getCurrentPrice());
                }
            }
            SaveItemObjects(item);
            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshListviewFast();
        }

    }

    //TODO neue ausgelagerte methode
}




