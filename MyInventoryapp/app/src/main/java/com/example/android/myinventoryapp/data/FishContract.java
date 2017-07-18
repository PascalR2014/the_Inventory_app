package com.example.android.myinventoryapp.data;

/**
 * Created by PB on 15/07/2017.
 */


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API FishContract for my inventory app.
 */
public final class FishContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private FishContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.myinventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FISH = "fish";

    /* Inner class that defines the table contents */
    public static class FishEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FISH);

        public final static String TABLE_NAME = "fishing";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_FISH_IMAGE = "fish_image";
        public final static String COLUMN_FISH_NAME = "fish_name";
        public final static String COLUMN_FISH_QUANTITY = "fish_quantity";
        public final static String COLUMN_FISH_PRICE = "fish_price";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";

        public static final String DEFAULT_IMAGE = "android.resource://com.example.android.myinventoryapp/drawable/default_fish";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of fish.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FISH;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single fish.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FISH;
    }
}


























