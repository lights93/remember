package com.example.minho.remember;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogActivity extends Activity {
    EditText editText;
    Button btnSave, btnCancel;
    String temp;
    String type = "text";
    String timestamp;
    SQLiteDatabase db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        editText = (EditText)findViewById(R.id.editText);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        local local=(local)getApplication();

        db=local.getDb();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = editText.getText().toString();
                timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                editText.setText("");
                db.execSQL("INSERT INTO myapp VALUES('" + temp + "', '" + type + "', '" + timestamp.substring(0, 8) + "', '" + timestamp.substring(8)+"');");
                local local=(local)getApplication();
                local.setDb(db);
                Cursor c = db.rawQuery("select * from myapp;", null);
                while(c.moveToNext()) {
                    String path = c.getString(0);
                    String type = c.getString(1);
                    String d = c.getString(2);
                    Log.d("Test","name:" + path + ",\ttype:" + type + ",\ttime : " + d);
                }
                Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
