package com.example.jerkapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{

    private SQLiteDataHelper helper;
    private SQLiteDatabase db;
    // Mapのキー
    private final String[] FROM = {"id","date-data","check"};
    // リソースのコントロールID
    private final int[] TO = {R.id.textView,R.id.textView2,R.id.checkBox};
    private ListView lv;
    private  List<Map<String,Object>>  list;
    private Map<String,Object> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // DB
        helper = new SQLiteDataHelper(getApplicationContext());
        db = helper.getWritableDatabase();

        //画面の設定
        lv = findViewById(R.id.listView);

        //ボタンの定義
        findViewById(R.id.button).setOnClickListener(this);

        // DB情報取得
        readJerk();

    }

    public void readJerk(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                "jerk_table",
                new String[] { "id", "hz", "data_date" },
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        //リストデータ作成
        list = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            map =  new HashMap<>();
            //map.put("id",cursor.getInt(0)+ "("+cursor.getString(1)+"Hz)");
            map.put("id",cursor.getString(1));
            map.put("date-data",cursor.getString(2) );
            map.put("check",false);
            list.add(map);

            cursor.moveToNext();
        }

        cursor.close();

        // アダプターの設定→update
        MyAdapter adapter = new MyAdapter(UploadActivity.this,list,R.layout.list,FROM,TO);
        lv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.button:
                    // クリック処理
                    System.out.println("クリック！");
                    CheckUpload();
                    break;


                default:
                    break;
            }
        }
    }

    private void CheckUpload() {
        // リストビューのチェック状態をログに出力する
        for(int i = 0;i < lv.getCount();i++) {
            UploadActivity.MyAdapter adapter = (UploadActivity.MyAdapter)lv.getAdapter();
            View view = adapter.getView(i,null,lv);
            TextView tv = view.findViewById(R.id.textView);
            TextView tv2 = view.findViewById(R.id.textView2);
            if(adapter.checkList.get(i)){
                Log.i("MyTAG", tv2.getText().toString()+"はtrueです。");
                uploadMySQL(tv.getText().toString(), tv2.getText().toString());

            } else{
                Log.i("MyTAG", tv2.getText().toString()+"はfalseです。");
            }

        }
    }

    private void uploadMySQL(String hz, String id) {
        AsyncHttp post = new AsyncHttp(hz,id);
        post.execute();

        readData(id);
    }

    private void readData(String data_date) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                "data_table",
                new String[] { "id", "data_date","time","x","y","z","lat","lon","alt" },
                "data_date == "+ data_date,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();



        for (int i = 0; i < cursor.getCount(); i++) {
            System.out.println(cursor.getInt(0)+cursor.getString(2)+cursor.getDouble(3)+cursor.getDouble(4)+cursor.getDouble(5));
            DataAsyncHttp post = new DataAsyncHttp(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
            post.execute();
            cursor.moveToNext();
        }

        cursor.close();
    }


    // カスタムアダプター
    private class MyAdapter extends SimpleAdapter {

        // 外部から呼び出し可能なマップ
        public Map<Integer,Boolean> checkList = new HashMap<>();

        public MyAdapter(Context context, List<? extends Map<String, ?>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);

            // 初期値を設定する
            for(int i=0; i<data.size();i++){
                Map map = (Map)data.get(i);
                checkList.put(i,(Boolean)map.get("check"));
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            CheckBox ch = view.findViewById(R.id.checkBox);

            // チェックの状態が変化した場合はマップに記憶する
            ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkList.put(position,isChecked);
                }
            });
            return view;
        }
    }


}
