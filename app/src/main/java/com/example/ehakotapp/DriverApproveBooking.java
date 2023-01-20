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

public class DriverApproveBooking extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private ImageView imgVehicleAPR, imgClientAPR;
    private TextView txtVehicleModelAPR, txtVehiclePlateAPR, txtClientNameAPR, txtClientAddressAPR, txtClientEmailAPR, txtBookingStatusAPR, txtRateAPR;
    private EditText editPickupAPR, editDeliveryAPR, editRemarksAPR;
    private Button btnApprove, btnCancel;

    //TODO: 'ONGOING' implementation as of May 14, 2022 | the isSlotInEmpty(driver_id) was tested
    // in the next session, make the Ongoing.Activity
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_approve_booking);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important id/s
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        int book_id = getIntent().getIntExtra("book_id", 0);
        int client_id = getIntent().getIntExtra("client_id", 0);
        int vehicle_id = getIntent().getIntExtra("vehicle_id", 0);
        Toast.makeText(DriverApproveBooking.this, "Driver: " + driver_id + "\nBook ID: " + book_id + "\nClient: " + client_id + "\nVehicle: " + vehicle_id, Toast.LENGTH_LONG).show();

        //Assign components from xml
        imgVehicleAPR = (ImageView) findViewById(R.id.imgVehicleAPR); // Vehicle
        imgClientAPR = (ImageView) findViewById(R.id.imgClientAPR);
        txtVehicleModelAPR = (TextView) findViewById(R.id.txtVehicleModelAPR); // Vehicle
        txtVehiclePlateAPR = (TextView) findViewById(R.id.txtVehiclePlateAPR); // Vehicle
        txtClientNameAPR = (TextView) findViewById(R.id.txtClientNameAPR);
        txtClientAddressAPR = (TextView) findViewById(R.id.txtClientAddressAPR);
        txtClientEmailAPR = (TextView) findViewById(R.id.txtClientEmailAPR);
        txtBookingStatusAPR = (TextView) findViewById(R.id.txtBookingStatusAPR);
        txtRateAPR = (TextView) findViewById(R.id.txtRateAPR); // Vehicle
        editPickupAPR = (EditText) findViewById(R.id.editPickupAPR);
        editDeliveryAPR = (EditText) findViewById(R.id.editDeliveryAPR);
        editRemarksAPR = (EditText) findViewById(R.id.editRemarksAPR);
        btnApprove = (Button) findViewById(R.id.btnApprove);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        //alert Dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverApproveBooking.this);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setTitle("Confirm");
        alert.setMessage("Approve booking?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //implement adding to slot_in and update booking to ongoing
                //Toast.makeText(DriverApproveBooking.this, "Approve \nBook ID: " + book_id + "\nDriver ID: " + driver_id, Toast.LENGTH_SHORT).show();
                String query1 = "INSERT INTO `slot_in` VALUES('"+driver_id+"', '"+book_id+"')";
                String query2 = "UPDATE `booking` SET `status` = 'ONGOING' WHERE `book_id` = '"+book_id+"'";
                try {
                    db.execSQL(query1);
                    db.execSQL(query2);
                    Toast.makeText(DriverApproveBooking.this, "Proceed to the Pick-up location", Toast.LENGTH_SHORT).show();
                    DriverApproveBooking.super.onBackPressed();
                }catch (Exception e){
                    Toast.makeText(DriverApproveBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DriverApproveBooking.this, "return", Toast.LENGTH_SHORT).show();
            }
        });
        // Alert Dialong END

        //Load Vehicle
        loadVehicleInfo(vehicle_id);

        //Load Client's user information
        loadClientInfo(client_id);

        //Load Booking
        loadBookingInfo(book_id);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DriverApproveBooking.this, "Cancelled return to previous page", Toast.LENGTH_LONG).show();
                DriverApproveBooking.super.onBackPressed();
            }
        });

        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });
    }

    private void loadBookingInfo(int book_id) {
        try {
            Cursor mCurr = db.rawQuery("SELECT pickup_location, delivery_location, remarks, status FROM booking WHERE book_id = '"+ book_id +"'", null);
            if(mCurr.moveToFirst()){
                editPickupAPR.setText(mCurr.getString(0));
                editDeliveryAPR.setText(mCurr.getString(1));
                editRemarksAPR.setText(mCurr.getString(2));
                txtBookingStatusAPR.setText(mCurr.getString(3));
            }
        }catch (Exception e){
            Toast.makeText(DriverApproveBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadClientInfo(int client_id) {
        try {
            Cursor myCursor = db.rawQuery("SELECT user_account.image_path, user_account.firstname, user_account.lastname, user_account.address, user_account.email FROM user_account, client WHERE user_account.user_id = client.user_id AND client_id = '"+ client_id +"'", null);
            if(myCursor.moveToFirst()){
                File clientIMG =new File(myCursor.getString(0));
                imgClientAPR.setImageURI(Uri.fromFile(clientIMG));
                txtClientNameAPR.setText("Client: " + myCursor.getString(1) + " " + myCursor.getString(2));
                txtClientAddressAPR.setText("Address: " + myCursor.getString(3));
                txtClientEmailAPR.setText("Email: " + myCursor.getString(4));
            }
        }catch (Exception e){
            Toast.makeText(DriverApproveBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadVehicleInfo(int vehicle_id) {
        try {
            Cursor mCursor = db.rawQuery("SELECT `image_path`, `model`, `plate_no`, `rate` FROM `vehicle` WHERE `vehicle_id` = '"+ vehicle_id +"'", null);
            if(mCursor.moveToFirst()){
                File vehicleIMG = new File(mCursor.getString(0));
                imgVehicleAPR.setImageURI(Uri.fromFile(vehicleIMG));
                txtVehicleModelAPR.setText(mCursor.getString(1));
                txtVehiclePlateAPR.setText(mCursor.getString(2));
                txtRateAPR.setText("P " + mCursor.getString(3));
            }
        }catch (Exception e){
            Toast.makeText(DriverApproveBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}