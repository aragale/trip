package com.example.yuze.bysjdemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dbHelper = new DBHelper(this, "HR_DB.db3", 1);
        Button register = (Button) findViewById(R.id.rregister);
        final EditText txusername = (EditText) findViewById(R.id.rusername);
        final EditText txpasswd = (EditText) findViewById(R.id.rpasswd);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txusername.getText().toString();
                String pwd = txpasswd.getText().toString();
                String sql = "insert into Users(name,pwd)values('" + name + "','" + pwd + "')";
                db = dbHelper.getWritableDatabase();
                db.execSQL(sql);
                Toast.makeText(Register.this, "注册成功", 1000).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
