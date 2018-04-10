package com.example.yuze.bysjdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuze on 2018/4/10.
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CreateTableSpl = "create table Users("
                + " id integer primary key autoincrement,"
                + " name varchar,"
                + " pwd varchar)";
        db.execSQL(CreateTableSpl);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
}
