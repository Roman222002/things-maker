package com.milan_projects.roma.things_maker;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Roma on 23.12.2017.
 */

public class Kurrent_things extends Activity{
    ListView listView;
    String login;
    Things[] thing;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    int it=-1;
    static String []thingString;
    static String [] TimeString;
    private static final String TITLE = "thingname";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    private Integer[] image_mas = Image_mas.mas;
    final String FILENAME = "my_login";
    boolean flag=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_things);
        listView=(ListView)findViewById(R.id.current_list);
        login=readFile();
        new ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login="+login+"&place="+Decoder.toEng("none")+"&kurrent=0");
        Intent intent = new Intent(Kurrent_things.this,TImeCheck.class);
        startService(intent);

}
public void openDialog(){
    AlertDialog.Builder quitDialog = new AlertDialog.Builder(Kurrent_things.this);
    quitDialog.setTitle(getString(R.string.vikonali)+Decoder.toUTF(thing[it].Name)+"?");
    quitDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int which){
           new ThingsAnnyTask().execute("https://things-maker.herokuapp.com/UpdateThing?id="+thing[it].Id+"&kurrent=1");
            writeFile(-1);
        }
    });
    quitDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
        public void onClick(DialogInterface dialog, int which){
            new ThingsAnnyTask().execute("https://things-maker.herokuapp.com/UpdateThing?id="+thing[it].Id+"&kurrent=2");
            writeFile(-1);
        }
    });
    quitDialog.show();
}

    public void onBackPressed(){
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(Kurrent_things.this);
        quitDialog.setTitle("Вихід?");
        quitDialog.setPositiveButton("Так", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                finish();
                writeFile();
                Intent i = new Intent(Kurrent_things.this,Login_Activity.class);
                startActivity(i);
            }
        });
        quitDialog.setNegativeButton("Ні", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
            }
        });
        quitDialog.show();
    }
    void writeFile() {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            bw.write("");
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    void writeFile(int it) {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput("Id", MODE_PRIVATE)));
            bw.write(Integer.toString(it));
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readId() {
        String res="";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput("Id")));
            // читаем содержимое
            String str = "";
                while ((str = br.readLine()) != null) {
                    res += "" + str;
                }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } return res;
    }

    public class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }

        protected void onPostExecute(String result) {
            if (result.equals("Ok")) {
            } else {
                Gson gson = new Gson();
                thing = gson.fromJson(result, Things[].class);

                String t = readId();
                if((t.equals(""))||(t.equals(" "))||(t==null)) {
                    writeFile(-1);
                }else{
                    it = Integer.parseInt(t);
                }
                if(it!=-1){
                    openDialog();
                }else{
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
                            if (time<=now) {
                                new Kurrent_things.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/UpdateThing?id="+thing[i].Id+"&kurrent=2");
                                Intent it = new Intent(Kurrent_things.this,Things_main.class);
                                startActivity(it);
                            }
                        }}
                }

                thingString = new String[thing.length];
                TimeString = new String[thing.length];
                ArrayList<HashMap<String, Object>> catList = new ArrayList<>();
                HashMap<String, Object> hashMap;
                for (int i = 0; i < thing.length; i++) {
                    thingString[i] = "" + thing[i].Name;
                    TimeString[i] = "" + thing[i].EndData;
                    hashMap = new HashMap<>();
                    hashMap.put(TITLE, Decoder.toUTF(thing[i].Name));
                    hashMap.put(DESCRIPTION, thing[i].EndData);
                    hashMap.put(ICON, image_mas[thing[i].image]);
                    catList.add(hashMap);
                }

                SimpleAdapter adapter = new SimpleAdapter(Kurrent_things.this, catList,
                        R.layout.list_gird, new String[]{TITLE, DESCRIPTION, ICON},
                        new int[]{R.id.textview_title, R.id.textview_description, R.id.imageview_icon});
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent1 = new Intent(Kurrent_things.this,Thing_Character.class);
                        intent1.putExtra("Thing_char_name",thing[position].Name);
                        intent1.putExtra("Thing_char_opus",thing[position].Opus);
                        intent1.putExtra("Thing_char_prise",thing[position].Prise);
                        intent1.putExtra("Thing_char_place","45");
                        intent1.putExtra("Thing_char_start",thing[position].StartData);
                        intent1.putExtra("Thing_char_end",thing[position].EndData);
                        intent1.putExtra("Thing_char_kurrent",Ststus.toStat(thing[position].Kurent));
                        intent1.putExtra("Thing_char_who",Decoder.toUTF(thing[position].Who));
                        startActivity(intent1);
                    }
                });
            }
        }
    }
}
