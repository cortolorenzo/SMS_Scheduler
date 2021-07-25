package com.example.sms_scheduler_final;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListLogActivity extends AppCompatActivity {


    DatabaseHelper mDatabaseHelper;
    private ListView mListView;

    private static final String TAG = "txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        mListView = (ListView) findViewById(R.id.listView);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }


    private void populateListView() {

        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1) + ", msg: " + data.getString(2));

            Log.d(TAG, data.getString(1) + ", msg: " + data.getString(2) );
        }

        ListAdapter adapter = new ArrayAdapter<>(this,  android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);


    }

}


