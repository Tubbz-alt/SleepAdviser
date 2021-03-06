package com.santiagoapps.sleepadviser.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.santiagoapps.sleepadviser.data.DatabaseManager;
import com.santiagoapps.sleepadviser.data.model.Session;
import com.santiagoapps.sleepadviser.data.model.User;
import com.santiagoapps.sleepadviser.helpers.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.santiagoapps.sleepadviser.data.model.Session.*;
import static com.santiagoapps.sleepadviser.helpers.DBHelper.*;

/**
 * Created by Ian on 1/23/2018.
 */

public class SessionRepo {

    public static String createTable(){
        return String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER," + //KEY_USER_FIREBASE_ID
                        "%s DATETIME, " +   //KEY_SLEEP_DATE
                        "%s TEXT, " +   //KEY_WAKE_DATE
                        "%s INT, " +   //KEY_SLEEP_RATING
                        "%s TEXT, " +   //KEY_SLEEP_DURATION
                        "%s DATETIME)", //KEY_CREATED_AT
                Session.TABLE,
                KEY_ID, KEY_USER_ID, KEY_SLEEP_DATE, KEY_WAKE_DATE,
                KEY_SLEEP_RATING, KEY_SLEEP_DURATION, KEY_CREATED_AT);
    }

    public SessionRepo(){}

    /************** INSERT METHODS ******************/


    /**
     * Insert sleep session to the database
     *
     * @param session This is the sleepSession object containing all sleep-related dates
     * @return result : -1 if failed else; success
     */
    public long insertSession(Session session){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, session.getUserId());
        values.put(KEY_SLEEP_DATE, DateHelper.dateToSqlString(session.getSleepDate().getTime()));
        values.put(KEY_WAKE_DATE, DateHelper.dateToSqlString(session.getWakeDate().getTime()));
        values.put(KEY_SLEEP_DURATION, session.getSleep_duration());
        values.put(KEY_SLEEP_RATING, session.getSleepQuality());

        long result = db.insert(TABLE, null, values);

        if(result!=-1){
            Log.d(TAG,"Sleep session successfully registered!");
        } else {
            Log.e(TAG,"Error inserting data");
        }

        return result;
    }




    /**************** FETCH METHODS ******************/
    /** get all sessions from local db **/
    public List<Session> getAllSession(){

        List<Session> session_list = new ArrayList<>();

        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " ORDER BY DATE(" + KEY_SLEEP_DATE + ") ASC";
        Log.e(TAG, query);

        Cursor res = db.rawQuery(query,null);

        while(res.moveToNext()){
            Session session = new Session();
            session.setId(Integer.parseInt(res.getString(res.getColumnIndex(KEY_ID))));
            session.setUserId((res.getString(res.getColumnIndex(KEY_USER_ID))));
            session.setSleepDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_SLEEP_DATE))));
            session.setWakeDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_WAKE_DATE))));
            session.setSleepDuration(res.getString(res.getColumnIndex(KEY_SLEEP_DURATION)));
            session.setSleepQuality(Integer.parseInt(res.getString(res.getColumnIndex(KEY_SLEEP_RATING))));
            session_list.add(session);
        }

        return session_list;
    }

    public List<Session> getSessionByMonth(int month){
        List<Session> matchedList = new ArrayList<>();

        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE + " ORDER BY DATE(" + KEY_SLEEP_DATE + ")";
        Log.e(TAG, query);

        Cursor res = db.rawQuery(query,null);

        while(res.moveToNext()){
            Session session = new Session();
            session.setId(Integer.parseInt(res.getString(res.getColumnIndex(KEY_ID))));
            session.setUserId((res.getString(res.getColumnIndex(KEY_USER_ID))));
            session.setSleepDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_SLEEP_DATE))));
            session.setWakeDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_WAKE_DATE))));
            session.setSleepDuration(res.getString(res.getColumnIndex(KEY_SLEEP_DURATION)));
            session.setSleepQuality(Integer.parseInt(res.getString(res.getColumnIndex(KEY_SLEEP_RATING))));

            if(DateHelper.getMonth(session.getSleepDate()) == month){
                matchedList.add(session);
            }

        }

        return matchedList;
    }

    public List<Session> getSessionOfCurrentWeek(){
        List<Session> matchedList = new ArrayList<>();

        SQLiteDatabase db =  DatabaseManager.getInstance().openDatabase();
        String query = "SELECT * FROM " + TABLE;
        Log.e(TAG, query);

        Cursor res = db.rawQuery(query,null);

        while(res.moveToNext()){
            Session session = new Session();
            session.setId(Integer.parseInt(res.getString(res.getColumnIndex(KEY_ID))));
            session.setUserId((res.getString(res.getColumnIndex(KEY_USER_ID))));
            session.setSleepDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_SLEEP_DATE))));
            session.setWakeDate(DateHelper.stringToSqlDate(res.getString(res.getColumnIndex(KEY_WAKE_DATE))));
            session.setSleepDuration(res.getString(res.getColumnIndex(KEY_SLEEP_DURATION)));
            session.setSleepQuality(Integer.parseInt(res.getString(res.getColumnIndex(KEY_SLEEP_RATING))));

            if(session.getSleepDate().get(Calendar.WEEK_OF_YEAR) == DateHelper.getCurrentWeek()){
                matchedList.add(session);
            }
        }

        return matchedList;
    }

    /** return session count **/

    public int getSessionCount(){
        SQLiteDatabase x = DatabaseManager.getInstance().openDatabase();
        Cursor res = x.rawQuery("select * from " + TABLE, null);
        int count = res.getCount();
        res.close();
        return count;
    }

    public int getLastInsertedId(){
        SQLiteDatabase x = DatabaseManager.getInstance().openDatabase();
        Cursor res = x.rawQuery("select * from " + TABLE, null);
        res.moveToLast();

        int id = res.getInt(res.getColumnIndex(KEY_ID));

        return id;
    }



    /************** DELETE METHODS *****************/

    /** delete all session data **/
    public boolean resetSessionTable(){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        int rowsAffected = db.delete(TABLE,"1",null);
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{TABLE});
        return rowsAffected > 0;
    }
}
