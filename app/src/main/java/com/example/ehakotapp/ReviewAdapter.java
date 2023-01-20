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

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private int mResource;

    public ReviewAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Review> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        //Initialize XML components
        ImageView imgClientIMGReview = convertView.findViewById(R.id.imgClientIMGReview);
        TextView txtClientNameReview = convertView.findViewById(R.id.txtClientNameReview);
        TextView txtReview = convertView.findViewById(R.id.txtReview);

        //get the values from Review.class
        File image = new File(getItem(position).getClientImage());
        imgClientIMGReview.setImageURI(Uri.fromFile(image));
        txtClientNameReview.setText(getItem(position).getClientFullName());
        txtReview.setText(getItem(position).getReview());

        return convertView;
    }
}
