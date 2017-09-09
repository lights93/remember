package com.example.minho.remember;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import java.io.File;
import java.util.ArrayList;

public class option_dialog extends Activity {
    Button start;
    Button voice;
    Button text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option);
        local local=(local)getApplication();

        start = (Button) findViewById(R.id.button_start);
        voice = (Button) findViewById(R.id.button_voice);
        text = (Button) findViewById(R.id.button_text);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), camera.class);
                startActivity(intent);
                finish();
            }
        });

        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), voice.class);
                startActivity(intent);
                finish();
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
                startActivity(intent);
            }
        });
    }
}

