package com.hanum.hanum_waktu_sholat.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import com.hanum.hanum_waktu_sholat.R;

public class AlarmNotifikasi extends Service {

    @SuppressLint("ResourceType")
    MediaPlayer mediaPlayer;

    public AlarmNotifikasi() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer = MediaPlayer.create(AlarmNotifikasi.this,R.raw.adzan);
        mediaPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        Toast.makeText(this, "service onDestroy", Toast.LENGTH_SHORT).show();

    }
}
