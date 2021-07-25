package com.example.sms_scheduler_final;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class SMSJobService extends JobService{

    private static final String TAG = "Log massage";
    private boolean jobCancelled = false;
    private static final String NUMBER = "Number";

    private String messagenumber ="";



    private static int currentTime = 0;
    private static String currentExactTime;
    DatabaseHelper mDatabaseHelper;

    List<Messages> ListMessMor = new ArrayList<Messages>();
    List<Messages> ListMessAft = new ArrayList<Messages>();
    List<Messages> ListMessNight = new ArrayList<Messages>();




    // SharedPreferences variables
    private static final String SHARED_PREFS_SMS = "AppState";
    private static final String SHARED_PREFS = "dailyCounters";
    private static final String MORNING = "cntMorning";
    private static final String AFTERNOON = "cntAfternoon";
    private static final String NIGHT = "cntNight";

    private static final int cntmonring = 0;
    private static final int cntafternoon = 0;
    private static final int cntnight = 0;

    private final String kiss = getEmoji(0x1F618);
    private final String smile = getEmoji(0x1F60D);



    @Override
    public boolean onStartJob(JobParameters params)
    {
        Log.d(TAG,"Job Started");

        mDatabaseHelper = new DatabaseHelper(this);
        MyInit();
        Collections.shuffle((Arrays.asList(ListMessMor)));
        Collections.shuffle((Arrays.asList(ListMessAft)));
        Collections.shuffle((Arrays.asList(ListMessNight)));
        messagenumber = (readDataString(NUMBER));
        doBackgroundWork(params);

        Log.d(TAG,"Numer wysyłki " + messagenumber);

        return true;
    }

    private void doBackgroundWork(final JobParameters params)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                // Massage for morning hours only 1
                Log.d(TAG, "Numer Joba " + readDataString(NUMBER));
                if ((getCurrentTime() >= 9 && getCurrentTime() <= 11) && (readData(MORNING) != 1 ))
                {
                    sendMessage(1, ListMessMor);
                    saveData(MORNING, 1);
                    AddDataPartDay(1, getCurrentExactTime(),ListMessMor);
                }

                // Massage for afternoon hours 2 massages

                if ((getCurrentTime() >= 13 && getCurrentTime() <= 19))
                {

                    int state = readData(AFTERNOON);
                    switch (state)
                    {
                        case 100:
                            sendMessage(2, ListMessAft);
                            saveData(AFTERNOON, 1);
                            AddDataPartDay(2, getCurrentExactTime(),ListMessAft);
                            break;

                        case 1:
                            saveData(AFTERNOON, 2);
                            AddData(getCurrentExactTime(),"Skip 1 hour 1st pass");
                            break;

                        case 2:
                            saveData(AFTERNOON, 3);
                            AddData(getCurrentExactTime(),"Skip 1 hour 2nd pass");
                            break;

                        case 3:
                            saveData(AFTERNOON, 4);
                            AddData(getCurrentExactTime(),"Skip 1 hour 3nd pass");
                            break;

                        case 4:
                            sendMessage(2, ListMessAft);
                            saveData(AFTERNOON, 5);
                            AddDataPartDay(2, getCurrentExactTime(),ListMessAft);
                            break;

                    }

                }

                // Message for goodnight only 1

                if ((getCurrentTime() == 23 || getCurrentTime() == 0) && readData(NIGHT) != 1 )
                {
                    Log.d(TAG, "WESZło1");
                    sendMessage(3, ListMessNight);
                    saveData(NIGHT, 1);
                    AddDataPartDay(3, getCurrentExactTime(),ListMessNight);

                }

                // Resetting the counters

                if ((getCurrentTime() >= 1 && getCurrentTime() <= 6))

                {
                    saveData(NIGHT, 100);
                    saveData(MORNING, 100);
                    saveData(AFTERNOON, 100);
                }



                for (int i = 0; i < 10; i++)
                {
                    Log.d(TAG, "run: " + i);
                    if (jobCancelled)
                    {
                        return;
                    }

                    try
                    {
                        Thread.sleep(1000);
                    }

                    catch (InterruptedException e)

                    {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        Log.d(TAG,"Job Cancelled");
        jobCancelled = true;
        return true;
    }


    // Adding Data to SQLite

    public void MyInit()
    {
        ListMessMor = mDatabaseHelper.MessagesList(1);
        ListMessAft = mDatabaseHelper.MessagesList(2);
        ListMessNight = mDatabaseHelper.MessagesList(3);

    }


    public void sendMessage(int gid ,List<Messages> txt)
    {
        int groupID = txt.get(0).getGroupID();
        Log.d(TAG, "WESZło w wysyłkę" + gid);
        String msgtxt ="";
        switch(gid)
        {
            case 1:
            case 3:
                msgtxt = txt.get(0).getMessage() + " " + kiss;
                break;
            case 2:
                msgtxt = txt.get(0).getMessage() + " " + smile;
                break;

        }

        Log.d(TAG,  msgtxt);

        String message = msgtxt;
        String number = messagenumber;
        Log.d(TAG,  number + message.length());

        SmsManager mySmsManager = SmsManager.getDefault();
        mySmsManager.sendTextMessage(number,null,message,null,null);
        mDatabaseHelper.setSent(txt);
        mDatabaseHelper.CheckIfAllSent(groupID);
    }

    public void AddData(String date, String msg)
    {

        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelper.addData(date, msg);
    }

    public void AddDataPartDay(int gid, String date, List<Messages> msg)
    {
        String msgtxt;
        switch(gid)
        {
            case 1:
                msgtxt = msg.get(0).getMessage() + kiss;
                mDatabaseHelper.addData(("M: "+ date), msgtxt);
                break;
            case 2:
                msgtxt = msg.get(0).getMessage() + smile;
                mDatabaseHelper.addData(("A: "+ date), msgtxt);
                break;
            case 3:
                msgtxt = msg.get(0).getMessage() + kiss;
                mDatabaseHelper.addData(("N: "+ date), msgtxt);
                break;


        }


    }


    public void saveData(String key, int value)
    {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    public int readData(String key)
    {
        final int result;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        result = sharedPreferences.getInt(key,100);

        return result;

    }

    public int getCurrentTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");

        // current time in 0-23
        currentTime = Integer.parseInt(sdf.format(cal.getTime()));
        Log.d(TAG, "Current time is " + currentTime);

        return currentTime;
    }

    public String getCurrentExactTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        // current time in 0-23
        currentExactTime = sdf.format(cal.getTime());
        Log.d(TAG, "Current time is " + currentExactTime);

        return currentExactTime;

    }


    public String readDataString(String key)
    {
        final String result;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SMS, MODE_PRIVATE);
        result = sharedPreferences.getString(key,"Number not Defined");

        return result;

    }

    public String getEmoji(int uni)
    {
        return new String(Character.toChars(uni));
    }



// Create instances of the messages
// Morning





}
