package com.milan_projects.roma.things_maker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ScreathThing extends AppCompatActivity {
    ListView listView;
    String login,place;
    Things[] thing;
    EditText et;
    TextView t;
    private static final String TITLE = "thingname";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer[] image_mas = Image_mas.mas;
    final String FILENAME = "my_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screath_thing);
        listView=(ListView)findViewById(R.id.list_screath);
        login=readFile();
        et=(EditText)findViewById(R.id.screatcEt);
        t=(TextView)findViewById(R.id.text_scraetc_view);

    }
    private String readFile() {
        String res="";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            // читаем содержимое
            String str = "";
            while ((str = br.readLine()) != null) {
                res+=""+str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } return res;
    }
    public void OnScreatch(View view) {
        place = et.getText().toString().trim();
        new ScreathThing.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login=screath"+"&place="+Decoder.toEng(place)+"&kurrent=4");
        t.setText("Шукаєм...");
    }

    public void onNewThingScreatch(View view) {
        Intent i =new Intent(ScreathThing.this,AddThings.class);
        i.putExtra("flag_s",true);
        startActivity(i);
    }

    public class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }

        protected void onPostExecute(String result) {
            if(result.equals("Ok")){

            }else{
            Gson gson = new Gson();
            if(result.equals("[]")){
                t.setText(getString(R.string.neZnadeno));
            }else {
                t.setText(getString(R.string.Znadeno));
            }
            thing=gson.fromJson(result, Things[].class);
            long now = System.currentTimeMillis();
                long time=0;
                if(thing!=null){
                for(int i=0;i<thing.length;i++) {
                    try {
                        Date nows=format.parse(thing[i].EndData);
                         time=nows.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (time<now) {
                        new ScreathThing.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/UpdateThing?id="+thing[i].Id+"&kurrent=2");
                        Intent it = new Intent(ScreathThing.this,ScreathThing.class);
                        startActivity(it);
                    }
                }}
            ArrayList<HashMap<String, Object>> catList = new ArrayList<>();
            HashMap<String, Object> hashMap;
            for(int i=0;i<thing.length;i++){
                hashMap = new HashMap<>();
                hashMap.put(TITLE, Decoder.toUTF(thing[i].Name));
                hashMap.put(DESCRIPTION, Decoder.toUTF(thing[i].Who));
                hashMap.put(ICON,image_mas[thing[i].image]);
                catList.add(hashMap);
            }
            SimpleAdapter adapter = new SimpleAdapter(ScreathThing.this, catList,
                    R.layout.list_gird, new String[]{TITLE, DESCRIPTION, ICON},
                    new int[]{R.id.textview_title, R.id.textview_description, R.id.imageview_icon});
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent1 = new Intent(ScreathThing.this,Thing_Character.class);
                    intent1.putExtra("Thing_char_name",thing[position].Name);
                    intent1.putExtra("flag_f",true);
                    intent1.putExtra("Thing_char_opus",thing[position].Opus);
                    intent1.putExtra("Thing_char_prise",thing[position].Prise);
                    intent1.putExtra("Thing_char_place",thing[position].Plice);
                    intent1.putExtra("Thing_char_start",thing[position].StartData);
                    intent1.putExtra("Thing_char_end",thing[position].EndData);
                    intent1.putExtra("Things_char_id",thing[position].Id);
                    intent1.putExtra("Thing_char_kurrent",getString(R.string.posuk));
                    intent1.putExtra("Thing_char_who",Decoder.toUTF(thing[position].Who));
                    startActivity(intent1);
                }
            });
        }}
    }
}
