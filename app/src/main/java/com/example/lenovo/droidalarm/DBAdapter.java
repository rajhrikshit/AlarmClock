package com.example.lenovo.droidalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Lenovo on 03-Oct-17.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
 class DBAdapter {

     public static final String KEY_ROWID="rId";

    //Setup fields for your columns
    public static final String KEY_ALARMID="aId";
    public static final String KEY_LABEL="label";
    public static final String KEY_HOUR="hour";
    public static final String KEY_MINUTES="minutes";
    public static final String KEY_STATE="state";
    public static final String KEY_RINGTONE="ring";
    public static final String KEY_OFFMETHOD="off";
    public static final String KEY_SHAKECOUNT="shakecnt";
    public static final String KEY_TAPCOUNT="tapcnt";
    public static final String KEY_SUNDAY="sun";
    public static final String KEY_MONDAY="mon";
    public static final String KEY_TUESDAY="tue";
    public static final String KEY_WEDNESDAY="wed";
    public static final String KEY_THURSDAY="thu";
    public static final String KEY_FRIDAY="fri";
    public static final String KEY_SATURDAY="sat";
    public static final String KEY_SNOOZE="snooze";
    public static final String KEY_MATH="math";
    public static final String KEY_FORCE="force";



    public static final int COL_ALARMID=1;
    public static final int COL_LABEL=2;
    public static final int COL_HOUR=3;
    public static final int COL_MINUTES=4;
    public static final int COL_STATE=5;
    public static final int COL_RINGTONE=6;
    public static final int COL_OFFMETHOD=7;
    public static final int COL_SHAKECOUNT=8;
    public static final int COL_TAPCOUNT=9;
    public static final int COL_SUNDAY=10;
    public static final int COL_MONDAY=11;
    public static final int COL_TUESDAY=12;
    public static final int COL_WEDNESDAY=13;
    public static final int COL_THURSDAY=14;
    public static final int COL_FRIDAY=15;
    public static final int COL_SATURDAY=16;
    public static final int COL_SNOOZE=17;
    public static final int COL_MATH=18;
    public static final int COL_FORCE=19;




    public static final String[] ALL_KEYS={KEY_ROWID,KEY_ALARMID,KEY_LABEL,KEY_HOUR,KEY_MINUTES,KEY_STATE,
            KEY_RINGTONE,KEY_OFFMETHOD,KEY_SHAKECOUNT,KEY_TAPCOUNT,KEY_SUNDAY,KEY_MONDAY,KEY_TUESDAY,
            KEY_WEDNESDAY,KEY_THURSDAY,KEY_FRIDAY,KEY_SATURDAY,KEY_SNOOZE,KEY_MATH,KEY_FORCE};

    public static final String DATABASE_NAME="dName";
    public static final String DATABASE_TABLENAME="dTabName";
    public static final int DATABASE_VERSION=19;

    public static final String DATABASE_CREATE_SQL=
            "create table "+DATABASE_TABLENAME
            +" (" + KEY_ROWID + " integer primary key autoincrement, "

            +KEY_ALARMID + " integer not null, "
            +KEY_LABEL + " string not null, "
                    +KEY_HOUR + " integer not null, "
                    +KEY_MINUTES + " integer not null, "
                    +KEY_STATE + " integer not null, "
                    +KEY_RINGTONE + " string not null, "
                    +KEY_OFFMETHOD + " string not null, "
                    +KEY_SHAKECOUNT + " integer not null, "
                    +KEY_TAPCOUNT + " integer not null, "
                    +KEY_SUNDAY + " integer not null, "
                    +KEY_MONDAY + " integer not null, "
                    +KEY_TUESDAY + " integer not null, "
                    +KEY_WEDNESDAY + " integer not null, "
                    +KEY_THURSDAY + " integer not null, "
                    +KEY_FRIDAY + " integer not null, "
                    +KEY_SATURDAY + " integer not null, "
                    +KEY_SNOOZE + " integer not null, "
                    +KEY_MATH + " integer not null, "
                    +KEY_FORCE + " integer not null"
            + ");";

    private final Context context;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context=ctx;
        this.mDBHelper=new DatabaseHelper(context);
    }

    //Open the database connection

    public DBAdapter open() {
        db=mDBHelper.getWritableDatabase();
        return this;
    }

    //Close the database connection.
    public void close() {
        mDBHelper.close();
    }

    //ADD a row

    public long insertRow(int aid,String label,int hour, int minutes,boolean state,String ring,
                          String offMeth, int shkCnt, int tapcnt, int su, int m, int tu,int w, int th,
                          int f, int sa, int sno, int mat, int force) {

        ContentValues initialValues=new ContentValues();
        initialValues.put(KEY_ALARMID,aid);
        initialValues.put(KEY_LABEL,label);
        initialValues.put(KEY_HOUR,hour);
        initialValues.put(KEY_MINUTES,minutes);
        if(state) {
            initialValues.put(KEY_STATE, 1);
        }else {
            initialValues.put(KEY_STATE, 0);

        }
        initialValues.put(KEY_RINGTONE,ring);
        initialValues.put(KEY_OFFMETHOD,offMeth);
        initialValues.put(KEY_SHAKECOUNT,shkCnt);
        initialValues.put(KEY_TAPCOUNT,tapcnt);
        initialValues.put(KEY_SUNDAY,su);
        initialValues.put(KEY_MONDAY,m);
        initialValues.put(KEY_TUESDAY,tu);
        initialValues.put(KEY_WEDNESDAY,w);
        initialValues.put(KEY_THURSDAY,th);
        initialValues.put(KEY_FRIDAY,f);
        initialValues.put(KEY_SATURDAY,sa);
        initialValues.put(KEY_SNOOZE,sno);
        initialValues.put(KEY_MATH,mat);
        initialValues.put(KEY_FORCE,force);


        return db.insert(DATABASE_TABLENAME,null,initialValues);
    }
    public boolean deleteRow(long rowId){
        String where=KEY_ROWID+"="+rowId;
        return db.delete(DATABASE_TABLENAME,where,null)!=0;
    }
    public Cursor getAllRows(){
        Cursor c=db.query(true,DATABASE_TABLENAME,ALL_KEYS,null,null,null,null,null,null);

        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRow(long rowId){
        String where=KEY_ROWID+"="+rowId;
        Cursor c=db.query(true,DATABASE_TABLENAME,ALL_KEYS,where,null,null,null,null,null);

        if(c!=null){
            c.moveToFirst();
        }
        return c;

    }
    public boolean updateRow(long rowid,int aid,String label,int hour, int minutes,boolean state,
                             String ring, String offMeth, int shkCnt, int tapcnt, int su, int m,
                             int tu,int w, int th, int f, int sa, int sno, int mat, int force){
        String where=KEY_ROWID+"="+rowid;
        ContentValues newValues=new ContentValues();
        Cursor c=getRow(rowid);
        if(c.moveToFirst()){
            newValues.put(KEY_ALARMID,aid);
            newValues.put(KEY_LABEL,label);
            newValues.put(KEY_HOUR,hour);
            newValues.put(KEY_MINUTES,minutes);
            if(state){
                newValues.put(KEY_STATE, 1);
            }else {
                newValues.put(KEY_STATE, 0);

            }
            newValues.put(KEY_RINGTONE,ring);
            newValues.put(KEY_OFFMETHOD,offMeth);
            newValues.put(KEY_SHAKECOUNT,shkCnt);
            newValues.put(KEY_TAPCOUNT,tapcnt);
            newValues.put(KEY_SUNDAY,su);
            newValues.put(KEY_MONDAY,m);
            newValues.put(KEY_TUESDAY,tu);
            newValues.put(KEY_WEDNESDAY,w);
            newValues.put(KEY_THURSDAY,th);
            newValues.put(KEY_FRIDAY,f);
            newValues.put(KEY_SATURDAY,sa);
            newValues.put(KEY_SNOOZE,sno);
            newValues.put(KEY_MATH,mat);
            newValues.put(KEY_FORCE,force);
        }
        return db.update(DATABASE_TABLENAME,newValues,where,null)!=0;
    }

    public int getLastId(){
        Cursor cursor = db.rawQuery("SELECT seq FROM sqlite_sequence WHERE name=?",
                new String[] { DATABASE_TABLENAME });
        int last = (cursor.moveToFirst() ? cursor.getInt(0) : 0);
        return last;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //Destroy old Data
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLENAME);

            //Recreate new Database

            onCreate(sqLiteDatabase);
        }
    }
}
