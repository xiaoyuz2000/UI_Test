package com.example.ui_test;

import androidx.appcompat.app.AppCompatActivity;
import devlight.io.library.ArcProgressStackView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public TextView timeClock;
    public TextView dateClock;
    public TextView tv_show ;
    public TextView currentCourse;
    public TextView nextCourse;
    public TextView weather;
    public SimpleDateFormat time;
    public SimpleDateFormat date;
    public ArcProgressStackView apsv;
    final ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();

    private BatteryReceiver receiver = null;
    private Boolean registered = false;

    public final static int MODEL_COUNT = 4;

    // Parsed colors
    private int[] mStartColors = new int[MODEL_COUNT];
    private int[] mEndColors = new int[MODEL_COUNT];
    //private int[] mEndColors2 = new int[MODEL_COUNT];
    private int[] mBatteryColors = new int[MODEL_COUNT];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeClock = (TextView) findViewById(R.id.time);
        dateClock = (TextView) findViewById(R.id.date);
        tv_show = (TextView) findViewById(R.id.tv_show);
        //创建课表对象ui
        currentCourse = (TextView) findViewById(R.id.currentCourse);
        nextCourse = (TextView) findViewById(R.id.nextCourse);
        //创建天气view
        weather = (TextView) findViewById(R.id.weather);
        apsv = (ArcProgressStackView) findViewById(R.id.apsv);


        time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        //time.setTimeZone(TimeZone.getTimeZone(sharedPreferences.getString("time_zone","GMT+5:30")));

        String dateFormat = "EE, dd MMM yyyy";
        date = new SimpleDateFormat(dateFormat,Locale.getDefault());
        //time.setTimeZone(TimeZone.getTimeZone(sharedPreferences.getString("time_zone","GMT+5:30")));

        Thread myThread = null;

        Runnable runnable = new TimeShow();
        myThread= new Thread(runnable);
        myThread.start();

        final String[] startColors = getResources().getStringArray(R.array.devlight);
        final String[] bgColors = getResources().getStringArray(R.array.medical_express);
        final String[] btColors = getResources().getStringArray(R.array.battery_statue);



        for (int i = 0; i < MODEL_COUNT; i++) {
            mStartColors[i] = Color.parseColor(startColors[i]);
            mBatteryColors[i] = Color.parseColor(btColors[i]);
        }

        models.add(new ArcProgressStackView.Model("",25, Color.parseColor(bgColors[0]), mStartColors[0]));
//        models.add(new ArcProgressStackView.Model("Progress", 50, Color.parseColor(bgColors[1]), mStartColors[1]));
//        models.add(new ArcProgressStackView.Model("Stack", 75, Color.parseColor(bgColors[2]), mStartColors[2]));
//        models.add(new ArcProgressStackView.Model("View", 100,Color.parseColor(bgColors[3]), mStartColors[3]));


        final ArcProgressStackView arcProgressStackView = (ArcProgressStackView) findViewById(R.id.apsv);
        arcProgressStackView.setModels(models);


        BatteryReceiver mReceiver = new BatteryReceiver();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        registered = true;

        String city = "London";
        String language = "en";
        String unit = "metric";  // or "imperial"

        setWeather(city,language,unit);
    }



    public void doWork() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Calendar c = Calendar.getInstance();
                    timeClock.setText(time.format(c.getTime()));
                    dateClock.setText(date.format(c.getTime()));

                }catch (Exception e) {}
            }
        });
    }

    private class TimeShow implements Runnable{
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(receiver);
        super.onStop();
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获得当前电量
            int current = intent.getExtras().getInt("level");
            // 获得总电量
            int total = intent.getExtras().getInt("scale");
            int percent = current * 100 / total;
            models.get(0).setProgress(percent);
            if(percent==100){
                apsv.setIsRounded(false);
            }
            if(percent>=50){
                models.get(0).setColor(mBatteryColors[0]);
                apsv.setTextColor(mBatteryColors[0]);
            }else if(percent>=20){
                models.get(0).setColor(mBatteryColors[1]);
                apsv.setTextColor(mBatteryColors[1]);
            }else{
                models.get(0).setColor(mBatteryColors[2]);
                apsv.setTextColor(mBatteryColors[2]);
            }
            tv_show.setText(String.valueOf(percent));
        }
    }

    public void setCurrentCourse(String course){
        currentCourse.setText(course);
    }

    public void setNextCourse(String course){
        nextCourse.setText(course);
    }

    public void setWeather(String city, String language, String unit){
        new OpenWeatherMapTask(weather).execute(city,language,unit);
    }
}