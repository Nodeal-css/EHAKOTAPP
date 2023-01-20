package com.example.ehakotapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DriverUpdateVehicle extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private ImageView imgVehicleUpdate, imgChangeVehicle;
    private TextView txtVehicleModelUpdate, txtPlateNumberUpdate, txtTypeUpdate, txtLocationUpdate, txtRateUpdate;
    private Button btnChooseUpdate, btnChangeImgVehicle, btnSaveVehicleChange;
    private LinearLayout linearLayoutUpdate;
    private EditText editVehiclePathChange, editPlateNumChange, editModelChange, editTypeChange, editLocationChange, editRateChange;
    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_update_vehicle);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //important ID/s
        int vehicle_id = getIntent().getIntExtra("vehicle_id", 0);
        Toast.makeText(DriverUpdateVehicle.this, "Vehicle ID: " + vehicle_id, Toast.LENGTH_LONG).show();

        //Initialize XML components
        initializeXmlComp();

        //Load vehicle Info
        //TODO: Continue in the EditTexts
        loadVehicleInfo(vehicle_id);
        loadVehicleInfoForm(vehicle_id);

        //set the update EditTexts to visible
        btnChooseUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutUpdate.setVisibility(View.VISIBLE);
            }
        });

        btnChangeImgVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        DriverUpdateVehicle.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnSaveVehicleChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addConfirmDialog(vehicle_id);
            }
        });
    }

    //first layout
    private void loadVehicleInfo(int vehicle_id) {
        try {
            Cursor mCursor = db.rawQuery("SELECT `image_path`, `model`, `plate_no`, `vehicle_type`, `vehicle_location`, `rate` FROM `vehicle` WHERE `vehicle_id` = '"+ vehicle_id +"'", null);
            if(mCursor.moveToFirst()){
                File vehicleIMG1 = new File(mCursor.getString(0));
                imgVehicleUpdate.setImageURI(Uri.fromFile(vehicleIMG1));
                txtVehicleModelUpdate.setText("model: " + mCursor.getString(1));
                txtPlateNumberUpdate.setText("plate no: " + mCursor.getString(2));
                txtTypeUpdate.setText("type: " + mCursor.getString(3));
                txtLocationUpdate.setText("location: " + mCursor.getString(4));
                txtRateUpdate.setText("P " + mCursor.getString(5));
            }
        }catch (Exception e){
            Toast.makeText(DriverUpdateVehicle.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Second layout
    private void loadVehicleInfoForm(int vehicle_id){
        try {
            Cursor mCursor = db.rawQuery("SELECT `image_path`, `model`, `plate_no`, `vehicle_type`, `vehicle_location`, `rate` FROM `vehicle` WHERE `vehicle_id` = '"+ vehicle_id +"'", null);
            if(mCursor.moveToFirst()){
                File vehicleIMG1 = new File(mCursor.getString(0));

                imgChangeVehicle.setImageURI(Uri.fromFile(vehicleIMG1));
                editVehiclePathChange.setText(mCursor.getString(0));
                editPlateNumChange.setText(mCursor.getString(2));
                editModelChange.setText(mCursor.getString(1));
                editTypeChange.setText(mCursor.getString(3));
                editLocationChange.setText(mCursor.getString(4));
                editRateChange.setText(mCursor.getString(5));
            }
        }catch (Exception e){
            Toast.makeText(DriverUpdateVehicle.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Initialize XML components
    private void initializeXmlComp() {
        imgVehicleUpdate = (ImageView) findViewById(R.id.imgVehicleUpdate);
        imgChangeVehicle = (ImageView) findViewById(R.id.imgChangeVehicle);
        txtVehicleModelUpdate = (TextView) findViewById(R.id.txtVehicleModelUpdate);
        txtPlateNumberUpdate = (TextView) findViewById(R.id.txtPlateNumberUpdate);
        txtTypeUpdate = (TextView) findViewById(R.id.txtTypeUpdate);
        txtLocationUpdate = (TextView) findViewById(R.id.txtLocationUpdate);
        txtRateUpdate = (TextView) findViewById(R.id.txtRateUpdate);
        btnChooseUpdate = (Button) findViewById(R.id.btnChooseUpdate);
        btnChangeImgVehicle = (Button) findViewById(R.id.btnChangeImgVehicle);
        btnSaveVehicleChange = (Button) findViewById(R.id.btnSaveVehicleChange);
        linearLayoutUpdate = (LinearLayout) findViewById(R.id.linearLayoutUpdate);
        editVehiclePathChange = (EditText) findViewById(R.id.editVehiclePathChange);
        editPlateNumChange = (EditText) findViewById(R.id.editPlateNumChange);
        editModelChange = (EditText) findViewById(R.id.editModelChange);
        editTypeChange = (EditText) findViewById(R.id.editTypeChange);
        editLocationChange = (EditText) findViewById(R.id.editLocationChange);
        editRateChange = (EditText) findViewById(R.id.editRateChange);
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
            editVehiclePathChange.setText(getRealPathFromURI(uri));

            try {
                InputStream inputstream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
                imgChangeVehicle.setImageBitmap(bitmap);
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

    //Alert Dialog to confirm update
    private void addConfirmDialog(int vehicle_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(DriverUpdateVehicle.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("Confirmation");
        builder.setMessage("Do you want to save changes?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(DriverUpdateVehicle.this, "Path: " + editVehiclePathChange.getText() + "\nPlate: " + editPlateNumChange.getText() + "\nModel: " + editModelChange.getText() + "\nType: " + editTypeChange.getText() + "\nLocation: " + editLocationChange.getText() + "\nRate: " + editRateChange.getText(), Toast.LENGTH_LONG).show();
                try {
                    db.execSQL("UPDATE `vehicle` SET `image_path` = '"+editVehiclePathChange.getText().toString()+"', `plate_no` = '"+editPlateNumChange.getText().toString()+"', `model` = '"+editModelChange.getText().toString()+"', `vehicle_type` = '"+editTypeChange.getText().toString()+"', `vehicle_location` = '"+editLocationChange.getText().toString()+"', `rate` = '"+editRateChange.getText()+"' WHERE `vehicle_id` = '"+vehicle_id+"'");
                    Toast.makeText(DriverUpdateVehicle.this, "Updated Vehicle ID: " + vehicle_id, Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(DriverUpdateVehicle.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                loadVehicleInfo(vehicle_id);
                linearLayoutUpdate.setVisibility(View.GONE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(DriverUpdateVehicle.this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        });
        builder.create().show();
    }
}