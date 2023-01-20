package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DriverHistory extends AppCompatActivity {
    private ListView listViewDriverHistory;
    private MyDB mydb1;
    private SQLiteDatabase db;

    //TODO: create the view profile for client and driver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listViewDriverHistory = (ListView) findViewById(R.id.listViewDriverHistory);

        //Important ID
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        Toast.makeText(DriverHistory.this, "Driver ID: " + driver_id, Toast.LENGTH_SHORT).show();
        ArrayList<History> arrayList = new ArrayList<>();
        try {
            Cursor mCursor = db.rawQuery("SELECT vehicle.vehicle_id, report.book_id, report.datetime_completed, payment.total_amount from report, payment, booking, vehicle, driver WHERE booking.vehicle_id = vehicle.vehicle_id AND vehicle.driver_id = driver.driver_id AND booking.book_id = report.book_id AND report.book_id = payment.book_id AND driver.driver_id = '"+driver_id+"'", null);
            if(mCursor.moveToFirst()){
                for(int i = 0; i < mCursor.getCount(); i++) {
                    arrayList.add(new History(mCursor.getInt(1), mCursor.getString(2), mCursor.getInt(3)));
                    mCursor.moveToNext();
                }
            }
        }catch (Exception e){
            Toast.makeText(DriverHistory.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        HistoryAdapter adapter = new HistoryAdapter(this, R.layout.list_client_history, arrayList);
        listViewDriverHistory.setAdapter(adapter);
    }
}