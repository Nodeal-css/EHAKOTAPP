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

public class ClientProfile extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private CircleImageView profile_image;
    private TextView txtClientProfileID, txtClientProfileName, txtClientProfileAddress, txtClientProfileEmail, txtClientProfileType;
    private Button btnClientLogOut;

    //TODO: create the driver profile in the next session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important ID/s
        int client_id = getIntent().getIntExtra("client_id", 0);
        Toast.makeText(ClientProfile.this, "Client: " + client_id, Toast.LENGTH_LONG).show();

        initializeXMLComponents();

        //Load client information
        loadClientInfo(client_id);

        btnClientLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogLogout();
            }
        });
    }

    private void loadClientInfo(int client_id) {
        try {
            Cursor mCursor = db.rawQuery("SELECT `image_path`, `firstname`, `lastname`, `address`, `email`, `user_type` FROM `user_account`, `client` WHERE user_account.user_id = client.user_id AND client.client_id = '"+ client_id +"'", null);
            if(mCursor.moveToFirst()){
                File clientIMG = new File(mCursor.getString(0));
                profile_image.setImageURI(Uri.fromFile(clientIMG));
                txtClientProfileID.setText("ID: " + client_id);
                txtClientProfileName.setText("name: " + mCursor.getString(1) + " " + mCursor.getString(2));
                txtClientProfileAddress.setText("address: " + mCursor.getString(3));
                txtClientProfileEmail.setText("email: " + mCursor.getString(4));
                txtClientProfileType.setText("type: " + mCursor.getString(5));
            }
        }catch (Exception e){
            Toast.makeText(ClientProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeXMLComponents() {
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        txtClientProfileID = (TextView) findViewById(R.id.txtClientProfileID);
        txtClientProfileName = (TextView) findViewById(R.id.txtClientProfileName);
        txtClientProfileAddress = (TextView) findViewById(R.id.txtClientProfileAddress);
        txtClientProfileEmail = (TextView) findViewById(R.id.txtClientProfileEmail);
        txtClientProfileType = (TextView) findViewById(R.id.txtClientProfileType);
        btnClientLogOut = (Button) findViewById(R.id.btnClientLogOut);
    }

    private void showDialogLogout(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ClientProfile.this);
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setTitle("Confirm");
        alert.setMessage("Are you sure you want to sign out?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mIntent = new Intent(ClientProfile.this, MainActivity.class);
                startActivity(mIntent);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ClientProfile.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        alert.create().show();
    }
}