package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewReviews extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private ListView listReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listReviews = (ListView) findViewById(R.id.listReviews);
        int vehicle_id = getIntent().getIntExtra("vehicle_id", 0);

        //create Data
        ArrayList<Review> arrayList = new ArrayList<>();
        //arrayList.add(new Review("image", "Johnny Joestar", "The service is cheap"));
        try {
            Cursor mCursor = db.rawQuery("SELECT `client_id`, `comment` FROM `review` WHERE `vehicle_id` = '"+vehicle_id+"'", null);
            if(mCursor.moveToFirst()){
                for(int i = 0; i < mCursor.getCount(); i++){
                    arrayList.add(new Review(getImage(mCursor.getInt(0)), getName(mCursor.getInt(0)), "'"+ mCursor.getString(1)+"'"));
                    mCursor.moveToNext();
                }
            }
        }catch (Exception e){
            Toast.makeText(ViewReviews.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //set up adapter
        ReviewAdapter adapter = new ReviewAdapter(this, R.layout.list_reviews_template, arrayList);
        listReviews.setAdapter(adapter);
    }

    //Get the Client user image_path
    private String getImage(int client_id){
        String path = "";
        MyDB mydb2 = new MyDB(this);
        mydb2.startWork();
        SQLiteDatabase db2 = mydb2.getWritableDatabase();
        try {
            Cursor myCursor = db2.rawQuery("SELECT user_account.image_path, client.client_id FROM user_account, client WHERE user_account.user_id = client.user_id AND client_id = '"+client_id+"'", null);
            if (myCursor.moveToFirst()){
                path = myCursor.getString(0);
            }
        }catch (Exception e){
            Toast.makeText(ViewReviews.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return path;
    }

    //Get the Client's Name
    private String getName(int client_id){
        String name = "";
        MyDB mydb3 = new MyDB(this);
        mydb3.startWork();
        SQLiteDatabase db3 = mydb3.getWritableDatabase();
        try {
            Cursor myCursor = db3.rawQuery("SELECT user_account.firstname, user_account.lastname, client.client_id FROM user_account, client WHERE user_account.user_id = client.user_id AND client_id = '"+client_id+"'", null);
            if (myCursor.moveToFirst()){
                name = myCursor.getString(0) + " " + myCursor.getString(1);
            }
        }catch (Exception e){
            Toast.makeText(ViewReviews.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return name;
    }

}