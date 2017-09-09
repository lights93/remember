package com.example.minho.remember;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class local extends Application{
    private int notice;
    private boolean alarm;
    public SQLiteDatabase db;


    public int getnotice(){
        return notice;
    }

    public void setnotice(int n){
        notice=n;
    }

    public boolean getalarm(){
        return alarm;
    }

    public void setalarm(boolean a){
        alarm=a;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
