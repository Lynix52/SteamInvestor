package de.stroehle.hendrik.steaminvestor;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.os.AsyncTask;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView list;
    CustomListview listviewAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RemoveSavedObjectsAll();
        //--nur zu debugzwecken

        RefreshListviewFast();


    }


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

    public void  SaveItemObjects(SteamItem[] array_item){
        String saved_names = "";
        for (int i = 0; i < array_item.length; i++){
            if (saved_names.equals("")){
                saved_names = array_item[i].getItemName();
            }
            else {
                saved_names = saved_names + "," + array_item[i].getItemName();
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
        Gson gson = new Gson();

        for (int i = 0; i < array_item.length; i++) {
            String json = gson.toJson(array_item[i]);
            prefsEditor.putString(array_item[i].getItemName(), json);
            prefsEditor.commit();
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
                System.out.println("item:  " + realname[i]);
                name[i] = array_item[i].getItemNameReadable();
                imageId[i] = 0;
                //price[i] = array_item[i].getCurrentPriceCached();
                price[i] = 0.222;
                //---ändern wenn später beim adden der preis gecached wird
            }
        }
        list = (ListView) findViewById(R.id.listView);
        listviewAdapter = new CustomListview(this, realname, name, price, imageId);
        list.setAdapter(listviewAdapter);
        list.setOnItemClickListener(this);

    }

    public void AddButton(View v){

        SteamItem[] array_item = RestoreSavedItemObjects();

        if (array_item[0].getItemName().equals("ERROR")){
            SteamItem[] array_new = new SteamItem[array_item.length];
            for (int i = 0; i < array_item.length; i++) {
                array_new[i] = array_item[i];
            }

            array_new[array_new.length - 1] = new SteamItem("TESTWAFFE%20%7C%20Blue%20Streak%20%28Well-Worn%29");
            //---testcode hier später sufu + .getCurrentPriceCached() erstmalig machen (AssyncTask)

            SaveItemObjects(array_new);

            RefreshListviewFast();
        }
        else {

            SteamItem[] array_new = new SteamItem[array_item.length + 1]; //---array ist eins größer als anderes array
            for (int i = 0; i < array_item.length; i++) {
                array_new[i] = array_item[i];
            }

            array_new[array_new.length - 1] = new SteamItem("TESTWAFFE%20%7C%20Blue%20Streak%20%28Well-Worn%29");
            //---testcode hier später sufu + .getCurrentPriceCached() erstmalig machen (AssyncTask)

            SaveItemObjects(array_new);

            RefreshListviewFast();
        }



    }

    public void DeleteButton(View v){

        RemoveSavedObjectsAll();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

       // test(i);


    }

   /* public void test(int position){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        SteamItem Item1 = new SteamItem(realname[position]);
        double price1 = Item1.getCurrentPriceCached();
        Toast.makeText(this, Item1.getItemNameReadable() + " price:   " + price1 + "€", Toast.LENGTH_SHORT).show();

    }*/



   /* public class RefreshPricesAssync extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            for (int i = 0; realname.length > i; i++){
                SteamItem Item = new SteamItem(realname[i]);
                price[i] = Item.getCurrentPriceCached();
            }
            return name;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            System.out.println("Downloads finished");
            listviewAdapter.notifyDataSetChanged();
        }
    }*/

}




