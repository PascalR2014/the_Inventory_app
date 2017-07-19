package com.example.android.myinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        String SQL_CREATE_FISH_TABLE = "CREATE TABLE " + FishContract.FeedEntry.TABLE_NAME + " ("
                + FishContract.FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FishContract.FeedEntry.COLUMN_ITEM_IMAGE+ " TEXT NOT NULL, "
                + FishContract.FeedEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + FishContract.FeedEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + FishContract.FeedEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + FishContract.FeedEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + FishContract.FeedEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + FishContract.FeedEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_FISH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}

