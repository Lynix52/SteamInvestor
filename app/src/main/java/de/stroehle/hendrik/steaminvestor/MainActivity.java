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

public class MainActivity extends AppCompatActivity {

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

                switch (position) {
                    case 0:
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentWatchlist).commit();
                        break;
                    case 1:
                        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragmentInventory).commit();
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





    //TODO move PriceRefreshAssyncAll to fragments
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
            //RefreshFragmentWatchlist();
        }

    }
}




