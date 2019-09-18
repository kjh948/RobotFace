package com.samsung.robotface;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.VideoView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    String base_path = "/sdcard/Download/";
    String src_path_default = "/sdcard/Download/neutral.mp4";
    String src_path_1 = "/sdcard/Download/neutral.mp4";
    String src_path_2 = "/sdcard/Download/neutral.mp4";
    String src_path_3 = "/sdcard/Download/neutral.mp4";

    private BluetoothSPP bt;
    private int currentID;
    private int loopCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bt = new BluetoothSPP(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        VideoView myVideoView = (VideoView) findViewById(R.id.face);
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                VideoView myVideoView = (VideoView) findViewById(R.id.face);
                if(loopCnt==0)
                {
                    myVideoView.stopPlayback();
                    playMovie(src_path_default,-1);
                }
                else
                {
                    myVideoView.start();
                    if(loopCnt>0) loopCnt -= 1;
                }
            }
        });
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener(){
            public void onDataReceived(byte[] data,  String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                String[] cmd = msg.split("#");
                int num = Integer.parseInt(cmd[1]);
                if(cmd.length!=2)
                {
                    Toast.makeText(MainActivity.this, "Error: Length is not correct", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(num<-2 || num>10)
                {
                    Toast.makeText(MainActivity.this, "Error: Range is not correct", Toast.LENGTH_SHORT).show();
                    return;
                }
                playMovie(base_path+cmd[0],num);

            }
        });

        Intent intent = new Intent(getApplicationContext(),DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        playMovie(src_path_default,1);
    }

    public void onDestroy()
    {
        super.onDestroy();
        bt.stopService();
    }
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }
    public void setup() {
        return;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                currentID +=1;
                if((currentID%3)==0) playMovie(src_path_1,1);
                if((currentID%3)==1) playMovie(src_path_2,1);
                if((currentID%3)==2) playMovie(src_path_3,1);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(event);
    }
    public void playMovie(String filePath,int lc)
    {
        VideoView myVideoView = (VideoView) findViewById(R.id.face);
        myVideoView.setVideoPath(filePath);
        myVideoView.setMediaController(null);
        myVideoView.requestFocus();
        myVideoView.start();
        loopCnt = lc-1;
    }


}
