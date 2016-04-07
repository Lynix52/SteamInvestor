package de.stroehle.hendrik.steaminvestor;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class FragmentInventory extends Fragment implements View.OnClickListener {
    private PreferencesUserInterface preferencesUserInterface = new PreferencesUserInterface();

    private InventoryListview adInventory;
    private ListView lvInventory;

    private SteamItem[] array_item;
    private List<SteamItem> list_item = new ArrayList<SteamItem>();




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_inventory, containers, false);


        array_item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"inventory");
        for (int i = 0; i < array_item.length; i++){
            list_item.add(array_item[i]);
        }


        lvInventory = (ListView)rootView.findViewById(R.id.listView);
        adInventory = new InventoryListview(getActivity(),list_item);
        lvInventory.setAdapter(adInventory);

        FloatingActionButton bnInventoryAdd = (FloatingActionButton) rootView.findViewById(R.id.bnInventoryAdd);
        bnInventoryAdd.setOnClickListener(this);


        RefreshLvInventory();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bnInventoryAdd:
                AddSteamItem();
                break;
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"inventory");

            menu.setHeaderTitle(item[info.position].getItemNameReadable());
            menu.add(001, 0, 0, "remove");
            menu.add(001, 1, 1, "add items");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(lvInventory);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){//---wenn remove
            preferencesUserInterface.removeSteamItemFromListByPosition(getActivity(), "inventory", info.position);
            RefreshLvInventory();
        }
        else if (menuItemIndex == 1){
            InventoryAddOwnPriceAndAmmount(new SteamItem(preferencesUserInterface.getSteamItemNameFromListByPosition(getActivity(),"inventory",info.position)));
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
                        new InventoryAddButtonAsyncNew().execute(text);
                        //new InventoryAddButtonAsyncNew().execute(text);
                    }
                })
                .show();
    }

    public class InventoryAddButtonAsyncNew extends AsyncTask<String, Integer, String[]> {
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

            InventoryCreateSearchList(result_list_real);

            //new InventoryPriceRefreshAssyncByName(getActivity()).execute(result_list[0]);//---strigs[0] ist name des neuen items
        }
    }


    public void InventoryCreateSearchList(final String[][] result_list){
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
                        InventoryCreateNewListEntry(name_list[which], result_list[which][1]);
                    }
                });
        builder.show();

    }

    public void InventoryCreateNewListEntry(String result, String img_url){
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
        if (preferencesUserInterface.isItemNameInItemList(getActivity(), steamItem.getItemName(), "inventory")){
            Toast toast = Toast.makeText(getActivity(),result_readable + " is already in the list", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        //bereits existierende items nicht überschreiben
        if(!preferencesUserInterface.doesSteamItemExistByItemName(getActivity(),steamItem.getItemName())){
            preferencesUserInterface.addSteamItem(getActivity(), steamItem);
        }

        preferencesUserInterface.addSteamItemToListByItemName(getActivity(), "inventory", result);

        Toast toast = Toast.makeText(getActivity(), "Added: " + result_readable, Toast.LENGTH_SHORT);
        toast.show();

        InventoryAddOwnPriceAndAmmount(steamItem);

        new InventoryPriceRefreshAssyncByName(getActivity()).execute(result);
        new InventoryGetImgAssyncByName(getActivity()).execute(result, img_url);
    }

    public class InventoryGetImgAssyncByName extends AsyncTask<String, Integer, String[]>{
        private Activity activity;
        public InventoryGetImgAssyncByName(Activity activity){
            this.activity = activity;
        }
        @Override
        protected String[] doInBackground(String... args) {
            String[] dummy = new String[5];

            String name = args[0];
            String img_url = args[1];


            Bitmap img = DataGrabber.GetitemImageByUrl(img_url);

            new ImageSaver(getActivity()).
                    setFileName(name + ".png").
                    setDirectoryName("images").
                    save(img);

            return dummy;
        }
        @Override
        protected void onPostExecute(String[] strings){
            RefreshLvInventory();
        }

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
            RefreshLvInventory();
        }

    }


    public void InventoryAddOwnPriceAndAmmount(final SteamItem steamItem){
        final Dialog d = new Dialog(getActivity());
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
                SteamItem item = preferencesUserInterface.getSteamItemByName(getContext(),steamItemFinal.getItemName());
                item.addBoughtItems(((double)np2.getValue()*100+(double)np3.getValue())/100, np1.getValue());
                System.out.println("added " + np1.getValue() + " items for price: " + np3.getValue());
                //TODO fix value + dialog_number_picker.xml verschönern
                preferencesUserInterface.deleteSteamItemByName(getContext(), item.getItemName());
                preferencesUserInterface.addSteamItem(getContext(), item);
                RefreshLvInventory();
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

    public void RefreshLvInventory(){
        array_item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(), "inventory");
        list_item.clear();

        for (int i = 0; i < array_item.length; i++){
            list_item.add(array_item[i]);
        }

        adInventory.notifyDataSetChanged();
    }



}
