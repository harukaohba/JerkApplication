package com.example.jerkapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener{

    private SQLiteDataHelper helper;
    private SQLiteDatabase db;
    // Mapのキー
    private final String[] FROM = {"id","date-data","check"};
    // リソースのコントロールID
    private final int[] TO = {R.id.textView,R.id.textView2,R.id.checkBox};
    private ListView lv,dataView;
    private  List<Map<String,Object>>  list;
    private Map<String,Object> map;
    private ArrayList data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // DB
        helper = new SQLiteDataHelper(getApplicationContext());
        db = helper.getWritableDatabase();

        //画面の設定
        lv = findViewById(R.id.listView);
        dataView = findViewById(R.id.dataView);

        //ボタンの定義
        findViewById(R.id.button).setOnClickListener(this);

        // DB情報取得
        readJerk();







        /*
        // イベント
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(String.valueOf(position)+"番目がクリックされました。");
            }
        });
        */

        /*
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // リストビューのチェック状態をログに出力する
                for(int i = 0;i < lv.getCount();i++) {
                    MyAdapter adapter = (MyAdapter)lv.getAdapter();
                    View view = adapter.getView(i,null,lv);
                    TextView tv = view.findViewById(R.id.textView);
                    if(adapter.checkList.get(i))
                        Log.i("MyTAG", tv.getText().toString()+"はtrueです。");
                    else
                        Log.i("MyTAG", tv.getText().toString()+"はfalseです。");
                }
            }
        });
        */




    }


    @Override
    protected void onStart() {
        super.onStart();


        Log.d("debug","onStart()");
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
            map.put("id",cursor.getInt(0)+ "("+cursor.getString(1)+"Hz)");
            map.put("date-data",cursor.getString(2) );
            map.put("check",false);
            list.add(map);

            cursor.moveToNext();
        }

        cursor.close();

        // アダプターの設定→update
        MyAdapter adapter = new MyAdapter(ViewActivity.this,list,R.layout.list,FROM,TO);
        lv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.button:
                    // クリック処理
                    System.out.println("クリック！");
                    checkSee();
                    break;


                default:
                    break;
            }
        }

    }

    private void checkSee() {
        //data出力用
        data = new ArrayList<>();

        // リストビューのチェック状態をログに出力する
        for(int i = 0;i < lv.getCount();i++) {
            MyAdapter adapter = (MyAdapter)lv.getAdapter();
            View view = adapter.getView(i,null,lv);
            TextView tv = view.findViewById(R.id.textView);
            TextView tv2 = view.findViewById(R.id.textView2);
            if(adapter.checkList.get(i)){
                Log.i("MyTAG", tv2.getText().toString()+"はtrueです。");
                readData(tv2.getText().toString());
            } else{
                Log.i("MyTAG", tv2.getText().toString()+"はfalseです。");
            }

        }

        //data出力
        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        // ListViewにArrayAdapterを設定する
        dataView.setAdapter(adapter);
    }

    private void readData(String data_date) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                "data_table",
                new String[] { "id", "data_date","time","x","y","z" },
                "data_date == "+ data_date,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();


        //リストデータ作成
        data.add("id | time | x | y | z");
        for (int i = 0; i < cursor.getCount(); i++) {
            System.out.println(cursor.getInt(0)+cursor.getString(2)+cursor.getDouble(3)+cursor.getDouble(4)+cursor.getDouble(5));
            data.add(cursor.getInt(0) + " | " + cursor.getString(2) + " | " + String.format("%.2f",cursor.getDouble(3)) + " | " + String.format("%.2f",cursor.getDouble(4)) + " | " + String.format("%.2f",cursor.getDouble(5)));
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
