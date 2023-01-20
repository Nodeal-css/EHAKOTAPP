package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientViewHistory extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private TextView txtPaymentIDHistory, txtBookIDHistory, txtVehicleHistory, txtDriverHistory, txtAmountHistory, txtDateHistory;
    private EditText editPickupHistory, editDeliveryHistory;
    //TODO: so in the next session, try to add users for client and driver to test out the sql queries

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_view_history);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important id/s
        int client_id = getIntent().getIntExtra("client_id", 0);
        int book_id = getIntent().getIntExtra("book_id", 0);
        String date = getIntent().getStringExtra("date");
        int amount = getIntent().getIntExtra("amount", 0);
        int vehicle_id = getVehicleID(book_id);
        int driver_id = getDriverID(vehicle_id);
        int user_id = getUserID(driver_id);
        int payment_id = getIntent().getIntExtra("payment_id", 0);;
        Toast.makeText(ClientViewHistory.this, "driver ID: "+ driver_id + " vehicle_id: " + vehicle_id + " book_ic: " + book_id + " date: " + date + " amount: " + amount, Toast.LENGTH_LONG).show();

        //Initialize the components from xml
        txtPaymentIDHistory = (TextView) findViewById(R.id.txtPaymentIDHistory); // found in payment table
        txtBookIDHistory = (TextView) findViewById(R.id.txtBookIDHistory); // found in report table
        txtVehicleHistory = (TextView) findViewById(R.id.txtVehicleHistory); // found in vehicle table
        txtDriverHistory = (TextView) findViewById(R.id.txtDriverHistory); // found in user_account table | get the driver id
        txtAmountHistory = (TextView) findViewById(R.id.txtAmountHistory); // found in payment table
        txtDateHistory = (TextView) findViewById(R.id.txtDateHistory); // found in report table;
        editPickupHistory = (EditText) findViewById(R.id.editPickupHistory); // found in Booking table
        editDeliveryHistory = (EditText) findViewById(R.id.editDeliveryHistory); // found in Booking table

        //Load the intent to elements
        txtBookIDHistory.setText("Booking ID: " + book_id);
        txtAmountHistory.setText("Total Amount Paid: " + amount);
        txtDateHistory.setText(date);
        txtPaymentIDHistory.setText("PaymentID: " + payment_id);

        //Load from Booking table
        try {
            Cursor cursor1 = db.rawQuery("SELECT `pickup_location`, `delivery_location` FROM `booking` WHERE `book_id` = '"+book_id+"'", null);
            if (cursor1.moveToFirst()){
                editPickupHistory.setText(cursor1.getString(0));
                editDeliveryHistory.setText(cursor1.getString(1));
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }

        //Load from Vehicle table
        try {
            Cursor cursor2 = db.rawQuery("SELECT `model`, `vehicle_type` FROM `vehicle` WHERE `vehicle_id` = '"+vehicle_id+"'", null);
            if (cursor2.moveToFirst()){
                txtVehicleHistory.setText("Vehicle Used: " + cursor2.getString(0) + " - " + cursor2.getString(1));
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }

        //Load from user_account table [get full name of driver]
        try {
            Cursor cursor3 = db.rawQuery("SELECT `firstname`, `lastname` FROM `user_account` WHERE `user_id` = '"+user_id+"'", null);
            if (cursor3.moveToFirst()){
                txtDriverHistory.setText("Driver: " + cursor3.getString(0) + " " + cursor3.getString(1));
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    //This will get the vehicle id from booking table
    private int getVehicleID(int book_id){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `vehicle_id` from booking where `book_id` = '"+book_id+"'", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //get the driver_id from vehicles
    private int getDriverID(int vehicle_id){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `driver_id` from vehicle where vehicle_id = '"+vehicle_id+"';", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //get the user_account from driver_id to load the driver information
    private int getUserID(int driver_id){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `user_id` from driver where driver_id = '"+driver_id+"';", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientViewHistory.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }
}