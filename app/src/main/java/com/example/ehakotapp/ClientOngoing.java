package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
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

public class ClientOngoing extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private LinearLayout layoutOngoingHeader, layoutBookingPayment;
    private ImageView imgVehicleOngoing, imgDriverOngoing;
    private TextView txtVehicleModelOngoing, txtVehiclePlateOngoing, txtDriverNameOngoing, txtLocationOngoing, txtDriverEmailOngoing, txtBookingStatus, txtRateOngoing;
    private EditText editPickupOngoing, editDeliveryOngoing, editRemarksOngoing, editCreditCardNumber;
    private Button btnCancelBooking, btnPaymentBooking, btnConfirmPayment;

    //TODO: Continue with the xml this evening and the same implementation of getting the vehicle and driver information
    // (May 4, 2022 - Wednesday)LEARN: about dialog boxes instead of using toast to complete payment and filling every forms
    //So this activity will receive the client's client_id in order to search the Ongoing Booking
    // (May 5, 2022) - implementation of History

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_ongoing);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important ID/s to intent
        int client_id = getIntent().getIntExtra("client_id", 0);
        int book_id = getBookingID(client_id);
        int vehicle_id = getVehicleID(book_id);
        int driver_id = getDriverID(vehicle_id);
        int user_id = getUserID(driver_id);

        //Initialize components from xml
        layoutOngoingHeader = (LinearLayout) findViewById(R.id.layoutOngoingHeader);
        layoutBookingPayment = (LinearLayout) findViewById(R.id.layoutBookingPayment);
        imgVehicleOngoing = (ImageView) findViewById(R.id.imgVehicleOngoing);
        imgDriverOngoing = (ImageView) findViewById(R.id.imgDriverOngoing);
        txtVehicleModelOngoing = (TextView) findViewById(R.id.txtVehicleModelOngoing);
        txtVehiclePlateOngoing = (TextView) findViewById(R.id.txtVehiclePlateOngoing);
        txtDriverNameOngoing = (TextView) findViewById(R.id.txtDriverNameOngoing);
        txtLocationOngoing = (TextView) findViewById(R.id.txtLocationOngoing);
        txtDriverEmailOngoing = (TextView) findViewById(R.id.txtDriverEmailOngoing);
        txtRateOngoing = (TextView) findViewById(R.id.txtRateOngoing);
        editPickupOngoing = (EditText) findViewById(R.id.editPickupOngoing);
        editDeliveryOngoing = (EditText) findViewById(R.id.editDeliveryOngoing);
        editRemarksOngoing = (EditText) findViewById(R.id.editRemarksOngoing);
        editCreditCardNumber = (EditText) findViewById(R.id.editCreditCardNumber);
        btnConfirmPayment = (Button) findViewById(R.id.btnConfirmPayment);

        //Alert Dialog for reviews


        //Initialize buttons to display layouts to test
        txtBookingStatus = (TextView) findViewById(R.id.txtBookingStatus);
        btnCancelBooking = (Button) findViewById(R.id.btnCancelBooking);
        btnPaymentBooking = (Button) findViewById(R.id.btnPaymentBooking);

        //Check the Booking status to change visibility of the layouts
        if(getStatus(book_id).equals("PAYMENT")){
            btnCancelBooking.setVisibility(View.GONE);
            btnPaymentBooking.setVisibility(View.VISIBLE);
        }else if(getStatus(book_id).equals("ONGOING")){
            btnCancelBooking.setVisibility(View.GONE);
            btnPaymentBooking.setVisibility(View.GONE);
        }else{
            btnCancelBooking.setVisibility(View.VISIBLE);
            btnPaymentBooking.setVisibility(View.GONE);
        }

        //Here, we'll search the ongoing booking
        loadBookingInfo(client_id);

        //Here, we'll load the Vehicle information | containing - vehicle img, model, plate, location, rate, [driver id <- make a method]
        loadVehicleInfo(vehicle_id);

        //Here, we'll load the Driver's information | containing - driver img, firstname & lastname, email
        try {
            Cursor mCursor2 = db.rawQuery("SELECT image_path, firstname, lastname, email FROM user_account WHERE user_id = '"+user_id+"'", null);
            if (mCursor2.moveToFirst()){
                File driverIMG = new File(mCursor2.getString(0));
                imgDriverOngoing.setImageURI(Uri.fromFile(driverIMG));
                txtDriverNameOngoing.setText("Name: " + mCursor2.getString(1).toString() + " " + mCursor2.getString(2).toString());
                txtDriverEmailOngoing.setText("Email: " + mCursor2.getString(3).toString());
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

        btnPaymentBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutOngoingHeader.setVisibility(View.GONE);
                layoutBookingPayment.setVisibility(View.VISIBLE);
                btnPaymentBooking.setVisibility(View.INVISIBLE);
            }
        });

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogCancel(book_id);
            }
        });

        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query1 = "UPDATE `booking` SET `status` = 'COMPLETE' WHERE `book_id` = '" +book_id+ "'";
                String query2 = "INSERT INTO `report` VALUES('"+book_id+"', '"+client_id+"', '"+getCurrentDateAndTime()+"')";
                String query3 = "INSERT INTO `payment` VALUES(null, '"+book_id+"', '"+getCurrentDateAndTime()+"', '"+getVehicleRate(vehicle_id)+"', '"+editCreditCardNumber.getText().toString()+"')";
                try {
                    db.execSQL(query1);
                    db.execSQL(query2);
                    db.execSQL(query3);
                    Toast.makeText(ClientOngoing.this, "E-hakot service and payment has been completed", Toast.LENGTH_LONG).show();
                    addReviewDialog(client_id, vehicle_id);
                }catch (Exception e){
                    Toast.makeText(ClientOngoing.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadVehicleInfo(int vehicle_id) {
        try {
            Cursor mCursor1 = db.rawQuery("SELECT image_path, model, plate_no, vehicle_location, rate FROM vehicle WHERE vehicle_id = '"+ vehicle_id +"'", null);
            if (mCursor1.moveToFirst()){
                File vehicleIMG = new File(mCursor1.getString(0));
                imgVehicleOngoing.setImageURI(Uri.fromFile(vehicleIMG));
                txtVehicleModelOngoing.setText(mCursor1.getString(1));
                txtVehiclePlateOngoing.setText(mCursor1.getString(2));
                txtLocationOngoing.setText("Location: " + mCursor1.getString(3));
                txtRateOngoing.setText("P " + mCursor1.getString(4));
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadBookingInfo(int client_id) {
        try {
            myCursor = db.rawQuery("SELECT * from booking where status != 'COMPLETE' AND client_id = '"+ client_id +"';", null);
            if(myCursor.moveToFirst()){
                editPickupOngoing.setText(myCursor.getString(3));
                editDeliveryOngoing.setText(myCursor.getString(4));
                editRemarksOngoing.setText(myCursor.getString(6));
                txtBookingStatus.setText(myCursor.getString(7));
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //Alert Dialog for reviews
    private void addReviewDialog(int client_id, int vehicle_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Review");
        builder.setMessage("How was the experience?");
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ClientOngoing.this, "Client: " + client_id + "\nvehicle: " + vehicle_id + "\nReview: " + input.getText().toString(), Toast.LENGTH_LONG).show();
                try {
                    db.execSQL("INSERT INTO `review` VALUES('"+client_id+"', '"+vehicle_id+"', '"+input.getText().toString()+"', null)");
                }catch (Exception e){
                    Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
                }
                ClientOngoing.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ClientOngoing.this, "Leave", Toast.LENGTH_LONG).show();
                ClientOngoing.super.onBackPressed();
            }
        });
        builder.create().show();
    }

    //This will get the booking id from booking table
    private int getBookingID(int client_id){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `book_id` from booking where status != 'COMPLETE' AND client_id = '"+client_id+"';", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //This will get the vehicle id from booking table
    private int getVehicleID(int book_id){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `vehicle_id` from booking where status != 'COMPLETE' AND book_id = '"+book_id+"';", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }

    // to get the current status of booking
    private String getStatus(int book_id){
        String status = "";
        try {
            Cursor mCursor = db.rawQuery("SELECT `status` from booking where book_id = '"+book_id+"';", null);
            if(mCursor.moveToFirst()){
                status = mCursor.getString(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return status;
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
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //get The current date
    private static String getCurrentDateAndTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    //get the rate of the vehicle
    private int getVehicleRate(int vehicle_id){
        int rate = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT `rate` FROM `vehicle` WHERE `vehicle_id` = '"+vehicle_id+"'", null);
            if (mCursor.moveToFirst()){
                rate = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientOngoing.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return rate;
    }

    //Dialog box for deleting a booked E-hakot
    private void showDialogCancel(int book_id){
        AlertDialog.Builder alert = new AlertDialog.Builder(ClientOngoing.this);
        alert.setIcon(android.R.drawable.ic_menu_delete);
        alert.setTitle("Confirm");
        alert.setMessage("Are you sure to cancel your booking?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    db.execSQL("DELETE FROM `booking` WHERE `book_id` = '"+book_id+"'");
                    Toast.makeText(ClientOngoing.this, "Pending booking has been deleted\nGoing back to previous page", Toast.LENGTH_LONG).show();
                    ClientOngoing.super.onBackPressed();
                }catch (Exception e){
                    Toast.makeText(ClientOngoing.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ClientOngoing.this, "return", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }
}