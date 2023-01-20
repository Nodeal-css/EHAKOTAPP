package com.example.ehakotapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientSignUserPhaseTwo extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private EditText editDateJoinSignUp, editPaymentSignUp;
    private CheckBox checkBoxGCash, checkBoxPaymaya, checkBoxPaypal;
    private Button btnSaveSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_sign_user_phase_two);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Important intents
        String email = getIntent().getStringExtra("email");
        int user_id = getUserID(email);
        Toast.makeText(ClientSignUserPhaseTwo.this, "Email: " + email + "\nUser_ID: " + user_id, Toast.LENGTH_LONG).show();

        //Initialize XML components
        editDateJoinSignUp = (EditText) findViewById(R.id.editDateJoinSignUp);
        editPaymentSignUp = (EditText) findViewById(R.id.editPaymentSignUp);
        btnSaveSignUp = (Button) findViewById(R.id.btnSaveSignUp);
        checkBoxGCash = (CheckBox) findViewById(R.id.checkBoxGCash);
        checkBoxPaymaya = (CheckBox) findViewById(R.id.checkBoxPaymaya);
        checkBoxPaypal = (CheckBox) findViewById(R.id.checkBoxPaypal);
        editDateJoinSignUp.setText(getCurrentDateAndTime());

        btnSaveSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    db.execSQL("INSERT INTO `client` VALUES('"+user_id+"', null, '"+editDateJoinSignUp.getText().toString()+"', '"+editPaymentSignUp.getText().toString()+"')");
                    //Toast.makeText(ClientSignUserPhaseTwo.this, "Signed up\nReturning to log in", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(ClientSignUserPhaseTwo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                showDialogSignUp();
            }
        });

        checkBoxGCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPaymentSignUp.setText("GCash");
                checkBoxGCash.setChecked(true);
                checkBoxPaymaya.setChecked(false);
                checkBoxPaypal.setChecked(false);
            }
        });
        checkBoxPaymaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPaymentSignUp.setText("Paymaya");
                checkBoxPaymaya.setChecked(true);
                checkBoxGCash.setChecked(false);
                checkBoxPaypal.setChecked(false);
            }
        });
        checkBoxPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPaymentSignUp.setText("Paypal");
                checkBoxPaypal.setChecked(true);
                checkBoxGCash.setChecked(false);
                checkBoxPaymaya.setChecked(false);
            }
        });

    }

    //get The current date
    private static String getCurrentDateAndTime(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);
        return formattedDate;
    }

    //get the user_id using 'email' from user_account table
    private int getUserID(String email){
        int id = 0;
        try {
            Cursor mCursor = db.rawQuery("SELECT user_id, email FROM user_account WHERE email = '"+email+"'", null);
            if(mCursor.moveToFirst()){
                id = mCursor.getInt(0);
            }
        }catch (Exception e){
            Toast.makeText(ClientSignUserPhaseTwo.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return id;
    }

    //alert dialog box for signing up
    private void showDialogSignUp(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ClientSignUserPhaseTwo.this);
        alert.setIcon(R.drawable.applogo);
        alert.setTitle("Sign Up completed");
        alert.setMessage("\nYou are signed in as client\nclick OK to return to login");
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent myIntent = new Intent(ClientSignUserPhaseTwo.this, MainActivity.class);
                startActivity(myIntent);
            }
        });
        alert.create().show();
    }
}