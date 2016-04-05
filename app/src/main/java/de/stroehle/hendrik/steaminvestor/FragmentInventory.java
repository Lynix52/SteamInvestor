package de.stroehle.hendrik.steaminvestor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class FragmentInventory extends Fragment {
    private PreferencesUserInterface preferencesUserInterface = new PreferencesUserInterface();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_inventory, containers, false);


        SteamItem[] array_item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"inventory");


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


        ListView list = (ListView)rootView.findViewById(R.id.listView);
        //ListView list = (ListView)rootView.findViewById(R.id.listView);
        CustomListview listviewAdapter = new CustomListview(getActivity(),realname,name,price,imageId);
        list.setAdapter(listviewAdapter);
        registerForContextMenu(list);



        return rootView;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if(v.getId() == R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SteamItem[] item = preferencesUserInterface.getSteamItemArrayFromList(getActivity(),"inventory");

            menu.setHeaderTitle(item[info.position].getItemNameReadable());
            menu.add(002, 0, 0, "remove");
        }
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);
    }

}
