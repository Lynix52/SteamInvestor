package de.stroehle.hendrik.steaminvestor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentInventory extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containers, Bundle savedInstanceState){
        return inflater.inflate(R.layout.list_single,containers,false);


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        /*
        ListView list = (ListView) findViewById(R.id.listView);
        CustomListview listviewAdapter = new CustomListview(getActivity(),realname,name,price,imageId);
        list.setAdapter(listviewAdapter);
        registerForContextMenu(list);*/
        super.onActivityCreated(savedInstanceState);
    }

}
