package com.example.android.myinventoryapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by PB on 15/07/2017.
 */

public class GridImageAdapter extends BaseAdapter{
    private Context mContext;

    public GridImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            /* Travaux en cours
            R.drawable.fish, R.drawable.fish1,
            R.drawable.fish2, R.drawable.fish3,
            R.drawable.fish4, R.drawable.fish5,
            R.drawable.fish6, R.drawable.fish8,
            R.drawable.fish9, R.drawable.fish10,
            R.drawable.fish11, R.drawable.fish12,
            R.drawable.fish13, R.drawable.fish14,
            R.drawable.fish15
            */
    };
}
