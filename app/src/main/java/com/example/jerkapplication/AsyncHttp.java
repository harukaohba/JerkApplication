package com.example.jerkapplication;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncHttp extends AsyncTask<String, Integer, Boolean> {

    String id, hz;
    HttpURLConnection urlConnection = null; //HTTPコネクション管理用
    Boolean flg = false;

    public AsyncHttp(String hz, String id) {
        this.hz = hz;
        this.id = id;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        String urlinput = "http://mznjerk.mizunolab.info/sensors/add";

        URL url = null;
        try {
            url = new URL(urlinput);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            String postDataSample = "id="+this.id+"&hz="+this.hz+"&name="+this.id;
            OutputStream out = urlConnection.getOutputStream();
            out.write(postDataSample.getBytes());
            out.flush();
            out.close();
            urlConnection.getInputStream();
            flg = true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flg;
    }


}
