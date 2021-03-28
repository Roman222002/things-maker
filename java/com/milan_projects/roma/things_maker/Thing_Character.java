package com.milan_projects.roma.things_maker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;

public class Thing_Character extends AppCompatActivity {
TextView name_th,opus,start,end,place,prise,who,kurrent;
    Button b;
    int id=0;
    final String FILENAME = "my_login";
    String login="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing__character);
        name_th=(TextView)findViewById(R.id.charakter_name);
        opus=(TextView)findViewById(R.id.charakter_opus);
        start=(TextView)findViewById(R.id.charakter_start);
        end=(TextView)findViewById(R.id.charakter_end);
        place=(TextView)findViewById(R.id.charakter_place);
        prise=(TextView)findViewById(R.id.charakter_prise);
        who=(TextView)findViewById(R.id.charakter_who);
        kurrent=(TextView)findViewById(R.id.charakter_status);
b=(Button)findViewById(R.id.button666);
        if(getIntent().getBooleanExtra("flag_f",false)){
            b.setVisibility(View.VISIBLE);
        }else{
            b.setVisibility(View.GONE);
        }
        name_th.setText(Decoder.toUTF(getIntent().getStringExtra("Thing_char_name")));
        opus.setText(Decoder.toUTF(getIntent().getStringExtra("Thing_char_opus")));
        start.setText(getIntent().getStringExtra("Thing_char_start"));
        end.setText(getIntent().getStringExtra("Thing_char_end"));
        place.setText(Decoder.toUTF(getIntent().getStringExtra("Thing_char_place")));
        prise.setText(Decoder.toUTF(getIntent().getStringExtra("Thing_char_prise")));
        id=getIntent().getIntExtra("Things_char_id",0);
        who.setText(getIntent().getStringExtra("Thing_char_who"));
        kurrent.setText(getIntent().getStringExtra("Thing_char_kurrent"));
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
    public void OnBeckClick(View view) {
        Intent i = new Intent(Thing_Character.this,Things_main.class);
        startActivity(i);
    }

    public void onAgree(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Thing_Character.this);
            builder.setTitle(getString(R.string.zagryzka))
                    .setMessage(getString(R.string.zachekaite))
                    .setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            new Thing_Character.PostAsyncTask().execute("https://things-maker.herokuapp.com/UpdateThingWho?id="+id+"&who="+readFile()+"&plice="+Decoder.toEng("none"));
        new Thing_Character.PostAsyncTask().execute("https://things-maker.herokuapp.com/UpdateThing?id="+id+"&kurrent=0");
    }
    private class PostAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            return Login_Activity.GET(urls[0]);
        }
        protected void onPostExecute(String result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Thing_Character.this);
            builder.setTitle(getString(R.string.yspih))
                    .setMessage(getString(R.string.thing_addedtolist))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.Ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent things = new Intent(Thing_Character.this, Things_main.class);
                                    startActivity(things);
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
