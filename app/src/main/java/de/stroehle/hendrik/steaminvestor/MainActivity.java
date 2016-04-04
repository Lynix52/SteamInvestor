package de.stroehle.hendrik.steaminvestor;

//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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

    public void RemoveSavedObjectByPosition(int position){
        SteamItem[] array_item = RestoreSavedItemObjects();
        System.out.println("hier: " + array_item.length);
        if (array_item.length == 1){
            RemoveSavedObjectsAll();
            return;
        }
        else {

            System.out.println("hier erst recht: " + array_item.length);

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
                System.out.println("länge: " + array_item.length);
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
            System.out.println(i + " . GOTCHA: " + json);
            item[i] = gson.fromJson(json, SteamItem.class);
            System.out.println(i + " . ITEM GOTCHA: " + item[i].getItemNameReadable());

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

    public class AddButtonAssync extends AsyncTask<String, Integer, String[]> {
        @Override
        protected String[] doInBackground(String... url) {
            String[] test = new String[3];


            SteamItem[] array_item = RestoreSavedItemObjects();
            SteamItem[] array_new = new SteamItem[array_item.length + 1];


            String search = url[0];
            String[] result = DataGrabber.GetItemnamesBySearching(search,1);

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



            return test;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            RefreshListviewFast();
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
                        String url = txtSearch.getText().toString();
                        new AddButtonAssync().execute(url);
                    }
                })
                //.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                //    public void onClick(DialogInterface dialog, int whichButton) {
                //    }
                //})
                .show();
    }

    public void DeleteButton(View v){
        RemoveSavedObjectsAll();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        System.out.println("Clicked position: " + i);

        RemoveSavedObjectByPosition(i);


    }


}




