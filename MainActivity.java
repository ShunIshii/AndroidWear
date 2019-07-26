package jp.aoyama.a5816071.heart_beats_time;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

import static android.hardware.Sensor.TYPE_HEART_BEAT;
import static android.hardware.Sensor.TYPE_HEART_RATE;


public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final String TAG = "MainActivity";
    private TextView heartTextView;
    private TextView intervalTextView;
    private TextView TimeView;
    double heart_rate_interval=0;
    double value=0;
    int count=0;
    double time=0;
    SensorManager mSensorManager;
    Date date=new Date();
    String filename = String.valueOf(date);
    //SensorManager rate_Manager;
    //Sensor mHeartRateSensor;
    //SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enables Always-on
        //setAmbientEnabled();

        heartTextView = (TextView) findViewById(R.id.text_heart);
        intervalTextView = (TextView) findViewById(R.id.interval);
        TimeView = (TextView) findViewById(R.id.time);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        value=0;

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::WakelockTag");

        wakeLock.acquire();
    }

    protected void onResume() {
        super.onResume();
        Sensor sensor = mSensorManager.getDefaultSensor(TYPE_HEART_BEAT);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG,"onAccuracyChanged - accuracy: " + accuracy);
    }

    public void onSensorChanged(SensorEvent event) {

        heartTextView = (TextView) findViewById(R.id.text_heart);
        intervalTextView = (TextView) findViewById(R.id.interval);
        TimeView = (TextView) findViewById(R.id.time);
        Sensor sensor = event.sensor;

        time = event.timestamp/Math.pow(10, 9);
        double x = event.values[0];

        heart_rate_interval=time-heart_rate_interval;
        if(count!=0) {
            value = value + heart_rate_interval;
        }
        String s = String.valueOf(x);
        String t = String.valueOf(heart_rate_interval);
        String u = String.valueOf(value);
        //heartTextView.setText("心拍検出\n"+s);
        intervalTextView.setText("心拍間隔\n"+t);
        TimeView.setText("経過時間\n"+u);
        heart_rate_interval=time;
        count++;

        sampleFileOutput(filename, count, t, u);

    }

    private void sampleFileOutput(String filename, int count, String data, String time){
        OutputStream out;

        if(count!=1) {
            try {
                out = openFileOutput(filename, MODE_PRIVATE | MODE_APPEND);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.append(data + ", " + time +"\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /*public void onStart(View v){
        ((Chronometer)findViewById(R.id.ch)).setBase(SystemClock.elapsedRealtime());
        ((Chronometer)findViewById(R.id.ch)).start();
    }

    public void onStop(View v){
        ((Chronometer)findViewById(R.id.ch)).stop();
    }*/
}
