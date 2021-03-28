package com.milan_projects.roma.things_maker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TImeCheck extends Service {
    String[] things=null;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timer timer=null;
    String log;
    TimerTask mTimerTask;
    Things[] thing;
    final String FILENAME = "my_login";
    public TImeCheck() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
      //  throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        log=readFile();
        new TImeCheck.ThingsAnnyTask().execute("https://things-maker.herokuapp.com/things?login="+log+"&place="+Decoder.toEng("none")+"&kurrent=0");
        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        mTimerTask = new MyTimerTask();
        timer.schedule(mTimerTask, 1000, 1000);
        return START_STICKY;
    }

    void sendNotif(String text,String text2) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        Intent intent = new Intent(this, Things_main.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Things_maker");
        builder.setContentText(text);
        builder.setSubText(text2);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());

    }

    private void logLoop() {

                           long now = System.currentTimeMillis();
                           String nowTime;
                           nowTime=format.format(new Date(now));
        if(things!=null){
                           for(int i=0;i<things.length;i++) {
                               if (things[i].equals(nowTime)) {
                                   sendNotif("Час вийшов","Натисніть для продовження");
                                   writeFile(i);
                               }
                           }}}
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
                logLoop();
        }
    }
    public static String convertInputStreamToString (InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        String result ="";
        while ((line=bufferedReader.readLine())!=null)
            result+=line;
        inputStream.close();
        return result;
    }
    public  static String GET(String url){
        InputStream inputStream=null;
        String result="";
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
    private class ThingsAnnyTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            thing=gson.fromJson(result, Things[].class);
            things=new String [thing.length];
            for(int i=0;i<thing.length;i++){
                things[i]=""+thing[i].EndData;
            }
        }
    }

}
