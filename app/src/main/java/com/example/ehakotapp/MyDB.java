package com.example.ehakotapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyDB extends SQLiteOpenHelper {

    public static String dbName = "ehakotdb1.db";
    public static int dbVersion = 2;
    public static String dbPath = "";
    Context mycontext;

    public MyDB(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
        mycontext = context;
    }

    public MyDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean existDatabase(){
        File myFile = new File(dbPath+dbName);
        return myFile.exists();
    }

    private void copyDatabase(){
        try {
            InputStream myInput = mycontext.getAssets().open(dbName);
            OutputStream myOutput = new FileOutputStream(dbPath+dbName);
            byte[] myBuffer = new byte[1024];
            int length;
            while((length=myInput.read(myBuffer))>0){
                myOutput.write(myBuffer,0,length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startWork(){
        dbPath = mycontext.getFilesDir().getParent()+"/databases/";
        if(!existDatabase()){
            this.getWritableDatabase();
            copyDatabase();
        }
    }
}
