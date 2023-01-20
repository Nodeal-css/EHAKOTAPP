package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientBooking extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private Button btnBookHakot;
    private LinearLayout layoutBooking;
    private ImageView imgVehicleBook, imgDriverBook;
    private TextView txtVehicleModel, txtVehiclePlate, txtDriverNameBook, txtLocationBook, txtRateBook;
    private EditText editPickup, editDelivery, editRemarks;
    private Button btnSaveBooking, btnViewReviews;

    //TODO: May 2, Create the ClientOngoing.activity for clients
    //1st. is the UI
    //2nd. is the ClientOngoing.Class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_booking);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //important ID intent get extras | came from ClientVehicle.java
        int client_id = getIntent().getIntExtra("client_id", 0);
        int vehicle_id = getIntent().getIntExtra("vehicle_id", 0);
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        int user_id = getID(driver_id);
        //Get the intents here, we'll need ['image_path_vehicle', 'vehicle_model', 'vehicle_plate', 'driver_id', 'vehicle_id', 'client_id', 'location', 'Rate']
        String vehicle_img = getIntent().getStringExtra("image_path_vehicle");
        String model = getIntent().getStringExtra("model");
        String plate_no = getIntent().getStringExtra("plate_no");
        String location = getIntent().getStringExtra("vehicle_location");
        String rate = getIntent().getStringExtra("vehicle_rate");
        String driver_img = getImageDriver(user_id);
        String driver_name = getNameDriver(user_id);

        //Initialize the components from xml using their id's
        btnBookHakot = (Button) findViewById(R.id.btnBookHakot);
        layoutBooking = (LinearLayout) findViewById(R.id.layoutBooking);
        //Intialize the components to load the contents of the vehicle and driver
        imgVehicleBook = (ImageView) findViewById(R.id.imgVehicleBook);
        imgDriverBook = (ImageView) findViewById(R.id.imgDriverBook);
        txtVehicleModel = (TextView) findViewById(R.id.txtVehicleModel);
        txtVehiclePlate = (TextView) findViewById(R.id.txtVehiclePlate);
        txtDriverNameBook = (TextView) findViewById(R.id.txtDriverNameBook);
        txtLocationBook = (TextView) findViewById(R.id.txtLocationBook);
        txtRateBook = (TextView) findViewById(R.id.txtRateBook);
        editPickup = (EditText) findViewById(R.id.editPickup);
        editDelivery = (EditText) findViewById(R.id.editDelivery);
        editRemarks = (EditText) findViewById(R.id.editRemarks);
        btnSaveBooking = (Button) findViewById(R.id.btnSaveBooking);
        btnViewReviews = (Button) findViewById(R.id.btnViewReviews);

        //Load the contents in the ClientBooking.java
        File imgFileVehicle = new File(vehicle_img);
        File imgFileDriver = new File(driver_img);
        imgVehicleBook.setImageURI(Uri.fromFile(imgFileVehicle));
        txtVehicleModel.setText(model);
        txtVehiclePlate.setText(plate_no);
        txtLocationBook.setText("Location: " + location);
        txtRateBook.setText("Rate: " + rate);
        imgDriverBook.setImageURI(Uri.fromFile(imgFileDriver));
        txtDriverNameBook.setText(driver_name);

        //Toast.makeText(ClientBooking.this, "Client: " + client_id + " Vehicle: " + vehicle_id + " Driver: " + driver_id + " User ID: " + user_id, Toast.LENGTH_LONG).show();

        //Show the form when 'Book for E-hakot' is clicked
        btnBookHakot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutBooking.setVisibility(View.VISIBLE);
                btnBookHakot.setVisibility(View.GONE);
            }
        });

        //this method will insert a booking to booking table
        btnSaveBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    db.execSQL("INSERT INTO `booking` VALUES(null, '"+client_id+"', '"+vehicle_id+"', '"+editPickup.getText().toString()+"', '"+editDelivery.getText().toString()+"', '"+getCurrentDateAndTime()+"', '"+editRemarks.getText().toString()+"', 'PENDING')");
                    Toast.makeText(ClientBooking.this, "Successfully applied for booking", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(ClientBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // extract this statements to a showDialogSave();
                showDialogSave();
            }
        });

        btnViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent2 = new Intent(ClientBooking.this, ViewReviews.class);
                //Intent Vehicle id
                myIntent2.putExtra("vehicle_id", vehicle_id);
                startActivity(myIntent2);
            }
        });
    }

    //to get the user_id from user account
    private int getID(int driver_id){
        int id = 0;
        myCursor = db.rawQuery("SELECT `user_id` from `driver` WHERE driver_id = '"+driver_id+"'", null);
        if(myCursor.moveToFirst()){
            id = myCursor.getInt(0);
        }
        return id;
    }

    //private get the image from user_account table
    private String getImageDriver(int user_id){
        String path = "";
        myCursor = db.rawQuery("SELECT `image_path` FROM `user_account` WHERE `user_id` = '"+user_id+"'", null);
        if(myCursor.moveToFirst()){
            path = myCursor.getString(0);
        }
        return path;
    }

    //get the full name of the driver from user_account table
    private String getNameDriver(int user_id){
        String name = "";
        myCursor = db.rawQuery("SELECT `firstname`, `lastname` FROM `user_account` WHERE `user_id` = '"+user_id+"'", null);
        if(myCursor.moveToFirst()){
            name = myCursor.getString(0) + " " + myCursor.getString(1);
        }
        return name;
    }

    //get The current date
    private static String getCurrentDateAndTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    //Dialog for booking E-Hakot
    private void showDialogSave(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ClientBooking.this);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setTitle("Thank you for trusting E-Hakot");
        alert.setMessage("\nPlease wait for approval of the driver\nWithin 2-3 days.");
        alert.setCancelable(false);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClientBooking.super.onBackPressed();
            }
        });
        alert.create().show();
    }
}