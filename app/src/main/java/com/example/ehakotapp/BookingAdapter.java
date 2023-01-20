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

public class BookingAdapter extends ArrayAdapter<Booking> {
    private Context mcontext;
    private int mresource;

    public BookingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Booking> objects) {
        super(context, resource, objects);
        this.mcontext = context;
        this.mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
        convertView = layoutInflater.inflate(mresource, parent, false);

        //Initialize XML components
        ImageView imgBookingIcon = convertView.findViewById(R.id.imgBookingIcon);
        TextView txtBookingStatusInfo = convertView.findViewById(R.id.txtBookingStatusInfo);
        TextView txtBookingClientName = convertView.findViewById(R.id.txtBookingClientName);
        TextView txtBookingVehicleReq = convertView.findViewById(R.id.txtBookingVehicleReq);
        TextView txtBookingID = convertView.findViewById(R.id.txtBookingID);

        //get the values from Booking.Class
        File clientProfile = new File(getItem(position).getClientImg());
        imgBookingIcon.setImageURI(Uri.fromFile(clientProfile));
        txtBookingStatusInfo.setText("Status: " + getItem(position).getBookStatus());
        txtBookingClientName.setText("Client: " + getItem(position).getClientName());
        txtBookingVehicleReq.setText("Vehicle: " + getItem(position).getVehicleModel());
        txtBookingID.setText("Booking ID: " + getItem(position).getBook_id());

        return convertView;
    }
}
