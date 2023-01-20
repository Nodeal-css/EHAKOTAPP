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
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClientSignUser extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private EditText editFirstnameSignUp, editLastnameSignUp, editAddressSignUp, editImageSignUp, editEmailSignUp, editPasswordSignUp, editRePasswordSignUp;
    private ImageView imgProfileSignUp;
    private Button btnChooseImgProfile, btnSubmitSignUp;
    final int REQUEST_CODE_GALLERY = 999;

    //TODO: Create the second phase of signing up

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_sign_user);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();

        //Initialize xml Components
        editFirstnameSignUp = (EditText) findViewById(R.id.editFirstnameSignUp);
        editLastnameSignUp = (EditText) findViewById(R.id.editLastnameSignUp);
        editAddressSignUp = (EditText) findViewById(R.id.editAddressSignUp);
        editImageSignUp = (EditText) findViewById(R.id.editImageSignUp);
        editEmailSignUp = (EditText) findViewById(R.id.editEmailSignUp);
        editPasswordSignUp = (EditText) findViewById(R.id.editPasswordSignUp);
        editRePasswordSignUp = (EditText) findViewById(R.id.editRePasswordSignUp);
        imgProfileSignUp = (ImageView) findViewById(R.id.imgProfileSignUp);
        btnChooseImgProfile = (Button) findViewById(R.id.btnChooseImgProfile);
        btnSubmitSignUp = (Button) findViewById(R.id.btnSubmitSignUp);

        //If email already taken & if passwords are not the same with re-password will not proceed
        btnSubmitSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailExist(editEmailSignUp.getText().toString())){
                    Toast.makeText(ClientSignUser.this, "Email already Existed", Toast.LENGTH_LONG).show();
                }else if(notSamePass(editPasswordSignUp.getText().toString(), editRePasswordSignUp.getText().toString())){
                    Toast.makeText(ClientSignUser.this, "Password fields are not the same", Toast.LENGTH_LONG).show();
                }else{
                    //Toast.makeText(ClientSignUser.this, "User_account signed up", Toast.LENGTH_LONG).show();
                    try {
                        db.execSQL("INSERT INTO `user_account` VALUES(null, '"+editEmailSignUp.getText().toString()+"', '"+editPasswordSignUp.getText().toString()+"', '"+editFirstnameSignUp.getText().toString()+"', '"+editLastnameSignUp.getText().toString()+"', '"+editAddressSignUp.getText().toString()+"', '"+editImageSignUp.getText().toString()+"', 'CLIENT')");
                        Toast.makeText(ClientSignUser.this, "Email: " + editEmailSignUp.getText().toString() + "\nAdded", Toast.LENGTH_LONG).show();

                        //Intent the email to second phase of Signing up
                        Intent myIntent = new Intent(ClientSignUser.this, ClientSignUserPhaseTwo.class);
                        myIntent.putExtra("email", editEmailSignUp.getText().toString());
                        startActivity(myIntent);
                    }catch (Exception e){
                        Toast.makeText(ClientSignUser.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Profile Picture picker
        btnChooseImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        ClientSignUser.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
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
            editImageSignUp.setText(getRealPathFromURI(uri));

            try {
                InputStream inputstream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
                imgProfileSignUp.setImageBitmap(bitmap);
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

    //returns true if the two password inputs are not the same
    private boolean notSamePass(String pass1, String pass2){
        boolean flag = true;
        if(pass1.equals(pass2)){
            flag = false;
        }
        return flag;
    }

    //returns true if there is similar email in user_account table
    private boolean emailExist(String email){
        boolean flag = false;
        try {
            Cursor myCursor = db.rawQuery("SELECT `email` FROM `user_account` WHERE `email` = '"+email+"'", null);
            if(myCursor.moveToFirst()){
                flag = true;
            }
        }catch (Exception e){
            Toast.makeText(ClientSignUser.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return flag;
    }
}