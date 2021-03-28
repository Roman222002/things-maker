package com.milan_projects.roma.things_maker;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Login_Activity extends AppCompatActivity {
EditText login,password;
    String log,pass,Name,Surname;
    static String result = "";
    final String FILENAME = "my_login";
    private void initializeComponent(){

        login=(EditText)findViewById(R.id.logenET);
        password=(EditText)findViewById(R.id.passwordET);

        PrefManager prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            log="";
            writeFile();
            writeFile(-1);
        }
        if ((!readFile().equals("")) && readFile() != null) {
            log=readFile();
            Intent things = new Intent(Login_Activity.this,Things_main.class);
            startActivity(things);
        }
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        initializeComponent();
    }
    public static String convertInputStreamToString (InputStream inputStream) throws IOException{
        int b;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while( (b=inputStream.read())!=-1 )
        {
            baos.write( b );
        }
        result = baos.toString();
            inputStream.close();
        return result;
    }
    public  static String GET(String url){
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        url = url.replace(" ", "%20");
        try{
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse=httpClient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream!=null)
                result=convertInputStreamToString(inputStream); else
                    result =" Did not work!";
        }catch (Exception e){
            Log.d("InputStream",e.getLocalizedMessage());
        }
        return result;
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void onButtonVhod(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
        builder.setTitle("Загрузка")
                .setMessage("Зачекайте будь ласка")
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
        log=login.getText().toString().trim();
        pass=password.getText().toString().trim();
        writeFile();
        if(isConnected()){
            new HttpAsyncTask().execute("https://things-maker.herokuapp.com/login?login="+log+"&password="+pass);
           // new HttpAsyncTask().execute("http://192.168.165.2:3000/login?login="+log+"&password="+pass);
        }else{
            Toast.makeText(getBaseContext(), "No connection!", Toast.LENGTH_LONG).show();
        }
    }

    public void onRegestration(View view) {
        Intent things = new Intent(Login_Activity.this,Create_user.class);
        startActivity(things);
    }
    private String readFile() {
        String res = "";
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(FILENAME)));
            // читаем содержимое
            String str = "";
            while ((str = br.readLine()) != null) {
                res += "" + str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    void writeFile() {
        try {
            // отрываем поток для записи
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            bw.write(log);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private class HttpAsyncTask extends AsyncTask<String,Void,String>{

    @Override
    protected String doInBackground(String... urls) {
        return GET(urls[0]);
    }
    protected void onPostExecute(String result){
        if (result.equals("User not found") || result.equals(" Did not work!")){
            Toast.makeText(getBaseContext(), getString(R.string.nepravilo), Toast.LENGTH_LONG).show();
            Intent it= new Intent(Login_Activity.this,Login_Activity.class);
            startActivity(it);
        }else{
            Gson gson = new Gson();
            Person person = gson.fromJson(result,Person.class);
        Name= Decoder.toUTF(person.FirstName);
       Surname=Decoder.toUTF(person.LastName);

        Toast.makeText(getBaseContext(), getString(R.string.vitau)+" "+Name+" "+Surname, Toast.LENGTH_LONG).show();
        Intent things = new Intent(Login_Activity.this,Things_main.class);
        startActivity(things);
            }
    }
}

}
