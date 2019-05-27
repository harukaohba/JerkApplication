package com.example.jerkapplication;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataAsyncHttp extends AsyncTask<String, Integer, Boolean> {

    String id, jerk_table_id, time, x, y, z;
    HttpURLConnection urlConnection = null; //HTTPコネクション管理用
    Boolean flg = false;

    public DataAsyncHttp(String id,String jerk_table_id,String time,String x,String y,String z) {
        this.id = id;
        this.jerk_table_id = jerk_table_id;
        this.time = time;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String urlinput = "http://mznjerk.mizunolab.info/data_tables/add";

        URL url = null;
        try {
            url = new URL(urlinput);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            String postDataSample = "id="+this.id+"&jerk_table_id="+this.jerk_table_id+"&time="+this.time+"&x="+this.x+"&y="+this.y+"&z="+this.z;
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
