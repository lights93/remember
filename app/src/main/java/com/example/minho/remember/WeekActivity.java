package com.example.minho.remember;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeekActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    Calendar calendar;
    SwipeRefreshLayout layout;
    ArrayList<Recycler_item> items;
    RecyclerAdapter newAdapter;
    RecyclerView recyclerView;
    public SQLiteDatabase db;
    public dbHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekactivity);
        items = new ArrayList<Recycler_item>();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        calendar= Calendar.getInstance();
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        newAdapter = new RecyclerAdapter(getApplicationContext(), items, R.layout.weekactivity);
        layout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        layout.setOnRefreshListener(this);
        RefreshView();
    }

    @Override
    public void onRefresh() {
        RefreshView();
        layout.setRefreshing(false);
    }

    public void RefreshView(){
        helper = new dbHelper(getApplicationContext());
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = helper.getReadableDatabase();
        }
        Log.i("Test", "create");
        Cursor c = db.rawQuery("select * from myapp;", null);
        items.clear();
        while(c.moveToNext()) {
            String temp = c.getString(0);
            String type = c.getString(1);
            int date = c.getInt(2);
            int time = c.getInt(3);
            String image = temp;
            int year= date / 10000;
            int month= date / 100 % 100;
            int day= date % 100;
            int hour= time / 10000;
            int minute= time / 100 % 100;
            int second= time % 100;
            Calendar cal= Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);

            if(year==cal.get(Calendar.YEAR) && month==cal.get(Calendar.MONTH)+1 && day==cal.get(Calendar.DAY_OF_MONTH))
                items.add(new Recycler_item(type, image, hour+"시 "+ minute + "분"+ second +"초", year+"."+month+"."+day));
        }
        newAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(newAdapter);
    }
}