package com.milan_projects.roma.things_maker;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.client.ResponseHandler;

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

public class ThingsActivity extends AppCompatActivity {
    ListView listView;
    String login;
     Things[] thing;
    static String []thingString;
    static String [] TimeString;
    private static final String TITLE = "thingname";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final String FILENAME = "my_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_things);
        //listView=(ListView)findViewById(R.id.listView);
        login=readFile();
        Toast.makeText(getBaseContext(), login, Toast.LENGTH_LONG).show();
        new ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login="+login);
        Intent intent = new Intent(ThingsActivity.this,TImeCheck.class);
        startService(intent);

    }


    public void onBackPressed(){
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(ThingsActivity.this);
        quitDialog.setTitle("Вихід?");
        quitDialog.setPositiveButton("Так", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                finish();
                Intent i = new Intent(ThingsActivity.this,Login_Activity.class);
                startActivity(i);
            }
        });
        quitDialog.setNegativeButton("Ні", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
            }
        });
        quitDialog.show();
    }

    public void onNewThing(View view) {
        Intent i = new Intent(ThingsActivity.this,AddThings.class);
        i.putExtra("login",login);
        startActivity(i);

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
    public void onCcreachTHing(View view) {
        Intent i = new Intent(ThingsActivity.this,ScreathThing.class);
        startActivity(i);
    }

    public void onVihod(View view) {
        writeFile();
        Intent i = new Intent(ThingsActivity.this,Login_Activity.class);
        startActivity(i);
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
    public void OnSendThing(View view) {
        Intent i = new Intent(ThingsActivity.this,SendThing.class);
        startActivity(i);
    }

    public void onRefresh(View view) {
        new ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login="+login);
    }

    private class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }

        protected void onPostExecute(String result) {
                Gson gson = new Gson();
                thing=gson.fromJson(result, Things[].class);
            thingString=new String [thing.length];
            TimeString= new String [thing.length];
            ArrayList<HashMap<String, Object>> catList = new ArrayList<>();
            HashMap<String, Object> hashMap;
            for(int i=0;i<thing.length;i++){
                    thingString[i]=""+thing[i].Name;
                    TimeString[i]=""+thing[i].EndData;
                hashMap = new HashMap<>();
                hashMap.put(TITLE, Decoder.toUTF(thing[i].Name));
                hashMap.put(DESCRIPTION, thing[i].EndData);
                Random random = new Random();
                int s= random.nextInt(12);
                if(s==0)s=random.nextInt(12);
                if (s==0) hashMap.put(ICON, R.drawable.image_part_001);
                if (s==1) hashMap.put(ICON, R.drawable.image_part_001);
                if (s==2) hashMap.put(ICON, R.drawable.image_part_002);
                if (s==3) hashMap.put(ICON, R.drawable.image_part_003);
                if (s==4) hashMap.put(ICON, R.drawable.image_part_004);
                if (s==5) hashMap.put(ICON, R.drawable.image_part_005);
                if (s==6) hashMap.put(ICON, R.drawable.image_part_006);
                if (s==7) hashMap.put(ICON, R.drawable.image_part_007);
                if (s==8) hashMap.put(ICON, R.drawable.image_part_008);
                if (s==9) hashMap.put(ICON, R.drawable.image_part_009);
                if (s==10) hashMap.put(ICON, R.drawable.image_part_010);
                if (s==11) hashMap.put(ICON, R.drawable.image_part_011);
                if (s==12) hashMap.put(ICON, R.drawable.image_part_012);
                catList.add(hashMap);
            }
            SimpleAdapter adapter = new SimpleAdapter(ThingsActivity.this, catList,
                    R.layout.list_gird, new String[]{TITLE, DESCRIPTION, ICON},
                    new int[]{R.id.textview_title, R.id.textview_description, R.id.imageview_icon});
            listView.setAdapter(adapter);
        }
    }



    }
