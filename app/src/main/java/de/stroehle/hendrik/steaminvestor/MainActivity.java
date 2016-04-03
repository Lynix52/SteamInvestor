package de.stroehle.hendrik.steaminvestor;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView l;
    String[] days={"Operation Breakout Weapon Case","CS:GO Weapon Case","CS:GO Weapon Case 2","CS:GO Weapon Case 3","Chroma Case","Phoenix Case","Huntsman Case","Operation Vanguard Weapon Case"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l= (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,days);
        l.setAdapter(adapter);
        l.setOnItemClickListener(this);
        //setContentView(R.layout.list_single);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        /*TextView temp= (TextView) view;
        i=i+1;
        Toast.makeText(this,temp.getText()+"  | "+i, Toast.LENGTH_SHORT).show();*/
        test();


    }

    public void test(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        SteamItem Item1 = new SteamItem("PP-Bizon%20%7C%20Blue%20Streak%20%28Battle-Scarred%29");
        double price1 = Item1.getCurrentPriceCached();
        Toast.makeText(this, "price:   " + price1, Toast.LENGTH_SHORT).show();

    }


}




