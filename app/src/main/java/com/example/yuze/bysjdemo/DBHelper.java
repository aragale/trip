package com.example.yuze.bysjdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuze on 2018/4/12.
 */

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase dbAndTripLog;

    public DBHelper(Context context) {
        super(context, Location.DATABASE_NAME, null, Location.DATABASE_VERSION);
        //super(context, name, null, version);
    }

    //创建数据库
    private void doDatabase(SQLiteDatabase db, int fromVersion) {
        if (fromVersion < 6) {
            db.execSQL("CREATE TABLE Trips (id integer primary key AUTOINCREMENT,start_date timestamp,stop_date timestamp);");
            db.execSQL("CREATE TABLE TripsLogs (id integer primary key AUTOINCREMENT,tripId integer,stamp timestamp,position text,longitude double,latitude double,altitude double);");
        }
        if (fromVersion < 7) {
            db.execSQL("ALTER TABLE Trips ADD trip_meter float;");
            db.execSQL("ALTER TABLE Trips ADD trip_duration long;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        doDatabase(db, 0);
    }

    @Override
    //更新数据记录
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("DROP TABLE IF EXISTS Trips");
            db.execSQL("DROP TABLE IF EXISTS TripsLogs");
        }
        if (oldVersion > newVersion) {
            onCreate(db);
        } else {
            doDatabase(db, oldVersion);
        }
    }

    //写入数据库
    public void startDatabase() {
        dbAndTripLog = getWritableDatabase();
    }

    public int startAnewTrip() {
        int current_trip;
        Cursor lst = dbAndTripLog.rawQuery("select max(id) from Trips", null);
        if (lst.moveToFirst()) {
            current_trip = lst.getInt(0) + 1;//tripId
        } else {
            current_trip = 1;
        }
        dbAndTripLog.execSQL("INSERT INTO Trips (id,start_date) VALUES (" + current_trip + ",current_timestamp);");
        return current_trip;
    }

    //删除单条记录
    public void deleteTrip(long delete_id) {
        dbAndTripLog.execSQL("delete from tripslogs where tripid = " + delete_id);
        dbAndTripLog.execSQL("delete from trips where id = " + delete_id);
    }

    //删除所有记录
    public void deleteAllTrip() {
        dbAndTripLog.execSQL("delete from tripslogs");
        dbAndTripLog.execSQL("delete from trips");
    }

    public Cursor getAllTripId() {
        return dbAndTripLog.rawQuery("select id from trips", null);
    }

    public Cursor getTripId(long tripId) {
        return dbAndTripLog.rawQuery("select id from trips where id = " + tripId, null);
    }

    public Cursor getTripData(long id) {
        return dbAndTripLog.rawQuery("select latitude,longitude,altitude,strftime('%Y-%m-%dT%H:%M:%SZ',stamp) as stamp from tripslogs where tripid = " + id, null);
    }

    public int getAllTripTrkCount() {
        Cursor l = dbAndTripLog.rawQuery("select count(*) from trips t join tripslogs l on (t.id=l.tripId)", null);
        l.moveToPosition(0);
        return l.getInt(0);
    }

    public int getTripTrkCount(long tripId) {
        Cursor l = dbAndTripLog.rawQuery("select count(*) from tripslogs where tripId =" + tripId, null);
        l.moveToPosition(0);
        return l.getInt(0);
    }

    //新建位置数据库
    public void newPointDB(double longitude, double latitude, double altitude, long gps_time, int current_trip) {
        dbAndTripLog.execSQL("INSERT INTO TripsLogs (tripId,stamp,position,longitude,latitude,altitude) VALUES (" + current_trip + ",datetime(" + gps_time + "/1000, 'unixepoch'),current_timestamp," + longitude + "," + latitude + "," + altitude + ");");
    }

    public void stopTrip(long last_timestamp, long first_timestamp, float total_distance, int current_trip) {
        dbAndTripLog.execSQL("UPDATE Trips set stop_date = current_timestamp,trip_duration = " + (last_timestamp - first_timestamp) + ",trip_meter = " + total_distance + " where id = " + current_trip);
    }

    public Cursor getListing() {
        return dbAndTripLog.rawQuery("select t.id as _id,t.id||' - '||start_date as cmt,count(tl.id)||' positions,'||coalesce(trip_meter,'?')||' meters'||','||coalesce((trip_duration/1000),'?')||' seconds' as descr from Trips t left outer join TripsLogs tl on (t.id=tl.tripId) group by t.id,start_date order by start_date desc", null);
    }

    public void end() {
        dbAndTripLog.close();
    }
}
