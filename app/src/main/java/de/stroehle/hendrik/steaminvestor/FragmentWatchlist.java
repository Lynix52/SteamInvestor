package de.stroehle.hendrik.steaminvestor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class FragmentWatchlist extends Fragment {
    private PreferencesUserInterface preferencesUserInterface = new PreferencesUserInterface();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_watchlist, containers, false);


        SteamItem[] steamItem = preferencesUserInterface.getSteamItemArrayFromList(getActivity(), "watchlist");

        String[] realname = new String[steamItem.length];
        String[] name = new String[steamItem.length];
        Double[] price = new Double[steamItem.length];
        Integer[] imageId = new Integer[steamItem.length];

        for (int i = 0; i < steamItem.length; i++){
            realname[i] = steamItem[i].getItemName();
            name[i] = steamItem[i].getItemNameReadable();
            price[i] = 99.4;
            imageId[i] = 0;
        }

        ListView list = (ListView)rootView.findViewById(R.id.listView);
        CustomListview listviewAdapter = new CustomListview(getActivity(),realname,name,price,imageId);
        list.setAdapter(listviewAdapter);
        registerForContextMenu(list);


        return rootView;


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){




        super.onActivityCreated(savedInstanceState);
    }

}
