package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DriverVehicles extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private ListView listView;

    //TODO: set on click listener for vehicles & an update feature for vehicles
    //TODO: inspect the usage of global myCursor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_vehicles);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listView = findViewById(R.id.listVehicles1);

        //important id/s
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        Toast.makeText(DriverVehicles.this, "Driver ID: " + driver_id, Toast.LENGTH_LONG).show();

        ArrayList<Vehicle> arrayList = new ArrayList<>();

        //Add data
        //arrayList.add(new Vehicle("/storage/emulated/0/DCIM/Camera/IMG_20220419_175151.jpg", "Isuzu", "Delivery Truck", "Quezon City", "1"));
        try {
            myCursor = db.rawQuery("SELECT `image_path`, `model`, `vehicle_type`, `vehicle_location`, `vehicle_id` FROM `vehicle` WHERE `driver_id` = '"+driver_id+"'", null);
            if(myCursor.moveToFirst()) {
                for (int i = 0; i < myCursor.getCount(); i++) {
                    arrayList.add(new Vehicle(myCursor.getString(0).toString(), myCursor.getString(1).toString(), myCursor.getString(2).toString(), myCursor.getString(3).toString(), myCursor.getString(4).toString()));
                    myCursor.moveToNext();
                }
            }

        }catch (Exception e){
            Toast.makeText(DriverVehicles.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //set up adapter
        VehicleAdapter adapter = new VehicleAdapter(this, R.layout.list_vehicles_template, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor mCursor = db.rawQuery("SELECT `image_path`, `model`, `vehicle_type`, `vehicle_location`, `vehicle_id` FROM `vehicle` WHERE `driver_id` = '"+driver_id+"'", null);
                mCursor.moveToFirst();
                mCursor.move(i);
                //Toast.makeText(DriverVehicles.this, "Vehicle_ID: " + mCursor.getString(4), Toast.LENGTH_LONG).show();
                //Intent with vehicle_id
                Intent mIntent3 = new Intent(DriverVehicles.this, DriverUpdateVehicle.class);
                mIntent3.putExtra("vehicle_id", mCursor.getInt(4));
                startActivity(mIntent3);
            }
        });

    }
}