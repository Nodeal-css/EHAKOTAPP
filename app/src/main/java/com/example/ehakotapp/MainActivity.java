package com.example.ehakotapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    MyDB mydb1;
    SQLiteDatabase db;
    Cursor myCursor;
    EditText editEmail, editPassword;
    Button btnLogin, btnSignUp;



    final int REQUEST_CODE_GALLERY = 999;
    //TODO: create a error catcher for null EditText in the next session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLogin()){
                    Toast.makeText(MainActivity.this, "User Type: " + userType() + " Log in as " + editEmail.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent;
                    switch(userType()){
                        case 'C':
                            intent = new Intent(MainActivity.this, ClientDashboard.class);
                            intent.putExtra("email", editEmail.getText().toString());
                            startActivity(intent);
                            break;

                        case 'D':
                            intent = new Intent(MainActivity.this, DriverDashboard.class);
                            intent.putExtra("email", editEmail.getText().toString());
                            startActivity(intent);
                            break;

                        default: Toast.makeText(MainActivity.this, "Can not determine user type", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Invalid log in as " + editEmail.getText().toString(), Toast.LENGTH_LONG).show();
                }

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, ClientSignUser.class);
                startActivity(intent1);
            }
        });

        // TEST IMAGE PERMISSION
        //myCursor = db.rawQuery("SELECT `image_path` from `vehicle` where `vehicle_id` = 1", null);
        //myCursor.moveToFirst();
        //
        //File imgFile = new File(myCursor.getString(0));
        //image.setImageURI(Uri.fromFile(imgFile));

        /*UNCOMMENT THIS IF YOU REINSTALL THE APP
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
                );*/

    }

    // method for login
    private boolean checkLogin(){
        boolean flag = false;
        try{
            myCursor = db.rawQuery("SELECT email, password from `user_account` WHERE `email` = '" + editEmail.getText().toString() + "' AND `password` = '"+editPassword.getText().toString()+"'", null);
            myCursor.moveToFirst();
            if(myCursor.moveToFirst()){
                //Toast.makeText(MainActivity.this, myCursor.getString(0), Toast.LENGTH_LONG).show();
                flag = true;
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        return flag;
    }

    //check if client or driver
    private char userType(){
        char ch;
        Cursor mcursor = db.rawQuery("SELECT `user_type` from `user_account` WHERE `email` = '" + editEmail.getText().toString() + "'", null);
        mcursor.moveToFirst();
        if(mcursor.getString(0).equals("CLIENT")){
            ch = 'C';
        }else{
            ch = 'D';
        }
        return ch;
    }

    //Onrequest permission to allow phone storage to send image
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else{
                Toast.makeText(this, "You don't have permission to access file location", Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            //Get the path of the image here
            //txtDisplay.setText(getRealPathFromURI(uri));

            try {
                InputStream inputstream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
                //image.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}