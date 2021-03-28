package com.milan_projects.roma.things_maker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class SendThing extends AppCompatActivity {
EditText Name;
    private static final String TITLE = "thingname";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";
    ListView list;
    String login="";
    private Integer[] image_mas = Image_mas.mas1;
    Kontakt[] kon;
    final String FILENAME = "my_login";

    String user,log="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_thing);
       Name=(EditText)findViewById(R.id.sendThing) ;
        list=(ListView)findViewById(R.id.list_kontakt);

        login=readFile();
        new SendThing.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/kontakts?login="+login);

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
    public void onSendTHing(View view) {
        if(isConnected()){
            new SendThing.HttpAsyncTask().execute("https://things-maker.herokuapp.com/findUser?login="+Name.getText().toString().trim());
        }else{
            Toast.makeText(getBaseContext(), "No connection!", Toast.LENGTH_LONG).show();
        }

    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connMgr.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public void onBackPressed(){
        openQuitDialog();
    }

    private void openQuitDialog() {
                Intent i = new Intent(SendThing.this,Things_main.class);
                startActivity(i);
    }
    public void onKreateKontakt(View view) {
        Intent i = new Intent(SendThing.this,AddKontakt.class);
        startActivity(i);
    }

    private class HttpAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            return Login_Activity.GET(urls[0]);
        }
        protected void onPostExecute(String result){
            if (result.equals("User not found")){
                Name.setError(getString(R.string.neIsnue));
            }else{
                Intent addthing = new Intent(SendThing.this,AddThings.class);
                addthing.putExtra("login",Name.getText().toString().trim());
                startActivity(addthing);
            }

        }
    }
    public class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }

        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            kon=gson.fromJson(result, Kontakt[].class);
            ArrayList<HashMap<String, Object>> catList = new ArrayList<>();
            HashMap<String, Object> hashMap;
            for(int i=0;i<kon.length;i++){
                hashMap = new HashMap<>();
                hashMap.put(TITLE, kon[i].Fk_login);
                hashMap.put(DESCRIPTION, Decoder.toUTF(kon[i].Fk_FirstName));
                hashMap.put(ICON,image_mas[kon[i].image]);
                catList.add(hashMap);
            }
            SimpleAdapter adapter = new SimpleAdapter(SendThing.this, catList,
                    R.layout.list_gird, new String[]{TITLE, DESCRIPTION, ICON},
                    new int[]{R.id.textview_title, R.id.textview_description, R.id.imageview_icon});
            list.setAdapter(adapter);
        }
    }

}
