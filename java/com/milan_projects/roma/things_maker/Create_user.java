package com.milan_projects.roma.things_maker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.Signature.getInstance;

public class Create_user extends AppCompatActivity {
    EditText loginEt, passEt, FirstNameEt, LastNameEt;
    String FirstName, LastName, login, Password_hash;
    static Person p;
    static Person[] users;
    static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        initializate();
    }

    private void initializate() {
        loginEt = (EditText) findViewById(R.id.LoginEtRo);
        passEt = (EditText) findViewById(R.id.passEt);
        FirstNameEt = (EditText) findViewById(R.id.first_nameEt);
        LastNameEt = (EditText) findViewById(R.id.SurNameEt);
    }

    public boolean isNotEmpty(EditText Name, EditText SurName, EditText log, EditText pass) {
        int n = 1;
        if (Name.getText().toString().trim().length() == 0) {
            Name.setError(getString(R.string.pole1));
            n = n * 0;
        }
        if (SurName.getText().toString().trim().length() == 0) {
            SurName.setError(getString(R.string.pole2));
            n = n * 0;
        }
        if (log.getText().toString().trim().length() == 0) {
            log.setError(getString(R.string.pole4));
            n = n * 0;
        }
        if (pass.getText().toString().trim().length() == 0) {
            pass.setError(getString(R.string.pole5));
            n = n * 0;
        }
        if (n == 1)
            return true;
        else return false;
    }

    public static String getHashPass(String pass) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(pass.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInteger = new BigInteger(1, digest);
        String hashtext = bigInteger.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    public Person dataToRequest(Person[] P, int id) throws NoSuchAlgorithmException {
        p = new Person();
        FirstName =FirstNameEt.getText().toString().trim();
        LastName =LastNameEt.getText().toString().trim();
        p.FirstName = Decoder.toEng(FirstName);
        p.LastName = Decoder.toEng(LastName);
        login = loginEt.getText().toString().trim();
        p.Login = login;
        Password_hash = getHashPass(passEt.getText().toString().trim());
        p.Password_hash = Password_hash;

        return p;
    }

    public String postChangeUser(String url) throws IOException, NoSuchAlgorithmException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        p = dataToRequest(users, id);
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

    public void saveUser(View view) {
        if (isNotEmpty(FirstNameEt, LastNameEt, loginEt, passEt)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Create_user.this);
            builder.setTitle(getString(R.string.zagryzka))
                    .setMessage(getString(R.string.zachekaite))
                    .setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
            new PostAsyncTask().execute("https://things-maker.herokuapp.com/saveUser");
         //   new PostAsyncTask().execute("http://192.168.165.2:3000/saveUser");
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.pole3), Toast.LENGTH_LONG).show();
        }
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
            if (result.equals("User already exists")) {
                Toast.makeText(getBaseContext(), getString(R.string.userExist), Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Create_user.this);
                builder.setTitle(getString(R.string.yspih))
                        .setMessage(getString(R.string.vviidit))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.Ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent things = new Intent(Create_user.this,Login_Activity.class);
                                        startActivity(things);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}

