package com.example.android.myinventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.FishContract.FeedEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int FISH_LOADER = 0;
    FishCursorAdapter mCursorAdapter;
    TextView buyNumber;
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView fishListView = (ListView) findViewById(R.id.list);

        //buyNumber = (TextView) findViewById(R.id.buy_number);
        //buyNumber.setText(0);

        emptyView = findViewById(R.id.empty_view);
        fishListView.setEmptyView(emptyView);

        mCursorAdapter = new FishCursorAdapter(this, null);
        fishListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        fishListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentFishUri = ContentUris.withAppendedId(FeedEntry.CONTENT_URI, id);

                intent.setData(currentFishUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(FISH_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                insertFish();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteAll_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllFish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllFish() {
        int rowsDeleted = getContentResolver().delete(FeedEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from fish database");
    }

    private void insertFish() {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ITEM_IMAGE, FeedEntry.DEFAULT_IMAGE);
        values.put(FeedEntry.COLUMN_ITEM_NAME, "Toto");
        values.put(FeedEntry.COLUMN_ITEM_PRICE, "7");
        values.put(FeedEntry.COLUMN_ITEM_QUANTITY, "20");
        values.put(FeedEntry.COLUMN_SUPPLIER_NAME, "Jason");
        values.put(FeedEntry.COLUMN_SUPPLIER_PHONE, "00000000");
        values.put(FeedEntry.COLUMN_SUPPLIER_EMAIL, "j.son@mail.com");

        Uri newUri = getContentResolver().insert(FeedEntry.CONTENT_URI, values);
        Log.v("MainActivity", "Uri of new fish: " + newUri);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_ITEM_IMAGE,
                FeedEntry.COLUMN_ITEM_NAME,
                FeedEntry.COLUMN_ITEM_PRICE,
                FeedEntry.COLUMN_ITEM_QUANTITY,
                FeedEntry.COLUMN_SUPPLIER_NAME,
                FeedEntry.COLUMN_SUPPLIER_PHONE,
                FeedEntry.COLUMN_SUPPLIER_EMAIL};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                FeedEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public void onClickItem (long id) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);

        Uri currentProductUri = ContentUris.withAppendedId(FeedEntry.CONTENT_URI, id);
        intent.setData(currentProductUri);

        startActivity(intent);
    }

    public void onSellFish(long id, int quantity ){
        int buying = 0;
        Uri currentFishUri = ContentUris.withAppendedId(FeedEntry.CONTENT_URI, id);
        Log.v("CatalogActivity", "Uri: " + currentFishUri);

        if(quantity > 0) {
            quantity--;
            buying++;
            //buyNumber.setText(String.valueOf(buying));
            Toast.makeText(this, "Item(s) sell(s) !", Toast.LENGTH_SHORT).show();
        }

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ITEM_QUANTITY, quantity);
        getContentResolver().update(currentFishUri, values, null, null);
    }
}