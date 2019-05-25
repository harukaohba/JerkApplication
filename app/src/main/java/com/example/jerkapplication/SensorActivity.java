package com.example.jerkapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener{

    private EditText hzText;
    private int hz_val;
    private SensorManager manager;
    private Sensor sensor;
    private Timer timer;
    private String nowDate;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private SensorEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        //ボタンの定義
        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.stop_button).setOnClickListener(this);
        hzText = (EditText) findViewById(R.id.hz_Text);
        hzText.setText("1");

        timer = new Timer();
        manager = (SensorManager)getSystemService(Activity.SENSOR_SERVICE);
        System.out.println(manager);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        System.out.println(sensor);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS", Locale.getDefault());

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
                    // クリック処理
                    hz_val = Integer.parseInt(hzText.getText().toString());
                    if(hz_val > 0)getdata(hz_val);
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


    private void getdata(final int hzVal) {
            manager.registerListener((SensorEventListener) this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    nowDate = simpleDateFormat.format(calendar.getTime());

                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        System.out.println(nowDate + " : " + event.values[0] + " : " + hzVal);
                    } else {
                        System.out.println(nowDate+" : null");
                    }
                }
            }, 0, 1000 / hzVal);//1Hz 1000ミリ秒, 8Hz 125ミリ秒
            System.out.println("Run!!");
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
