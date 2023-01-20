package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class DriverOngoing extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private ImageView imgClientDriverOngoing, imgDriverVehicleOngoing;
    private TextView txtClientNameOngoing, txtClientEmailOngoing, txtDriverModelOngoing;
    private EditText editDriverPickupOngoing, editDriverDeliveryOngoing, editDriverRemarksOngoing;
    private Button btnCompleteBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ongoing);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important ID/s
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        int book_id = getBookID(driver_id);
        int client_id = getClientID(book_id);
        int vehicle_id = getVehicleID(book_id);
        //Toast.makeText(DriverOngoing.this, "Driver ID: " + driver_id + "\nBook id: " + book_id + "\nclient id: " + client_id + "\nvehicle ID: " + vehicle_id, Toast.LENGTH_LONG).show();

        //Initalize XML components
        imgClientDriverOngoing = (ImageView) findViewById(R.id.imgClientDriverOngoing);
        imgDriverVehicleOngoing = (ImageView) findViewById(R.id.imgDriverVehicleOngoing);
        txtClientNameOngoing = (TextView) findViewById(R.id.txtClientNameOngoing);
        txtClientEmailOngoing = (TextView) findViewById(R.id.txtClientEmailOngoing);
        txtDriverModelOngoing = (TextView) findViewById(R.id.txtDriverModelOngoing);
        editDriverPickupOngoing = (EditText) findViewById(R.id.editDriverPickupOngoing);
        editDriverDeliveryOngoing = (EditText) findViewById(R.id.editDriverDeliveryOngoing);
        editDriverRemarksOngoing = (EditText) findViewById(R.id.editDriverRemarksOngoing);
        btnCompleteBooking = (Button) findViewById(R.id.btnCompleteBooking);

        //Alert Dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverOngoing.this);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setTitle("Confirmation");
        alert.setMessage("Confirm if the booking has been completed");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String query1 = "UPDATE `booking` SET `status` = 'PAYMENT' WHERE `book_id` = '"+book_id+"'";
                String query2 = "DELETE FROM `slot_in` WHERE `book_id` = '"+book_id+"'";
                try {
                    db.execSQL(query1);
                    db.execSQL(query2);
                    Toast.makeText(DriverOngoing.this, "Successfully completed", Toast.LENGTH_LONG).show();
                    DriverOngoing.super.onBackPressed();
                }catch (Exception e){
                    Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DriverOngoing.this, "return", Toast.LENGTH_LONG).show();
            }
        });

        //Load Client info
        loadClientInfo(client_id);

        //Load booking info
        loadBookingInfo(book_id);

        //load vehicle info
        loadVehicleInfo(vehicle_id);

        btnCompleteBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
    }

    private void loadVehicleInfo(int vehicle_id) {
        try {
            Cursor myCursor3 = db.rawQuery("SELECT `image_path`, `model`, `rate` FROM `vehicle` WHERE `vehicle_id` = '"+ vehicle_id +"'", null);
            if(myCursor3.moveToFirst()){
                File vehicleIMG = new File(myCursor3.getString(0));
                imgDriverVehicleOngoing.setImageURI(Uri.fromFile(vehicleIMG));
                txtDriverModelOngoing.setText("Model: " + myCursor3.getString(1) + "\nP " + myCursor3.getString(2));
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadBookingInfo(int book_id) {
        try {
            Cursor myCursor2 = db.rawQuery("SELECT `pickup_location`, `delivery_location`, `remarks` FROM `booking` WHERE `book_id` = '"+ book_id +"'", null);
            if(myCursor2.moveToFirst()){
                editDriverPickupOngoing.setText(myCursor2.getString(0));
                editDriverDeliveryOngoing.setText(myCursor2.getString(1));
                editDriverRemarksOngoing.setText(myCursor2.getString(2));
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Load client Information
    private void loadClientInfo(int client_id) {
        try {
            Cursor myCursor1 = db.rawQuery("SELECT user_account.image_path, user_account.firstname, user_account.lastname, user_account.email FROM user_account, client WHERE user_account.user_id = client.user_id AND client_id = '"+ client_id +"'", null);
            if(myCursor1.moveToFirst()){
                File clientIMG =new File(myCursor1.getString(0));
                imgClientDriverOngoing.setImageURI(Uri.fromFile(clientIMG));
                txtClientNameOngoing.setText(myCursor1.getString(1) + " " + myCursor1.getString(2));
                txtClientEmailOngoing.setText(myCursor1.getString(3));
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //get the book_id from slot_in
    private int getBookID(int driver_id){
        int id = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT `book_id` FROM `slot_in` WHERE `driver_id` = '"+driver_id+"'", null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //get the client_id from booking
    private int getClientID(int book_id){
        int id = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT `client_id` FROM `booking` WHERE `book_id` = '"+book_id+"'", null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //get the vehicle_id from booking
    private int getVehicleID(int book_id){
        int id = 0;
        try {
            Cursor cursor = db.rawQuery("SELECT `vehicle_id` FROM `booking` WHERE `book_id` = '"+book_id+"'", null);
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverOngoing.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }
}