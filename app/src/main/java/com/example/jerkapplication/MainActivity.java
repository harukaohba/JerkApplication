package com.example.jerkapplication;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //初期化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ボタンの定義
        findViewById(R.id.insert_button).setOnClickListener(this);
        findViewById(R.id.view_button).setOnClickListener(this);

        Log.d("debug","onCreate()");
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug","onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug","onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug","onResume()");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug","onPause()");
    }

    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug","onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug","onDestroy()");
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.insert_button:
                    // クリック処理
                    Intent intent1= new Intent(getApplication(), SensorActivity.class);
                    startActivity(intent1);
                    break;

                case R.id.view_button:
                    // クリック処理
                    Intent intent2= new Intent(getApplication(), ViewActivity.class);
                    startActivity(intent2);
                    break;

                default:
                    break;
            }
        }

    }

    //ライフサイクルについては
    //https://akira-watson.com/android/orientation.html
}
