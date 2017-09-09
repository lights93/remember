package com.example.minho.remember;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("deprecation")
public class MainActivity extends ActivityGroup implements OnClickListener{
    SQLiteDatabase db;
    public dbHelper helper;
    public static int flag = 0;
    public static File pictureFile;
    private static final String WEEK = "week";
    private static final String MONTH = "month";
    private static final String TODAY = "today";
    private ViewPager mPager;
    private View mVWeek;
    private View mVMonth;
    private View mVToday;
    Button btn_option, btn_save;
    Intent intent;
    String filename;
    String timestamp;
    String path = GetFilePath();
    File file;
    MediaPlayer player;
    MediaRecorder recorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainactivity);
        LocalActivityManager am;
        am = getLocalActivityManager();
        local local=(local)getApplication();
        local.setnotice(0);
        local.setalarm(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.LOCATION_HARDWARE}, 0);
        }
        helper = new dbHelper(getApplicationContext());
        try {
            db = helper.getWritableDatabase();
            local.setDb(db);
            Log.i("Test","DB");
        } catch (SQLiteException e) {
            db = helper.getReadableDatabase();
            local.setDb(db);
        }

        mVWeek = am.startActivity(WEEK, new Intent(this, MonthActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
        mVMonth = am.startActivity(MONTH, new Intent(this, WeekActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
        mVToday = am.startActivity(TODAY, new Intent(this, TodayActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();

        setLayout();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapterClass(getApplicationContext()));

        btn_option=(Button)findViewById(R.id.btn_option);
        btn_save=(Button)findViewById(R.id.btn_save);

        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent= new Intent(getApplicationContext(), OptionActivity.class);
                startActivity(intent);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                option_dialog();
            }
        });
    }

    private void setCurrentInflateItem(int type){
        if(type==0){
            mPager.setCurrentItem(0);
        }else if(type==1){
            mPager.setCurrentItem(1);
        }else{
            mPager.setCurrentItem(2);
        }
    }


    private void setLayout(){
        Button btn_one;
        Button btn_two;
        Button btn_three;

        btn_one = (Button) findViewById(R.id.bt_one);
        btn_two = (Button) findViewById(R.id.bt_two);
        btn_three = (Button) findViewById(R.id.bt_three);

        btn_one.setOnClickListener(this);
        btn_two.setOnClickListener(this);
        btn_three.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_one:
                setCurrentInflateItem(0);
                break;
            case R.id.bt_two:
                setCurrentInflateItem(1);
                break;
            case R.id.bt_three:
                setCurrentInflateItem(2);
                break;
        }
    }
    private View.OnClickListener mPagerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = ((Button)v).getText().toString();
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    };

    private class PagerAdapterClass extends PagerAdapter{

        private LayoutInflater mInflater;

        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            if(position==0){
                v = mVMonth;
            }
            else if(position==1){
                v = mVToday;
            }else{
                v = mVWeek;

            }

            ((ViewPager)pager).addView(v, 0);

            return v;
        }

        @Override
        public void destroyItem(View pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}
    }
    public static synchronized String GetFilePath()
    {
        String sdcard = Environment.getExternalStorageState();
        File file;
        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
            file = Environment.getRootDirectory();
        else
            file = Environment.getExternalStorageDirectory();

        String dir = file.getAbsolutePath() + String.format("/MyApp");
        file = new File(dir);

        if ( !file.exists() )
            file.mkdirs();

        return dir;
    }
    private void option_dialog(){
        final CharSequence[] items = {"사진", "음성", "문자"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        builder.setTitle("추가 저장")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        if (index == 0 ){
                            camera_dialog();
                        }
                        if(index == 1){
                            Intent intent = new Intent(getApplicationContext(), voice.class);
                            startActivity(intent);
                        }
                        if(index == 2){
                            Intent intent = new Intent(getApplicationContext(), DialogActivity.class);
                            startActivity(intent);
                        }
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    private void camera_dialog(){
        final CharSequence[] items = {"촬영하기", "앨범에서 선택"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        builder.setTitle("사진")        // 제목 설정
                .setItems(items, new DialogInterface.OnClickListener(){    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index){
                        if (index == 0 ){
                            timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            filename = path+"/"+ timestamp+".jpg";
                            file = new File(filename);
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 1011);
                        }
                        if(index == 1){
                            timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                            filename = path+"/"+ timestamp+".jpg";
                            file = new File(filename);
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, 1011);
                        }
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1011 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                db.execSQL("INSERT INTO myapp VALUES('" + filename + "', 'image', '" + timestamp.substring(0, 8) + "', '" + timestamp.substring(8)+"');");
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
        super.onActivityResult(requestCode,resultCode,data);
    }
}

