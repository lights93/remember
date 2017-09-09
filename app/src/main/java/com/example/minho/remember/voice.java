package com.example.minho.remember;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class voice extends Activity{
    String type = "voice";
    SQLiteDatabase db;

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
    MediaPlayer player;
    MediaRecorder recorder;
    String path=GetFilePath();
    int playbackposition =0;
    String filename;
    String timestamp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Test", path);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

        Button recordbtn = (Button)findViewById(R.id.rbtn);
        Button recordstopbtn = (Button)findViewById(R.id.sbtn);
        final Button playbtn = (Button)findViewById(R.id.pbtn);
        Button playstopbtn = (Button)findViewById(R.id.psbtn);
        local local=(local)getApplication();

        db=local.getDb();

        recordbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recorder!=null){
                    Log.i("Test","녹음시작!");
                    recorder.stop();
                    recorder.release();
                    recorder=null;
                }
                Log.i("Test", "111111111111");
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                filename = path+"/"+ timestamp+".mp4";
                recorder.setOutputFile(filename);
                try{
                    Toast.makeText(getApplicationContext(), "녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                    recorder.prepare();
                    recorder.start();
                }catch (Exception e)
                {
                    Log.e("Sample audiorecorder","Exception : ", e);
                }

            }
        });

        recordstopbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(recorder==null)
                    return;
                recorder.stop();
                recorder.release();
                recorder=null;
                Toast.makeText(getApplicationContext(), "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show();
                db.execSQL("INSERT INTO myapp VALUES('" + filename + "', '" + type + "', '" + timestamp.substring(0, 8) + "', '" + timestamp.substring(8)+"');");
                local local=(local)getApplication();
                local.setDb(db);
                Cursor c = db.rawQuery("select * from myapp;", null);
                while(c.moveToNext()) {
                    String path = c.getString(0);
                    String type = c.getString(1);
                    String d = c.getString(2);
                    String t = c.getString(3);
                    Log.d("Test","name:" + path + ",\ttype:" + type + ",\tdate : " + d + ",\ttime : " + t);
                }
            }
        });

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player!=null)
                {
                    player.stop();
                    player.release();
                    player=null;
                }

                Toast.makeText(getApplicationContext(),"녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show();

                try{
                    player=new MediaPlayer();
                    player.setDataSource(filename);

                    player.prepare();
                    player.start();

                }catch(Exception e){
                    Log.e("sampleAudioRecorder", "Audio play failed", e);
                }
            }
        });

        playstopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player==null)
                    return;
                player.stop();
                player.release();
                player=null;
                Toast.makeText(getApplicationContext(), "음악파일 재생 중지됨", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void playAudio(String url) throws Exception{
        killMediaplayer();

        player=new MediaPlayer();
        player.setDataSource(url);
        player.prepare();
        player.start();
    }

    public void onDestroy(){
        super.onDestroy();
        killMediaplayer();
    }

    private void killMediaplayer(){
        if(player!=null)
        {
            try{
                player.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void onPause(){
        if(recorder!=null)
        {
            recorder.release();
            recorder=null;
        }
        if(player!=null)
        {
            player.release();
            player=null;
        }
        super.onPause();
    }
}

