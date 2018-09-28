package com.zcliyiran.mysoundrecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service {

    private static final String TAG = "RecordingService";


    private String mFileName = null;
    private String mFilePath = null;
    private MediaRecorder mRecorder = null;
    private DBHelper mDatabase;
    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;

    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }
    public interface OnTimerChangedListener {

        void onTimerChanged(int seconds);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {

        if (mRecorder != null) {
            stopRecording();
        }
        super.onDestroy();
    }
    private void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            //设置录音的音频采样率。调用这个方法前准备()。
            // 准备()可以执行额外的检查参数确定指定的音频采样率是适用的。
            // 采样率很依赖在录音的格式,以及平台的功能。例如,AAC音频编码标准支持的采样率范围从8到96 kHz,
            // AMRNB是8 kHz,支持的采样率和采样率由AMRWB是16 khz。请咨询相关的音频编码标准支持的音频采样率。
            //  @param每秒的音频采样率样本
            mRecorder.setAudioSamplingRate(44100);
            //设置录音的音频编码比特率。调用这个方法前准备()。
            // 准备()可以执行额外的检查参数以确保是否指定比特率适用,有时通过比特率将剪在内部,
            // 确保录音可以顺利进行的基础上平台的功能。
            // @param比特率的音频编码比特率位每秒。
            mRecorder.setAudioEncodingBitRate(192000);
        }
        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }


    }

    private void setFileNameAndPath() {
        int count = 0;
        File f;
        do {
            count++;
            mFileName = getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;
            f = new File(mFilePath);
        } while (f.exists() && !f.isDirectory());
    }




    private void stopRecording() {

        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e) {

            Log.e(TAG, "exception", e);

        }

    }



    private void startTimer() {
        mTimer = new Timer();
        mIncrementTimerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if (onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mgr.notify(1, createNotification());
            }
        };
        mTimer.scheduleAtFixedRate(mIncrementTimerTask, 1000, 1000);
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_mic_white_36dp)
                        .setContentTitle(getString(R.string.notification_recording))
                        .setContentText(mTimerFormat.format(mElapsedSeconds * 1000))
                        .setOngoing(true);

        mBuilder.setContentIntent(PendingIntent.getActivities(getApplicationContext(), 0,
                new Intent[]{new Intent(getApplicationContext(), MainActivity.class)}, 0));

        return mBuilder.build();
    }

}
