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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Roma on 23.12.2017.
 */

public class Made_things extends Activity{
    ListView listView;
    String login;
    Things[] thing;
    int it=0;
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
        new Made_things.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login="+login+"&place="+Decoder.toEng("none")+"&kurrent=1");
    }

    public void onBackPressed(){
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(Made_things.this);
        quitDialog.setTitle(getString(R.string.vihod));
        quitDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                finish();
                writeFile();
                Intent i = new Intent(Made_things.this,Login_Activity.class);
                startActivity(i);
            }
        });
        quitDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
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

    public class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }

        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            thing = gson.fromJson(result, Things[].class);
            ArrayList<HashMap<String, Object>> catList = new ArrayList<>();
            HashMap<String, Object> hashMap;
            for (int i = 0; i < thing.length; i++) {
                hashMap = new HashMap<>();
                hashMap.put(TITLE, Decoder.toUTF(thing[i].Name));
                hashMap.put(DESCRIPTION, thing[i].EndData);
                hashMap.put(ICON, image_mas[thing[i].image]);
                catList.add(hashMap);
            }

            SimpleAdapter adapter = new SimpleAdapter(Made_things.this, catList,
                    R.layout.list_gird, new String[]{TITLE, DESCRIPTION, ICON},
                    new int[]{R.id.textview_title, R.id.textview_description, R.id.imageview_icon});
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent1 = new Intent(Made_things.this,Thing_Character.class);
                    intent1.putExtra("Thing_char_name",thing[position].Name);
                    intent1.putExtra("Thing_char_opus",thing[position].Opus);
                    intent1.putExtra("Thing_char_prise",thing[position].Prise);
                    intent1.putExtra("Thing_char_place","45");
                    intent1.putExtra("Thing_char_kurrent",Ststus.toStat(thing[position].Kurent));
                    intent1.putExtra("Thing_char_start",thing[position].StartData);
                    intent1.putExtra("Thing_char_end",thing[position].EndData);
                    intent1.putExtra("Thing_char_who",Decoder.toUTF(thing[position].Who));
                    startActivity(intent1);
                }
            });
        }
    }}
