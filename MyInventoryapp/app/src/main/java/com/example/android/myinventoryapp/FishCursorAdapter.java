package com.example.android.myinventoryapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.FishContract.FishEntry;

/**
 * Created by PB on 15/07/2017.
 */

public class FishCursorAdapter extends CursorAdapter {

    private final MainActivity activity;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public FishCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.activity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        ImageView imageView = (ImageView) view.findViewById(R.id.img_fish);
        TextView nameTextView = (TextView) view.findViewById(R.id.text_name_fish);
        TextView quantityTextView = (TextView) view.findViewById(R.id.fish_remain_number);
        TextView priceTextView = (TextView) view.findViewById(R.id.fish_price);
        ImageView saleView = (ImageView) view.findViewById(R.id.buy);

        TextView mBuyNumber = (TextView) view.findViewById(R.id.buy_number);

        // Find the columns of pet attributes that we're interested in
        int imageIndex = cursor.getColumnIndex(FishEntry.COLUMN_FISH_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(FishEntry.COLUMN_FISH_NAME);
        int quantityIndex = cursor.getColumnIndex(FishEntry.COLUMN_FISH_QUANTITY);
        int priceIndex = cursor.getColumnIndex(FishEntry.COLUMN_FISH_PRICE);

        // Read the pet attributes from the Cursor for the current fish
        String imageFish = cursor.getString(imageIndex);
        String fishName = cursor.getString(nameColumnIndex);
        final int fishQuantity = cursor.getInt(quantityIndex);
        String fishPrice = cursor.getString(priceIndex);

        final long id = cursor.getLong(cursor.getColumnIndex(FishEntry._ID));

        // Update the TextViews with the attributes for the current fish
        imageView.setImageURI(Uri.parse(imageFish));
        nameTextView.setText(fishName);
        quantityTextView.setText(String.valueOf(fishQuantity));
        priceTextView.setText(String.valueOf(fishPrice));

        view.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View view) {
                activity.onClickItem(id);
            }
        });

        saleView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                if(fishQuantity > 0 ) {
                    activity.onSellFish(id, fishQuantity);
                } else {
                    Toast.makeText(activity, "Quantity Unvailable, order more!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

