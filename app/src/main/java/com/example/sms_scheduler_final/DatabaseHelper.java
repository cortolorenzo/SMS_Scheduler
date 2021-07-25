package com.example.sms_scheduler_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "text";

    private static final String LOGIN_TABLE_NAME = "tbl_LogIn";
    private static final String COL_USER = "User";

    private static final String DB_NAME = "MyDB";

    private static final String TABLE_NAME = "SMS_Log_Table";
    private static final String COL0 = "ID";
    private static final String COL1 = "Send_Time";
    private static final String COL2 = "Message";

    private static final String TABLE_MSG = "SMS_MESSAGES";
    private static final String COL_ID = "ID";
    private static final String COL_MSGTXT = "MsgTxt";
    private static final String COL_GROUPID = "GroupID";
    private static final String COL_SENT = "Sent";






    public DatabaseHelper(@Nullable Context context){
        super(context, DB_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," + COL1 + " TEXT," + COL2 + " TEXT)";
        String createMsgTable = "CREATE TABLE " + TABLE_MSG + " (ID INTEGER PRIMARY KEY, " + COL_MSGTXT + " TEXT," + COL_GROUPID + " INTEGER," + COL_SENT + " INTEGER)";
        db.execSQL(createTable);
        db.execSQL(createMsgTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSG);
        //onCreate(db);

    }





    public void addData(String data, String msg){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, data);
        contentValues.put(COL2, msg);

        Log.d(TAG, "Add Data to " + TABLE_NAME + " " + data + " " + msg);
        long result = db.insert(TABLE_NAME, null, contentValues);

        Log.d(TAG, String.valueOf(result));

    }


    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC";
        Cursor data = db.rawQuery(query,null);
        return data;

    }


    public String getCurrentExactTime()
    {
        String currentExactTime;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        // current time in 0-23
        currentExactTime = sdf.format(cal.getTime());


        return currentExactTime;

    }
    public void CheckIfAllSent(int groupID)
    {
        String sqlquery = "SELECT * FROM SMS_MESSAGES WHERE GroupID = " + groupID + " AND Sent = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlquery,null);
        int allSent = cursor.getCount();

        if (allSent <= 0)
        {
            sqlquery = "UPDATE SMS_MESSAGES SET Sent = 0 WHERE GroupID = " + groupID;
            db.execSQL(sqlquery);
        }

        String leftMessCnt = "Pozostało "+ allSent + " wiadomości do wysłania przed resetem, grupa: " +groupID;
        addData("LEFT Msg: ", leftMessCnt);

    }

    public void addDataMsg(int ID, String msgTxt, int groupID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlquery;

        try
        {
            sqlquery = "INSERT INTO " + TABLE_MSG + " (" +COL_ID +", "+COL_MSGTXT+", "+COL_GROUPID +", "+COL_SENT+" ) VALUES ("+ID+", "+"'"+msgTxt+"'"+", "+groupID+", 0)";
            db.execSQL(sqlquery);
        }
        catch   (Exception ex)
        {
            Log.d(TAG, ex.getMessage());
            sqlquery = "UPDATE SMS_MESSAGES SET MsgTxt = "+"'"+msgTxt+"'"+" WHERE "+ COL_ID + " = " + ID;
            db.execSQL(sqlquery);
        }

        Log.d(TAG, "Add Data to " + TABLE_MSG + " " + ID + " " + msgTxt + " " + groupID );

    }

    public void setSent(List<Messages> txt)
    {
        int IDmess = txt.get(0).getID();
        //int groupID = txt.get(2).getGroupID();

        String sqlquery = "UPDATE SMS_MESSAGES SET Sent = 1 WHERE ID = " + IDmess ;

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlquery);


    }
    public List<Messages> MessagesList (int partDay)
    {
        List<Messages> ListMess = new ArrayList<Messages>();
        int groupid = partDay;
        String sqlquery = "SELECT * FROM SMS_MESSAGES WHERE GroupID = "+ partDay + " AND Sent = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlquery,null);

        if (cursor.moveToFirst())
        do {
            Messages operatorTable = new Messages();
            operatorTable.setID(cursor.getInt(0));
            operatorTable.setMsgTxt(cursor.getString(1));
            operatorTable.setGroupID(cursor.getInt(2));
            operatorTable.setSent(cursor.getInt(3));

            ListMess.add(operatorTable);


        } while (cursor.moveToNext());

        return ListMess;

    }



}
