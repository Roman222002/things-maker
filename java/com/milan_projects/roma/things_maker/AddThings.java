package com.milan_projects.roma.things_maker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AddThings extends AppCompatActivity {
    Calendar dateAndTime = Calendar.getInstance();
    String name, opus,login,prise,plice;
    static Things[] things;
    static Things p;
    LinearLayout liner;
    static int id;
    int position1=0;
    final String FILENAME = "my_login";
    EditText nameEt, opusEt;
    TextView currentDateTime,loginTV,priseEt,pliceEt;
    SimpleDateFormat format;
    private ImageView bigimage;
    private Integer[] imgid = Image_mas.mas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_things);
        plice="";
        currentDateTime = (TextView) findViewById(R.id.data_chas);
        nameEt = (EditText) findViewById(R.id.name_ET_S);
        priseEt = (EditText) findViewById(R.id.priseEt);
        pliceEt = (EditText) findViewById(R.id.pliceEt);
        opusEt = (EditText) findViewById(R.id.opus);
        loginTV=(TextView)findViewById(R.id.loginTV12);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTime.setText(setInitialDateTime());
        login=getIntent().getStringExtra("login");
        if((login==null||(login.equals("")))){
            login=readFile();
        }
        loginTV.setText(login);
        liner=(LinearLayout)findViewById(R.id.place_visible);
        if(getIntent().getBooleanExtra("flag_s",false)){
            liner.setVisibility(View.VISIBLE);
        }else{
            liner.setVisibility(View.GONE);
        }
        bigimage = (ImageView) findViewById(R.id.imageView111);

        bigimage.setImageResource(imgid[2]);
        final Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Выводим номер позиции при щелчке на картинке из галереи
                position1=position;
                bigimage.setImageResource(imgid[position]);
            }
        });
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
    public Things dataToRequest(Things[] P, int id) throws NoSuchAlgorithmException {
        p = new Things();
        name = nameEt.getText().toString().trim();
        opus = opusEt.getText().toString().trim();
        prise=priseEt.getText().toString().trim();
        plice=pliceEt.getText().toString().trim();
        if(plice.equals("")){
            plice="none";
        }
        p.Name = Decoder.toEng(name);
        p.Opus = Decoder.toEng(opus);
        p.EndData = setInitialDateTime();
        p.StartData = setNowTime();
        p.Who = Decoder.toEng(readFile());
        p.Kurent = 0;
        if(getIntent().getBooleanExtra("flag_s",false)) {
            p.Login = "screath";
            p.Kurent=4;
        }else {
            p.Login=login;
        }
        p.Plice=Decoder.toEng(plice);
        p.Prise=Decoder.toEng(prise);
        p.image=position1;
        return p;
    }

    public String postChangeUser(String url) throws IOException, NoSuchAlgorithmException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        p = dataToRequest(things, id);
        Gson gson = new Gson();
        String requestBody = gson.toJson(p);
        StringEntity se = new StringEntity(requestBody, "UTF-8");
        httpPost.setEntity(se);
        httpPost.setHeader("content-type", "application/json; charset=UTF-8");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        InputStream inputStream = httpResponse.getEntity().getContent();
        String result = Login_Activity.convertInputStreamToString(inputStream);
        return result;
    }

    private String setInitialDateTime() {
        long time = dateAndTime.getTimeInMillis();
        String dateString = format.format(new Date(time));
        return dateString;
    }

    private String setNowTime() {
        long time = System.currentTimeMillis();
        String dateString = format.format(new Date(time));
        return dateString;
    }

    public void onDadaClick(View view) {
        new DatePickerDialog(AddThings.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public void onTimeClikc(View view) {
        new TimePickerDialog(AddThings.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    public void onAddThing(View view) {
        if (isNotEmpty(nameEt, opusEt)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddThings.this);
            builder.setTitle(getString(R.string.zagryzka))
                    .setMessage(getString(R.string.zachekaite))
                    .setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            new AddThings.PostAsyncTask().execute("https://things-maker.herokuapp.com/saveThing");

        } else {
            Toast.makeText(getBaseContext(), getString(R.string.pole3), Toast.LENGTH_LONG).show();
        }
    }

    public  boolean isNotEmpty(EditText Name, EditText opus) {
        int n = 1;
        if (Name.getText().toString().trim().length() == 0) {
            Name.setError(getString(R.string.pole1));
            n = n * 0;
        }
        if (opus.getText().toString().trim().length() == 0) {
            opus.setError(getString(R.string.pole2));
            n = n * 0;
        }
        if (n == 1)
            return true;
        else return false;
    }

    private class PostAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            try {
                return postChangeUser(urls[0]);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddThings.this);
                builder.setTitle(getString(R.string.yspih))
                        .setMessage(getString(R.string.thing_addedtolist))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.Ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent things = new Intent(AddThings.this, Things_main.class);
                                        startActivity(things);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }