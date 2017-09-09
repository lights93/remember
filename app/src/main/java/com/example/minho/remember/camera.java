package com.example.minho.remember;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class camera extends Activity{
    SQLiteDatabase db;
    Button btn, btn2;
    ImageView imageView;
    String type = "image";
    String path=GetFilePath();
    String filename;
    String timestamp;
    File file;

    public static synchronized String GetFilePath()
    {
        String sdcard = Environment.getExternalStorageState();
        File file = null;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
        {
            // SD카드가 마운트되어있지 않음
            file = Environment.getRootDirectory();
        }
        else {
            //SD카드가 마운트되어있음
            file = Environment.getExternalStorageDirectory();
        }

        String dir = file.getAbsolutePath() + String.format("/MyApp");
        file = new File(dir);
        if ( !file.exists() )
        {
            // 디렉토리가 존재하지 않으면 디렉토리 생성
            file.mkdirs();
        }
        Log.i("Test", dir);
        // 파일 경로 반환
        return dir;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.album);
        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);
        imageView = (ImageView) findViewById(R.id.image);
        local local=(local)getApplication();

        db=local.getDb();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                filename = path+"/"+ timestamp+".jpg";
                file = new File(filename);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1011);

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                filename = path+"/"+ timestamp+".jpg";
                file = new File(filename);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1011);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1011 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                db.execSQL("INSERT INTO myapp VALUES('" + filename + "', '" + type + "', '" + timestamp.substring(0, 8) + "', '" + timestamp.substring(8)+"');");
                local local=(local)getApplication();
                local.setDb(db);
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                FileOutputStream out = new FileOutputStream(filename);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Cursor c = db.rawQuery("select * from myapp;", null);
                while(c.moveToNext()) {
                    String path = c.getString(0);
                    String type = c.getString(1);
                    String d = c.getString(2);
                    String t = c.getString(3);
                    Log.d("Test","name:" + path + ",\ttype:" + type + ",\tdate : " + d + ",\ttime : " + t);
                }
            }catch (IOException e){

            }
        }
    }
}