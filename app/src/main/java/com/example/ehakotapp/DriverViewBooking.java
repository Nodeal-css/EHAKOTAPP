package com.example.ehakotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DriverViewBooking extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view_booking);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listView = (ListView) findViewById(R.id.listDriverViewBooking);

        //Intents
        int driver_id = getIntent().getIntExtra("driver_id", 0);
        Toast.makeText(DriverViewBooking.this, "Driver ID: " + driver_id, Toast.LENGTH_LONG).show();
        //Create Data
        ArrayList<Booking> arrayList = new ArrayList<>();
        //arrayList.add(new Booking("/storage/emulated/0/Download/images.png", "PENDING", "John Smith", "Isuzu", "2"));

        //Load the Booking list distict to every driver
        loadBookingList(driver_id, arrayList);

        //set-up the adapter
        BookingAdapter adapter = new BookingAdapter(this, R.layout.list_driver_view_booking_template, arrayList);
        //assign adapter to listView
        listView.setAdapter(adapter);

        //TODO: make a condition, if the driver has an ongoing 'slot_in'
        // then it will not proceed to the next activity | SAME as clientVehicles.class
        // In the next session, create the DriverApproveBooking.Activity to approve or cancel a booking
        // 2. so that we can insert in the slot_in table and test it.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: condition, if slot_in is empty, then proceed
                if(isSlotInEmpty(driver_id)) {
                    try {
                        Cursor myCursor1 = db.rawQuery("SELECT DISTINCT(booking.book_id), client_id, booking.vehicle_id FROM booking, vehicle, driver WHERE status != 'COMPLETE' AND status != 'PAYMENT' AND booking.vehicle_id = vehicle.vehicle_id AND vehicle.driver_id = '" + driver_id + "'", null);
                        myCursor1.moveToFirst();
                        myCursor1.move(i);
                        Intent myIntent = new Intent(DriverViewBooking.this, DriverApproveBooking.class);
                        myIntent.putExtra("driver_id", driver_id);
                        myIntent.putExtra("book_id", myCursor1.getInt(0));
                        myIntent.putExtra("client_id", myCursor1.getInt(1));
                        myIntent.putExtra("vehicle_id", myCursor1.getInt(2));

                        startActivity(myIntent);
                    } catch (Exception e) {
                        Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(DriverViewBooking.this, "Ongoing Slot in, will not proceed", Toast.LENGTH_LONG).show();
                }

                /*Toast.makeText(DriverViewBooking.this, "Book ID: " + myCursor1.getString(0) + "\nClient ID: " + myCursor1.getString(1) + "\nVehicle ID: " + myCursor1.getString(2), Toast.LENGTH_LONG).show();
                if (isSlotInEmpty(driver_id)) {
                    Toast.makeText(DriverViewBooking.this, "Slot in is empty approve this booking in the next page", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(DriverViewBooking.this, "Ongoing Slot in, will not proceed", Toast.LENGTH_LONG).show();
                }*/

            }
        });
    }

    private void loadBookingList(int driver_id, ArrayList<Booking> arrayList) {
        try {
            Cursor mCursor = db.rawQuery("SELECT DISTINCT(booking.book_id), client_id, status, booking.vehicle_id FROM booking, vehicle, driver WHERE status != 'COMPLETE' AND status != 'PAYMENT' AND booking.vehicle_id = vehicle.vehicle_id AND vehicle.driver_id = '"+ driver_id +"'", null);
            if (mCursor.moveToFirst()){
                for(int i = 0; i < mCursor.getCount(); i++){
                    arrayList.add(new Booking(getImage(mCursor.getInt(1)), mCursor.getString(2), getName(mCursor.getInt(1)), getModel(mCursor.getInt(3)), mCursor.getString(0)));
                    mCursor.moveToNext();
                }
            }
        }catch (Exception e){
            Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return name;
    }

    //get the Vehicle's Model
    private String getModel(int vehicle_id){
        String model = "";
        MyDB mydb4 = new MyDB(this);
        mydb4.startWork();
        SQLiteDatabase db4 = mydb4.getWritableDatabase();
        try {
            Cursor myCursor = db4.rawQuery("SELECT vehicle.model, vehicle.vehicle_id FROM vehicle WHERE vehicle_id = '"+vehicle_id+"'", null);
            if (myCursor.moveToFirst()){
                model = myCursor.getString(0);
            }
        }catch (Exception e){
            Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return model;
    }

    // This method returns true if there is no ongoing slot_in
    private boolean isSlotInEmpty(int driver_id){
        boolean flag = true;
        try {
            Cursor mcursor2 = db.rawQuery("SELECT * FROM slot_in WHERE driver_id = '"+driver_id+"'", null);
            if(mcursor2.moveToFirst()){
                flag = false;
            }
        }catch (Exception e){
            Toast.makeText(DriverViewBooking.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return flag;
    }
}