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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverProfile extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private CircleImageView profile_image_driver;
    private TextView txtDriverProfileID, txtDriverProfileName, txtDriverProfileAddress, txtDriverProfileEmail, txtDriverProfileType;
    private Button btnDriverLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important ID/S
        int driver_id = getIntent().getIntExtra("driver_id", 0);

        initializeXML();

        //Load driver information
        loadDriverInfo(driver_id);

        btnDriverLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogLogout();
            }
        });

    }

    private void loadDriverInfo(int driver_id) {
        try {
            Cursor myCursor = db.rawQuery("SELECT `image_path`, `firstname`, `lastname`, `address`, `email`, `user_type` FROM `user_account`, `driver` WHERE user_account.user_id = driver.user_id AND driver.driver_id = '"+ driver_id +"'", null);
            if(myCursor.moveToFirst()){
                File image = new File(myCursor.getString(0));
                profile_image_driver.setImageURI(Uri.fromFile(image));
                txtDriverProfileID.setText("Driver ID: " + driver_id);
                txtDriverProfileName.setText("name: " + myCursor.getString(1) + " " + myCursor.getString(2));
                txtDriverProfileAddress.setText("address: " + myCursor.getString(3));
                txtDriverProfileEmail.setText("email: " + myCursor.getString(4));
                txtDriverProfileType.setText("type: " + myCursor.getString(5));
            }
        }catch (Exception e){
            Toast.makeText(DriverProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeXML() {
        profile_image_driver = (CircleImageView) findViewById(R.id.profile_image_driver);
        txtDriverProfileID = (TextView) findViewById(R.id.txtDriverProfileID);
        txtDriverProfileName = (TextView) findViewById(R.id.txtDriverProfileName);
        txtDriverProfileAddress = (TextView) findViewById(R.id.txtDriverProfileAddress);
        txtDriverProfileEmail = (TextView) findViewById(R.id.txtDriverProfileEmail);
        txtDriverProfileType = (TextView) findViewById(R.id.txtDriverProfileType);
        btnDriverLogOut = (Button) findViewById(R.id.btnDriverLogOut);
    }

    private void showDialogLogout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverProfile.this);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setTitle("Confirm");
        alert.setMessage("Are you sure you want to sign out?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mIntent = new Intent(DriverProfile.this, MainActivity.class);
                startActivity(mIntent);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DriverProfile.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }
}