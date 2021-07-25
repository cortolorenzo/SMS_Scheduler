package com.example.sms_scheduler_final;

import android.Manifest;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";

    private static final String SHARED_PREFS = "AppState";
    private static final String SHARED_PREFS_CNT = "AppState";
    private static final String STATUS = "Status";
    private static final String NUMBER = "Number";
    DatabaseHelper mDatabaseHelper;

    private static final String MORNING = "cntMorning";
    private static final String AFTERNOON = "cntAfternoon";
    private static final String NIGHT = "cntNight";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);



        Button btnSchedule = (Button) findViewById(R.id.btnS);
        Button btnCanceled = (Button) findViewById(R.id.btnC);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        EditText edtTextNumber = (EditText) findViewById(R.id.editTextPhone);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        readData(STATUS);
        Log.d(TAG, "Status to: " + readData(STATUS));


        if (readData(STATUS) == 0)
        {
            btnCanceled.setBackgroundColor(Color.GREEN);
            btnSchedule.setBackgroundColor(Color.GRAY);

        }

        if (readData(STATUS) == 1)
        {
            btnCanceled.setBackgroundColor(Color.GRAY);
            btnSchedule.setBackgroundColor(Color.GREEN);

        }

        String stBecky = "503906760";
        String stMiguel = "789170386";

        if (readDataString(NUMBER).equals(stBecky) )
        {
            Log.d(TAG, "IN Becky1992 " + readDataString(NUMBER));
            btnSave.setBackgroundColor(Color.RED);
        }

        if (readDataString(NUMBER).equals(stMiguel) )
        {
            btnSave.setBackgroundColor(Color.CYAN);
        }

        edtTextNumber.setText(readDataString(NUMBER));
        edtTextNumber.clearFocus();



    }

    public void resetCnt(View v)
    {
        saveDataCtn(NIGHT, 100);
        saveDataCtn(MORNING, 100);
        saveDataCtn(AFTERNOON, 100);

        Toast.makeText(MainActivity.this, "Counters have been reset", Toast.LENGTH_LONG).show();
    }

    public void saveNumber(View v)
    {

        final String stBecky = "503906760";
        final String stMiguel = "789170386";



        final Button btnSave = (Button) findViewById(R.id.btnSave);
        EditText edtTextNumber = (EditText) findViewById(R.id.editTextPhone);
        final String stNumber = edtTextNumber.getText().toString();

        //if (stNumber.equals(stBecky) || stNumber.equals(stMiguel))
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    switch (which)
                    {
                        case DialogInterface.BUTTON_POSITIVE:

                            mDatabaseHelper.addData(("----- " + mDatabaseHelper.getCurrentExactTime()), stNumber );
                            saveDataString(NUMBER,stNumber);

                            if (readDataString(NUMBER).equals(stMiguel) )
                            {
                                btnSave.setBackgroundColor(Color.CYAN);
                            }

                            if (readDataString(NUMBER).equals(stBecky) )
                            {
                                Log.d(TAG, "IN Becky1992 " + readDataString(NUMBER));
                                btnSave.setBackgroundColor(Color.RED);
                            }

                            Log.d(TAG, readDataString(NUMBER));
                            Toast.makeText(MainActivity.this, "Number saved successfully!", Toast.LENGTH_LONG).show();


                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            Toast.makeText(MainActivity.this, "Number not saved\nCurrent number: \n\n" + readDataString(NUMBER), Toast.LENGTH_LONG).show();
                            break;
                    }


                }
            };

            TextView myMsg = new TextView(this);
            myMsg.setText("\nMessages will be sent to number: \n\n" + stNumber + "\n\n Do you confirm?");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            myMsg.setTextColor(Color.BLACK);
            myMsg.setTextSize(15);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(myMsg).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();



        }
//        else
//        {
//            Toast.makeText(MainActivity.this, "Number doesn't match permitted numbers", Toast.LENGTH_LONG).show();
//        }







    }

    public void scheduleJob(View v)
    {
        Button btnSchedule = (Button) findViewById(R.id.btnS);
        Button btnCanceled = (Button) findViewById(R.id.btnC);




        ComponentName componentName = new ComponentName(this, com.example.sms_scheduler_final.SMSJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)

                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(true)
                .setPeriodic(63 * 60 * 1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS)
        {
            Log.d(TAG, "Job scheduled");

            mDatabaseHelper.addData(("----- " + mDatabaseHelper.getCurrentExactTime()), "Job scheduled");
        }
        else
        {
            Log.d(TAG, "Job Failed");
        }

        saveData(STATUS,1);


        btnCanceled.setBackgroundColor(Color.GRAY);
        btnSchedule.setBackgroundColor(Color.GREEN);

        Toast.makeText(MainActivity.this, "Job Scheduled", Toast.LENGTH_LONG).show();

    }

    public void cancelJob(View v)
    {

        Button btnSchedule = (Button) findViewById(R.id.btnS);
        Button btnCanceled = (Button) findViewById(R.id.btnC);

        JobScheduler scheduler = (JobScheduler) getSystemService((JOB_SCHEDULER_SERVICE));
        scheduler.cancel(123);
        Log.d(TAG, "Job Cancelled");
        mDatabaseHelper.addData(("----- " + mDatabaseHelper.getCurrentExactTime()), "Job Cancelled");

        saveData(STATUS,0);

        btnCanceled.setBackgroundColor(Color.GREEN);
        btnSchedule.setBackgroundColor(Color.GRAY);

        Toast.makeText(MainActivity.this, "Job Cancelled", Toast.LENGTH_LONG).show();

    }

    public void loadXML(View v)
    {
        Button btnLoadXML = (Button) findViewById(R.id.btnXML);
        btnLoadXML.setBackgroundColor(Color.GREEN);
        ArrayList<Messages> messagesArrayList = null;

        try
        {
            GetXML task = new GetXML();
            task.execute("https://mmiszkurka.pl/MsgList.xml");
            messagesArrayList = task.get();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Log.d(TAG, "succes");
        Toast.makeText(this, "XML Data Loaded", Toast.LENGTH_SHORT).show();



        for (int i=0; i<messagesArrayList.size(); i++)
        {

            int dbID = messagesArrayList.get(i).getID();
            String dbMsgTxt = messagesArrayList.get(i).getMsgTxt();
            int dbGroupID = messagesArrayList.get(i).getGroupID();

            mDatabaseHelper.addDataMsg(dbID, dbMsgTxt, dbGroupID);
            mDatabaseHelper.addData("XML: ",dbID + " " + dbMsgTxt + " " + dbGroupID);

        }


    }

    // View Log Table Data

    public void viewLog(View v){

        Intent intent = new Intent(MainActivity.this, ListLogActivity.class);
        startActivity(intent);
    }

    public void saveData(String key, int value)
    {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    public void saveDataString(String key, String valueString)
    {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, valueString);
        editor.apply();
    }

    public void saveDataCtn(String key, int valueString)
    {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_CNT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, valueString);
        editor.apply();
    }

    public int readData(String key)
    {
        final int result;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        result = sharedPreferences.getInt(key,0);

        return result;

    }
    public String readDataString(String key)
    {
        final String result;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        result = sharedPreferences.getString(key,"");

        return result;

    }



}