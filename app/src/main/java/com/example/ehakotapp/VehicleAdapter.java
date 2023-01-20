package com.example.ehakotapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;

public class VehicleAdapter extends ArrayAdapter<Vehicle> {
    private Context mContext;
    private int mResource;

    public VehicleAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Vehicle> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        //Initialize components from xml
        ImageView imgVehicle = convertView.findViewById(R.id.imgVehicle);
        TextView txtModel = convertView.findViewById(R.id.txtModel);
        TextView txtType = convertView.findViewById(R.id.txtType);
        TextView txtLocation = convertView.findViewById(R.id.txtLocation);
        TextView txtVehicleID = convertView.findViewById(R.id.txtVehicleID);

        //get the values from Vehicle.class
        File imgFile1 = new File(getItem(position).getImage());
        imgVehicle.setImageURI(Uri.fromFile(imgFile1));

        txtModel.setText("Model: " + getItem(position).getModel());
        txtType.setText("Type: " + getItem(position).getType());
        txtLocation.setText("Location: " + getItem(position).getLocation());
        txtVehicleID.setText("Vehicle ID:" + getItem(position).getvehicle_id());

        return convertView;
    }
}
