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

public class ClientVehicles extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_vehicles);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listView = (ListView) findViewById(R.id.listVehiclesClients);
        int client_id = getIntent().getIntExtra("client_id", 0);
        Toast.makeText(ClientVehicles.this, "Client id: " + client_id, Toast.LENGTH_LONG).show();

        //Create data
        ArrayList<Vehicle> arrayList = new ArrayList<>();
        try {
            myCursor = db.rawQuery("SELECT `image_path`, `model`, `vehicle_type`, `vehicle_location`, `vehicle_id` FROM `vehicle`", null);
            if(myCursor.moveToFirst()){
                for(int i = 0; i < myCursor.getCount(); i++){
                    arrayList.add(new Vehicle(myCursor.getString(0).toString(), myCursor.getString(1).toString(), myCursor.getString(2).toString(), myCursor.getString(3).toString(), myCursor.getString(4).toString()));
                    myCursor.moveToNext();
                }
            }
        }catch (Exception e){
            Toast.makeText(ClientVehicles.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //set-up the adapter
        VehicleAdapter adapter = new VehicleAdapter(this, R.layout.list_vehicles_template, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isBookingEmpty(client_id)) {
                    try {
                        myCursor = db.rawQuery("SELECT `image_path`, `model`, `vehicle_type`, `vehicle_location`, `vehicle_id`, `driver_id`, `plate_no`, `rate` FROM `vehicle`", null);
                        myCursor.moveToFirst();
                        myCursor.move(i);
                        //Toast.makeText(ClientVehicles.this, "Vehicle ID: " + myCursor.getString(4).toString(), Toast.LENGTH_LONG).show();
                        //(Attention) here
                        Intent intent1 = new Intent(ClientVehicles.this, ClientBooking.class);
                        intent1.putExtra("image_path_vehicle", myCursor.getString(0));
                        intent1.putExtra("model", myCursor.getString(1));
                        intent1.putExtra("plate_no", myCursor.getString(6));
                        intent1.putExtra("vehicle_location", myCursor.getString(3));
                        intent1.putExtra("vehicle_rate", myCursor.getString(7));

                        intent1.putExtra("client_id", client_id);
                        intent1.putExtra("vehicle_id", myCursor.getInt(4));
                        intent1.putExtra("driver_id", myCursor.getInt(5));
                        startActivity(intent1);
                    } catch (Exception e) {
                        Toast.makeText(ClientVehicles.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(ClientVehicles.this, "There is an Ongoing Booking", Toast.LENGTH_LONG).show();
                }
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
            Toast.makeText(ClientVehicles.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return flag;
    }
}