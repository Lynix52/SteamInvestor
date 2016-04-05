package de.stroehle.hendrik.steaminvestor;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesDataInterface{
    private Context ActivityContext;

    private String PreferenceName;
    private String PreferenceSubName;
    private String PreferenceContent;


    public PreferencesDataInterface(Context context, String name, String subname){
        this.ActivityContext = context;

        this.PreferenceName = name;
        this.PreferenceSubName = subname;
    }


    public void addContent(String content){
        this.PreferenceContent = content;
    }

    public void deleteAll(){
        SharedPreferences mPrefs = this.ActivityContext.getSharedPreferences(this.PreferenceName, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }

    public void delete(){
        SharedPreferences mPrefs = this.ActivityContext.getSharedPreferences(this.PreferenceName, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(this.PreferenceSubName);
        editor.commit();
    }

    public void commit(){
        SharedPreferences mPrefs = this.ActivityContext.getSharedPreferences(this.PreferenceName, 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(this.PreferenceSubName, this.PreferenceContent);
        editor.commit();
    }


    public String read(){
        SharedPreferences sharedpreferences = this.ActivityContext.getSharedPreferences(this.PreferenceName, 0);
        String content = sharedpreferences.getString(this.PreferenceSubName, "");
        this.PreferenceContent = content;
        return this.PreferenceContent;
    }

}
