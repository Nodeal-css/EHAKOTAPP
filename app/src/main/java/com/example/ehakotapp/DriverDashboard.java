package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class DriverDashboard extends AppCompatActivity {
    EditText editText2;
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private Button btnViewBooking, btnAddVehicle, btnOngoing, btnVehicles, btnHistory;
    private ImageView imgProfile;
    private TextView txtFullname, txtAddress, txtDriverID;
    private RelativeLayout layoutProfileDriver;
    //TODO: (May 14, 2022) replace Toast confimation with Dialog Boxes | reference - DriverApproveBooking.java
    // Implement this comment to the next session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //initialize components from xml
        btnViewBooking = (Button) findViewById(R.id.btnViewBooking);
        btnAddVehicle = (Button) findViewById(R.id.btnAddVehicle);
        btnOngoing = (Button) findViewById(R.id.btnOngoing);
        btnVehicles = (Button) findViewById(R.id.btnVehicles);
        btnHistory = (Button) findViewById(R.id.btnHistory);
        layoutProfileDriver = (RelativeLayout) findViewById(R.id.layoutProfileDriver);
        //Profile
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        txtFullname = (TextView) findViewById(R.id.txtFullname);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtDriverID = (TextView) findViewById(R.id.txtDriverID);

        //editText2 = (EditText) findViewById(R.id.editText2);
        String email = getIntent().getStringExtra("email");
        int driver_id = getDriverID(getID(email));
        //Toast.makeText(DriverDashboard.this, "email: " + email + "\ndriver_id: " + getDriverID(getID(email)) + "\nuser_id: " + getID(email), Toast.LENGTH_LONG).show();

        //Load the profile into the dashboard
        try {
            myCursor = db.rawQuery("SELECT `image_path`, `firstname`, `lastname`, `address` FROM `user_account` WHERE `user_id` = '"+getID(email)+"'", null);
            if(myCursor.moveToFirst()){
                //Toast.makeText(DriverDashboard.this, "image_path: " + myCursor.getString(0), Toast.LENGTH_LONG).show();
                File imgFile = new File(myCursor.getString(0));
                imgProfile.setImageURI(Uri.fromFile(imgFile));
                txtFullname.setText(myCursor.getString(1) + " " + myCursor.getString(2));
                txtAddress.setText(myCursor.getString(3));
                txtDriverID.setText("Driver ID: " + getDriverID(getID(email)));
            }
        }catch (Exception e){
            Toast.makeText(DriverDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboard.this, DriverAddVehicle.class);
                intent.putExtra("driver_id", driver_id);
                startActivity(intent);
            }
        });

        btnVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverDashboard.this, DriverVehicles.class);
                intent.putExtra("driver_id", driver_id);
                startActivity(intent);
            }
        });

        btnViewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(DriverDashboard.this, DriverViewBooking.class);
                intent1.putExtra("driver_id", driver_id);
                startActivity(intent1);
            }
        });

        //TODO: may 15, create the DriverOngoing.Activity
        btnOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSlotInEmpty(driver_id)){
                    Toast.makeText(DriverDashboard.this, "Filled Slot_in:\nproceed to next page", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(DriverDashboard.this, DriverOngoing.class);
                    //intent parameters in the next session | intent driver_id
                    intent4.putExtra("driver_id", driver_id);
                    startActivity(intent4);
                }else{
                    Toast.makeText(DriverDashboard.this, "Empty Slot_in:\nDo not proceed", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent1 = new Intent(DriverDashboard.this, DriverHistory.class);
                mIntent1.putExtra("driver_id", driver_id);
                startActivity(mIntent1);
            }
        });

        layoutProfileDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent4 = new Intent(DriverDashboard.this, DriverProfile.class);
                myIntent4.putExtra("driver_id", driver_id);
                startActivity(myIntent4);
            }
        });
    }

    //to get the user_id from user account
    private int getID(String email){
        int id = 0;
        try {
            myCursor = db.rawQuery("SELECT `user_id` from `user_account` WHERE email = '" + email + "'", null);
            if (myCursor.moveToFirst()) {
                id = myCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //to get the driver id in driver table
    private int getDriverID(int user){
        int client = 0;
        try {
            myCursor = db.rawQuery("SELECT `driver_id` from `driver` WHERE `user_id` = '" + user + "'", null);
            if (myCursor.moveToFirst()) {
                client = myCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return client;
    }

    // This method returns true if there is no ongoing slot_in
    private boolean isSlotInEmpty(int driver_id){
        boolean flag = true;
        try {
            Cursor mcursor2 = db.rawQuery("SELECT * FROM slot_in WHERE driver_id = '"+driver_id+"'", null);
            if(mcursor2.moveToFirst()){
                flag = false;
            }
        }catch (Exception e){
            Toast.makeText(DriverDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return flag;
    }
}