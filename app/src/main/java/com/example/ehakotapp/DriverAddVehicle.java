package com.example.ehakotapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Driver;

public class DriverAddVehicle extends AppCompatActivity {

    final int REQUEST_CODE_GALLERY = 999;
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private ImageView imgChooseVehicle;
    private Button btnChooseImgVehicle, btnSaveVehicle;
    private EditText editVehiclePath, editPlateNum, editModel, editType, editLocation, editRate;

    //TODO: Clear the input fields after registering a vehicle
    // incomplete
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_add_vehicle);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        //get the driver from dashboard
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        Toast.makeText(DriverAddVehicle.this, "Driver id: " + driver_id, Toast.LENGTH_LONG).show();

        // Initialize components from xml
        imgChooseVehicle = (ImageView) findViewById(R.id.imgChooseVehicle);
        btnChooseImgVehicle = (Button) findViewById(R.id.btnChooseImgVehicle);
        btnSaveVehicle = (Button) findViewById(R.id.btnSaveVehicle);
        editVehiclePath = (EditText) findViewById(R.id.editVehiclePath);
        editPlateNum = (EditText) findViewById(R.id.editPlateNum);
        editModel = (EditText) findViewById(R.id.editModel);
        editType = (EditText) findViewById(R.id.editType);
        editLocation = (EditText) findViewById(R.id.editLocation);
        editRate = (EditText) findViewById(R.id.editRate);

        btnChooseImgVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        DriverAddVehicle.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnSaveVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogAddVehicle(driver_id);
            }
        });

    }

    //Show Dialog for adding a new Vehicle
    private void showDialogAddVehicle(int driver_id){
        AlertDialog.Builder alert = new AlertDialog.Builder(DriverAddVehicle.this);
        alert.setIcon(R.drawable.applogo);
        alert.setTitle("Message");
        alert.setMessage("\nAdd new Vehicle?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    db.execSQL("INSERT INTO `vehicle` VALUES(null, '" + driver_id + "', '" + editVehiclePath.getText().toString() + "', '" + editPlateNum.getText().toString() + "', '" + editModel.getText().toString() + "', '" + editType.getText().toString() + "', '" + editLocation.getText().toString() + "', '" + editRate.getText().toString() + "')");
                    Toast.makeText(DriverAddVehicle.this, "New Vehicle Added", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(DriverAddVehicle.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                DriverAddVehicle.super.onBackPressed();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editVehiclePath.setText("");
                editPlateNum.setText("");
                editModel.setText("");
                editType.setText("");
                editLocation.setText("");
                editLocation.setText("");
                editRate.setText("");
            }
        });
        alert.create().show();
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
            editVehiclePath.setText(getRealPathFromURI(uri));

            try {
                InputStream inputstream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
                imgChooseVehicle.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}