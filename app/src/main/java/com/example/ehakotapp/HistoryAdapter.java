package com.example.ehakotapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<History> {
    private Context mContext;
    private int mResource;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<History> objects) {
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
        TextView txtBookIDReport = convertView.findViewById(R.id.txtBookIDReport);
        TextView txtDateCompleted = convertView.findViewById(R.id.txtDateCompleted);
        TextView txtAmount = convertView.findViewById(R.id.txtAmount);

        //get the values from History.class
        txtBookIDReport.setText("Booking ID: " + getItem(position).getBook_id());
        txtDateCompleted.setText("Date Completed: " + getItem(position).getDate());
        txtAmount.setText("Amount Paid: P " + getItem(position).getAmount());

        return convertView;
    }
}
