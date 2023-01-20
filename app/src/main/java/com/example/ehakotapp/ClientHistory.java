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

public class ClientHistory extends AppCompatActivity {
    private MyDB mydb1;
    private SQLiteDatabase db;
    private Cursor myCursor;
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_history);
        mydb1 = new MyDB(this);
        mydb1.startWork();
        db = mydb1.getWritableDatabase();
        listView = (ListView) findViewById(R.id.listHistoryClient);

        //Important ID/s
        int client_id = getIntent().getIntExtra("client_id", 0);
        Toast.makeText(ClientHistory.this, "Client_id: " + client_id, Toast.LENGTH_LONG).show();

        //Create data | use ArrayList<History>
        ArrayList<History> arrayList = new ArrayList<>();
        try {
            myCursor = db.rawQuery("SELECT report.book_id, report.datetime_completed, payment.total_amount from report, payment WHERE client_id = '"+client_id+"' AND report.book_id = payment.book_id", null);
            if (myCursor.moveToFirst()){
                for(int i = 0; i < myCursor.getCount(); i++){
                    arrayList.add(new History(myCursor.getInt(0), myCursor.getString(1), myCursor.getInt(2)));
                    myCursor.moveToNext();
                }
            }
        }catch (Exception e){
            Toast.makeText(ClientHistory.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //set-up the adapter
        HistoryAdapter adapter = new HistoryAdapter(this, R.layout.list_client_history, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Cursor mCursor2 = db.rawQuery("SELECT report.book_id, report.datetime_completed, payment.total_amount, payment.payment_id from report, payment WHERE client_id = '"+client_id+"' AND report.book_id = payment.book_id", null);
                    if (mCursor2.moveToFirst()){
                        mCursor2.move(i);
                        Intent intent4 = new Intent(ClientHistory.this, ClientViewHistory.class);
                        intent4.putExtra("client_id", client_id);
                        intent4.putExtra("book_id", mCursor2.getInt(0));
                        intent4.putExtra("date", mCursor2.getString(1));
                        intent4.putExtra("amount", mCursor2.getInt(2));
                        intent4.putExtra("payment_id", mCursor2.getInt(3));
                        startActivity(intent4);
                        //Toast.makeText(ClientHistory.this, "ID: " + mCursor2.getString(0) + " date: " + mCursor2.getString(1) + " amount: " + mCursor2.getString(2), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(ClientHistory.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}