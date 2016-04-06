package de.stroehle.hendrik.steaminvestor;

import android.content.Context;
import com.google.gson.Gson;

public class PreferencesUserInterface {

    public SteamItem[] getSteamItemArrayFromList(Context context, String listName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();

        String[] names_list_array = names_list.split(",");
        SteamItem[] steamItems = new SteamItem[names_list_array.length];

        //return wenn liste leer ist
        if (names_list_array.length == 0) {
            System.out.println("getSteamItemArrayFromList: Can't get items from an empty list");
            return steamItems;
        }

        for (int i = 0; i < names_list_array.length; i++){
            PreferencesDataInterface prefInterface = new PreferencesDataInterface(context,"steamItems",names_list_array[i]);

            Gson gson = new Gson();
            String json = prefInterface.read();
            steamItems[i] = gson.fromJson(json, SteamItem.class);
        }

        return steamItems;
    }

    public SteamItem getSteamItemByName(Context context, String itemName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"steamItems",itemName);

        Gson gson = new Gson();
        String json = preferencesDataInterface.read();

        if (json.equals("")){
            SteamItem error = new SteamItem("error");
            System.out.println("getSteamItemByName: Item does not exist");
            return error;
        }

        SteamItem steamItem = gson.fromJson(json, SteamItem.class);
        return steamItem;
    }

    public String getSteamItemNameFromListByPosition(Context context, String listName, int position){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();
        String[] names_list_array = names_list.split(",");
        if(names_list_array.length <= position){
            System.out.println("getSteamItemNameFromListByPosition: " + listName + " only has " + names_list_array.length + " positions");
            return "Out of Bound";
        }
        String name = names_list_array[position];
        return name;
    }

    public void addSteamItem(Context context, SteamItem steamItem){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"steamItems",steamItem.getItemName());
        Gson gson = new Gson();

        String json = gson.toJson(steamItem);
        preferencesDataInterface.addContent(json);
        preferencesDataInterface.commit();
    }

    public void addSteamItemToListByItemName(Context context, String listName, String itemName){
        //schauen ob das item existiert
        PreferencesDataInterface prefInterface = new PreferencesDataInterface(context,"steamItems", itemName);
        String json = prefInterface.read();
        if (json.equals("")){
            System.out.println("addSteamItemToListByItemName: can't add a non existent item to a list");
            return;
        }


        //content aus liste auslesen
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();

        //neue liste mit neuem item erzeugen
        String names_list_new = "";
        if (names_list.equals("")){
            names_list_new = itemName;
        }
        else {
            names_list_new = names_list + "," + itemName;
        }

        preferencesDataInterface.addContent(names_list_new);
        preferencesDataInterface.commit();

    }

    public void removeSteamItemFromListByPosition(Context context, String listName, int position){
        //content aus liste auslesen
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();

        //array aus liste erzeugen
        String[] names_list_array = names_list.split(",");
        String[] names_list_array_new = new String[names_list_array.length-1];

        //return wenn item nicht existiert
        if (names_list_array.length <= position){
            System.out.println("removeSteamItemFromListByPosition: " + listName + " only has " + names_list_array.length + " positions");
            return;
        }

        //neues array ohne zu löschendes item erstellen
        int j = 0;
        for (int i = 0; i < names_list_array.length; i++){
            if (i == position){
                j--;
            }
            else{
                names_list_array_new[j] = names_list_array[i];
            }
            j++;
        }

        //liste aus array erzeugen
        String names_list_new = "";
        for (int i = 0; i < names_list_array_new.length; i++){
            names_list_new = names_list_new + names_list_array_new[i];
            //wenn nicht das letzte item setze komma dahinter
            if(i < names_list_array_new.length-1){
                names_list_new = names_list_new + ",";
            }
        }
        //content setzen und daten wieder schreiben
        preferencesDataInterface.addContent(names_list_new);
        preferencesDataInterface.commit();

    }

    public void removeSteamItemFromListByItemName(Context context, String listName, String itemName){
        //content aus liste auslesen
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();

        //return wenn item nicht existiert
        if (!isItemNameInItemList(context, itemName, listName)){
            System.out.println("removeSteamItemFromListByItemName: " + itemName + " is not in " + listName);
            return;
        }

        //array aus liste erzeugen
        String[] names_list_array = names_list.split(",");
        String[] names_list_array_new = new String[names_list_array.length-1];

        //neues array ohne zu löschendes item erstellen
        int j = 0;
        for (int i = 0; i < names_list_array.length; i++){
            if (names_list_array[i].equals(itemName)){
                j--;
            }
            else{
                names_list_array_new[j] = names_list_array[i];
            }
            j++;
        }

        //liste aus array erzeugen
        String names_list_new = "";
        for (int i = 0; i < names_list_array_new.length; i++){
            names_list_new = names_list_new + names_list_array_new[i];
            //wenn nicht das letzte item setze komma dahinter
            if(i < names_list_array_new.length-1){
                names_list_new = names_list_new + ",";
            }
        }
        //content setzen und daten wieder schreiben
        preferencesDataInterface.addContent(names_list_new);
        preferencesDataInterface.commit();

    }

    public void deleteItemListByName(Context context, String listName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        preferencesDataInterface.delete();
    }

    public void deleteItemListsAll(Context context){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", "");
        preferencesDataInterface.deleteAll();
    }

    public void deleteSteamItemByName(Context context, String itemName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"steamItems", itemName);
        preferencesDataInterface.delete();
    }

    public void deleteSteamItemsAll(Context context){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"steamItems", "");
        preferencesDataInterface.deleteAll();
    }

    public boolean doesSteamItemExistByItemName(Context context, String itemName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"steamItems", itemName);
        String json = preferencesDataInterface.read();
        if (json.equals("")){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isItemNameInItemList(Context context, String itemName, String listName){
        PreferencesDataInterface preferencesDataInterface = new PreferencesDataInterface(context,"itemLists", listName);
        String names_list = preferencesDataInterface.read();

        if (names_list.contains(itemName)){
            return true;
        }
        else{
            return false;
        }
    }

}
