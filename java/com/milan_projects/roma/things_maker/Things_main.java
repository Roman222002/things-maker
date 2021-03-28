package com.milan_projects.roma.things_maker;

import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Things_main extends TabActivity {
String login="";
    final String FILENAME = "my_login";
int it;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things);
        login=readFile();
        TabHost tabHost = getTabHost();
        // инициализация была выполнена в getTabHost
        // метод setup вызывать не нужно

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator(getString(R.string.potochni));
        Intent intent = new Intent(this,Kurrent_things.class);
        tabSpec.setContent(intent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator(getString(R.string.provaleni));
        tabSpec.setContent(new Intent(this, Failed_things.class));
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator(getString(R.string.vilonani));
        tabSpec.setContent(new Intent(this, Made_things.class));
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag4");
        tabSpec.setIndicator(getString(R.string.zadani));
        tabSpec.setContent(new Intent(this, ZadaniThings.class));
        tabHost.addTab(tabSpec);
    }
    void initializate(){
       Intent innent = new Intent(Things_main.this,Things_main.class);
        startActivity(innent);

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
    public void onNewThing(View view) {
        Intent i = new Intent(Things_main.this,AddThings.class);
        i.putExtra("login",login);
        startActivity(i);

    }
    public void OnSendThing(View view) {
        Intent i = new Intent(Things_main.this,SendThing.class);
        startActivity(i);
    }

    public void onRefresh(View view) {
      initializate();
    }

    public void onCcreachTHing(View view) {
      Intent i = new Intent(Things_main.this,ScreathThing.class);
        startActivity(i);
    }

    public void onVihod(View view) {
        writeFile();
        Intent i = new Intent(Things_main.this,Login_Activity.class);
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

}
