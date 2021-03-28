package com.milan_projects.roma.things_maker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
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

public class AddKontakt extends AppCompatActivity {
    private Integer[] imgid1 = Image_mas.mas1;
    EditText nameEt, loginEt;
    String login,name;
    private ImageView bigimage;
    final String FILENAME = "my_login";
    static Kontakt[] kon;
    static Kontakt p;
    int id=0;
    int position1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kontakt);
        nameEt = (EditText) findViewById(R.id.add_kontakt_name);
        loginEt = (EditText) findViewById(R.id.add_kontakt_login);
        bigimage = (ImageView) findViewById(R.id.imageView333);
        login=readFile();
        bigimage.setImageResource(imgid1[2]);
        final Gallery gallery = (Gallery) findViewById(R.id.gallery2);
        gallery.setAdapter(new ImageAdapter2(this));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Выводим номер позиции при щелчке на картинке из галереи
                position1=position;
                bigimage.setImageResource(imgid1[position]);
            }
        });
    }
    public Kontakt dataToRequest(Kontakt[] P, int id) throws NoSuchAlgorithmException {
        p = new Kontakt();
        name = nameEt.getText().toString().trim();
        String log="";
        log = loginEt.getText().toString().trim();
        p.Fk_FirstName = Decoder.toEng(name);
        p.Fk_login = log;
        p.Login = login;
        p.image=position1;
        return p;
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
    public String postChangeUser(String url) throws IOException, NoSuchAlgorithmException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        p = dataToRequest(kon, id);
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
    public void OnAddKontakt(View view) {
        if (isNotEmpty(nameEt, loginEt)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddKontakt.this);
            builder.setTitle("Загрузка")
                    .setMessage("Зачекайте будь ласка")
                    .setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            new AddKontakt.PostAsyncTask().execute("https://things-maker.herokuapp.com/saveKontakt");

        } else {
            Toast.makeText(getBaseContext(), getString(R.string.pole3), Toast.LENGTH_LONG).show();
        }
    }
    public boolean isNotEmpty(EditText Name, EditText opus) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddKontakt.this);
            builder.setTitle(getString(R.string.yspih))
                    .setMessage(getString(R.string.kontakt_addedtolist))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.Ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent things = new Intent(AddKontakt.this, SendThing.class);
                                    startActivity(things);
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

}
