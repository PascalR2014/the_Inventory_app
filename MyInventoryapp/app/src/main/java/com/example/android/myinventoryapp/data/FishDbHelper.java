package com.example.android.myinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.myinventoryapp.data.FishContract.FishEntry;

/**
 * Created by PB on 15/07/2017.
 */

//Initiate inventory.db
public class FishDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public FishDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FISH_TABLE = "CREATE TABLE " + FishEntry.TABLE_NAME + " ("
                + FishEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FishEntry.COLUMN_FISH_IMAGE+ " TEXT NOT NULL, "
                + FishEntry.COLUMN_FISH_NAME + " TEXT NOT NULL, "
                + FishEntry.COLUMN_FISH_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + FishEntry.COLUMN_FISH_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + FishEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + FishEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + FishEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_FISH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}

