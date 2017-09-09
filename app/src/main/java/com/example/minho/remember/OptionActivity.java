package com.example.minho.remember;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class OptionActivity extends Activity{
    ToggleButton toggle;
    SeekBar seekBar;
    TextView tv, tv2;
    public AlertDialog mDialog = null;
    Button btn_allim, btn_finish, btn_Finish2;
    Intent intent;
    int notice;
    boolean alarm;
    SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.optionactivity);

        local local=(local)getApplication();
        db = local.getDb();
        btn_allim=(Button)findViewById(R.id.btn_allim);
        btn_finish=(Button)findViewById(R.id.btn_finish);


        btn_allim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = createDialog();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    public AlertDialog createDialog() {

        Cursor c = db.rawQuery("select * from myapp;", null);
        while(c.moveToNext()) {
            String path = c.getString(0);
            String type1 = c.getString(1);
            String d = c.getString(2);
            String t = c.getString(3);
            Log.d("dbdb","name:" + path + ",\ttype:" + type1 + ",\tdate : " + d + ",\ttime : " + t);
        }
        c.moveToFirst();
        while(c.moveToNext()) {
            String type = c.getString(1);
            if(type.equals("alarm")){
                String temp = c.getString(0);
                if (temp.equals("true")){
                    alarm = true;
                }
                else
                    alarm = false;
                notice = c.getInt(2);
            }
        }
        db.delete("myapp","type = ?", new String[]{"alarm"});

        LayoutInflater inflater = LayoutInflater.from(OptionActivity.this);
        View innerview= getLayoutInflater().inflate(R.layout.allimlayout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(innerview);
        seekBar = (SeekBar)innerview.findViewById(R.id.seekbar);
        tv= (TextView) innerview.findViewById(R.id.tv);
        tv2= (TextView) innerview.findViewById(R.id.tv2);
        final boolean origin_alarm=alarm;
        final int origin_notice=notice;

        toggle =(ToggleButton) innerview.findViewById(R.id.toggle);

        ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                local local=(local)getApplication();
                db.execSQL("INSERT INTO myapp VALUES('" + Boolean.toString(origin_alarm) + "', 'alarm', '" + Integer.toString(origin_notice) + "', null);");
                mDialog.dismiss();
            }
        });
        tv.setText(Integer.toString(notice+30)+ "분");
        toggle.setChecked(origin_alarm);

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(alarm == true) {
                    db.execSQL("INSERT INTO myapp VALUES('" + Boolean.toString(alarm) + "', 'alarm', '" + Integer.toString(notice) + "', null);");
                    scheduleNotification(getNotification(), (notice + 30) * 60 * 1000);
                }
                else if (alarm == false) {
                    db.execSQL("INSERT INTO myapp VALUES('" + Boolean.toString(alarm) + "', 'alarm', '" + Integer.toString(notice) + "', null);");
                    releaseAlarm(getApplicationContext());
                }
                if(mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });


        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                alarm=origin_alarm;
                notice=origin_notice;
                local local=(local)getApplication();
                db.execSQL("INSERT INTO myapp VALUES('" + Boolean.toString(origin_alarm) + "', 'alarm', '" + Integer.toString(origin_notice) + "', null);");

                if(mDialog != null && mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        seekBar.setProgress(notice/30);
        seekBar.setMax(10);
        if(alarm) {
            seekBar.setVisibility(View.VISIBLE);
            tv.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);

        }
        else {
            seekBar.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
        }

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle.isChecked()) {
                    alarm = true;
                    seekBar.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                }
                else {
                    alarm = false;
                    seekBar.setVisibility(View.INVISIBLE);
                    tv.setVisibility(View.INVISIBLE);
                    tv2.setVisibility(View.INVISIBLE);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                notice=progress*30;
                local local=(local)getApplication();

                local.setnotice(notice);
                tv.setText(Integer.toString(notice+30)+"분");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv.setText(tv.getText());
            }
        });

        AlertDialog alert = ab.create();
        alert.show();

        return ab.create();
    }

    private void releaseAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, BroadReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void scheduleNotification(Notification notification, int delay) {
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, BroadReceiver.class);
        notificationIntent.putExtra(BroadReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(BroadReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000, delay, pendingIntent);
    }


    private Notification getNotification() {
        Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
        Intent txtIntent = new Intent(getApplicationContext(), DialogActivity.class);
        Intent camIntent = new Intent(getApplicationContext(), camera.class);
        Intent voiceIntent = new Intent(getApplicationContext(), voice.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent txtPend = PendingIntent.getActivity(getApplicationContext(), 0, txtIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent camPend = PendingIntent.getActivity(getApplicationContext(), 0, camIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent voicePend = PendingIntent.getActivity(getApplicationContext(), 0, voiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("써주세용~~");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("뭘 하고 계신가요?");
        builder.setProgress(100, 50, false);
        builder.setContentText("간단하게 기록해주세요!");
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.addAction(0, "글쓰기", txtPend);
        builder.addAction(0, "사진찍기", camPend);
        builder.addAction(0, "음성녹음", voicePend);

        // 고유ID로 알림을 생성.
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(123456, builder.build());
        Toast.makeText(OptionActivity.this, "뭐 하고 계신가요?", Toast.LENGTH_LONG).show();
        return builder.build();
    }
}