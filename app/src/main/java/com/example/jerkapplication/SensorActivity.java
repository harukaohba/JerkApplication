package com.example.jerkapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, LocationListener {

    private EditText hzText;
    private int hz_val;
    private SensorManager manager;
    private Sensor sensor;
    private Timer timer;
    private String nowDate, filename, time;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat, simpleDateFormatDate;
    private SensorEvent event;
    private TextView sensorText, sensorTextLatlon,sensorStartText;
    private Switch csvswitchButton, databaseswitchButton;
    private SQLiteDataHelper helper;
    private SQLiteDatabase db;
    private int one_only = -1;
    private Location locationdata;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //ボタンの定義
        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.stop_button).setOnClickListener(this);
        hzText = (EditText) findViewById(R.id.hz_Text);
        hzText.setText("1");
        sensorStartText = (TextView) findViewById(R.id.sensorStartText);
        sensorText = (TextView) findViewById(R.id.sensorText);
        sensorTextLatlon = (TextView) findViewById(R.id.sensorTextLatlon);
        csvswitchButton = (Switch) findViewById(R.id.csvswitch);
        databaseswitchButton = (Switch) findViewById(R.id.databaseswitch);

        timer = new Timer();
        manager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
        System.out.println(manager);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        System.out.println(sensor);

        // DB作成
        helper = new SQLiteDataHelper(getApplicationContext());
        db = helper.getWritableDatabase();

        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS", Locale.getDefault());
        simpleDateFormatDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());


        //latlon
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }else {
            System.out.println("位置情報取得できない");
        }
        Log.d("debug","onCreate()");

    }


    // 解除するコードも入れる!
    @Override
    protected void onPause() {
        super.onPause();
        // Listenerを解除
        manager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.start_button:
                    one_only++;
                    if(one_only == 0){
                        // クリック処理
                        hz_val = Integer.parseInt(hzText.getText().toString());
                        if(hz_val > 0){
                            //日付取得
                            calendar = Calendar.getInstance();
                            nowDate = simpleDateFormat.format(calendar.getTime());
                            filename = simpleDateFormatDate.format(calendar.getTime());
                            sensorStartText.setText(String.valueOf(nowDate) + "からスタート！ \n (" + filename + ".csv)");
                            getdata(hz_val);
                            System.out.println("Strat!!");
                        }
                    }
                    break;

                case R.id.stop_button:
                    // クリック処理
                    stopdata();
                    break;

                default:
                    break;
            }
        }
    }

    private void checkDataSave() {
        if(csvswitchButton.isChecked())csvFile(); //csv出力
        if(databaseswitchButton.isChecked())dataBaseSave();
    }


    private void getdata(final int hzVal) {
        manager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calendar = Calendar.getInstance();
                nowDate = simpleDateFormat.format(calendar.getTime());
                time = nowDate;

                /* if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { */
                if((event != null) && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)){
                    System.out.println(String.valueOf(nowDate) + "|| x:" + String.valueOf(event.values[0]) + " y:" + String.valueOf(event.values[1]) + " z:" + String.valueOf(event.values[2]));
                    //data保存について確認
                    checkDataSave();
                } else {
                    //System.out.println(nowDate+" : null");
                }
            }
        }, 0, 1000 / hzVal);//1Hz 1000ミリ秒, 8Hz 125ミリ秒
        System.out.println("Run!!");
    }

    public void csvFile(){
        try{
            FileWriter fw = new FileWriter(getFilesDir() + filename+".csv",true);//true追記、false上書き
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print(time);
            pw.print(",");
            pw.print(event.values[0]);
            pw.print(",");
            pw.print(event.values[1]);
            pw.print(",");
            pw.print(event.values[2]);
            pw.print(",");
            pw.print(locationdata.getLatitude());
            pw.print(",");
            pw.print(locationdata.getLongitude());
            pw.print(",");
            pw.print(locationdata.getAltitude());
            pw.print(",");


            /*if (location == null){
                pw.print(location1);
                pw.print(",");
                pw.print(location1);
            }else {
                pw.print(location1.getLatitude());
                pw.print(",");
                pw.print(location1.getLongitude());
            }*/
            pw.println();
            pw.close();
        }catch (IOException e){
            e.printStackTrace(); //例外時処理
        }
    }

    private void dataBaseSave() {
        ContentValues values;
        values = new ContentValues();
        if(one_only == 0){
            one_only++;
            values.put("hz", hz_val);
            values.put("data_date", filename);
            db.insert("jerk_table", null, values);
        }

        values = new ContentValues();
        values.put("data_date", filename);
        values.put("time", time);
        values.put("x", event.values[0]);
        values.put("y", event.values[1]);
        values.put("z", event.values[2]);
        values.put("lat", locationdata.getLatitude());
        values.put("lon", locationdata.getLongitude());
        values.put("alt", locationdata.getAltitude());
        db.insert("data_table", null, values);
    }


    private void stopdata() {
        manager.unregisterListener((SensorEventListener) this);
        timer.cancel();
        event = null;
        System.out.println("Stop!!");
    }


    //センサの値が変化したとき
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        event = sensorEvent;
        sensorText.setText(String.valueOf(nowDate) + "|| x:" + String.valueOf(event.values[0]) + " y:" + String.valueOf(event.values[1]) + " z:" + String.valueOf(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    //緯度経度変化した時
    @Override
    public void onLocationChanged(Location location) {
        locationdata = location;
        System.out.println("location : "+location.getLatitude()+", "+location.getLongitude()+", "+location.getAltitude());
        sensorTextLatlon.setText("lat:"+ String.valueOf(locationdata.getLatitude()) + "lon:" + String.valueOf(locationdata.getLongitude()) + "alt:" + String.valueOf(locationdata.getAltitude()));
        //count = count+1;

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
