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

public class ClientDashboard extends AppCompatActivity {
    EditText editText1;
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private Button btnBook, btnHistoryClient, btnOngoingClient;
    private ImageView imgProfileClient;
    private TextView txtFullnameClient, txtAddressClient, txtClientID;
    private RelativeLayout layoutProfileClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_dashboard);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Initialize components from xml
        btnBook = (Button) findViewById(R.id.btnBook);
        btnHistoryClient = (Button) findViewById(R.id.btnHistoryClient);
        btnOngoingClient = (Button) findViewById(R.id.btnOngoingClient);
        imgProfileClient = (ImageView) findViewById(R.id.imgProfileClient);
        txtFullnameClient = (TextView) findViewById(R.id.txtFullnameClient);
        txtAddressClient = (TextView) findViewById(R.id.txtAddressClient);
        txtClientID = (TextView) findViewById(R.id.txtClientID);
        layoutProfileClient = (RelativeLayout) findViewById(R.id.layoutProfileClient);

        // get the extra after logging in
        String email = getIntent().getStringExtra("email");
        int client_id = getClientID(getID(email));

        //Load the profile into the dashboard
        try {
            myCursor = db.rawQuery("SELECT `image_path`, `firstname`, `lastname`, `address` FROM `user_account` WHERE `user_id` = '"+getID(email)+"'", null);
            if(myCursor.moveToFirst()){
                //Toast.makeText(DriverDashboard.this, "image_path: " + myCursor.getString(0), Toast.LENGTH_LONG).show();
                File imgFile = new File(myCursor.getString(0));
                imgProfileClient.setImageURI(Uri.fromFile(imgFile));
                txtFullnameClient.setText(myCursor.getString(1) + " " + myCursor.getString(2));
                txtAddressClient.setText(myCursor.getString(3));
                txtClientID.setText("Client ID: " + getClientID(getID(email)));
            }
        }catch (Exception e){
            Toast.makeText(ClientDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClientDashboard.this, ClientVehicles.class);
                intent.putExtra("client_id", client_id);
                startActivity(intent);
                //Toast.makeText(ClientDashboard.this, "Opening the list of vehicles for booking", Toast.LENGTH_SHORT).show();
            }
        });

        btnOngoingClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add condition here -> if a booking is ongoing, then proceed to next page
                if (!isBookingEmpty(client_id)){
                    Toast.makeText(ClientDashboard.this, "Booking is Ongoing [proceed to next page]", Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent(ClientDashboard.this, ClientOngoing.class);
                    intent2.putExtra("client_id", client_id);
                    startActivity(intent2);
                }
                else{
                    Toast.makeText(ClientDashboard.this, "There are no bookings [Do not proceed to next page]", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnHistoryClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(ClientDashboard.this, ClientHistory.class);
                intent3.putExtra("client_id", client_id);
                startActivity(intent3);
            }
        });

        layoutProfileClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent1 = new Intent(ClientDashboard.this, ClientProfile.class);
                mIntent1.putExtra("client_id", client_id);
                startActivity(mIntent1);
            }
        });
    }

    // This method returns true if theres an ongoing booking
    private boolean isBookingEmpty(int client_id){
        boolean flag = true;
        try {
            myCursor = db.rawQuery("SELECT * from booking where status != 'COMPLETE' AND client_id = '"+client_id+"';", null);
            if (myCursor.moveToFirst()){
                flag = false;
            }
        }catch (Exception e){
            Toast.makeText(ClientDashboard.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return flag;
    }

    // To get the user_account id
    private int getID(String email){
        int id = 0;
        myCursor = db.rawQuery("SELECT `user_id` from `user_account` WHERE email = '"+email+"'", null);
        if(myCursor.moveToFirst()){
            id = myCursor.getInt(0);
        }
        return id;
    }

    //to get the client id in client table
    private int getClientID(int user){
        int client = 0;
        myCursor = db.rawQuery("SELECT `client_id` from `client` WHERE `user_id` = '" +user+ "'", null);
        if(myCursor.moveToFirst()){
            client = myCursor.getInt(0);
        }
        return client;
    }

}