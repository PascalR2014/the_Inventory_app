package com.example.android.myinventoryapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.myinventoryapp.data.FishContract.FeedEntry;

/**
 * Created by PB on 15/07/2017.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_FISH_LOADER = 0;

    private Uri mCurrentFishUri;
    private boolean mFishHasChanged = false;

    Uri imageUri;
    private ImageView mImage;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierName;
    private EditText mSupplierPhone;
    private EditText mSupplierEmail;
    private int quantity;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mFishHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentFishUri = intent.getData();

        if(mCurrentFishUri == null){
            setTitle(getString(R.string.editor_activity_title_new_fish));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_fish));
            getLoaderManager().initLoader(EXISTING_FISH_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mImage = (ImageView) findViewById(R.id.add_picture);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);

        mSupplierName = (EditText) findViewById(R.id.edit_sup_name);
        mSupplierPhone = (EditText) findViewById(R.id.edit_sup_phone);
        mSupplierEmail = (EditText) findViewById(R.id.edit_sup_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picImage();
                //Intent intent = new Intent(EditorActivity.this, GridActivity.class);
                //startActivity(intent);
                mFishHasChanged = true;
            }
        });
    }

    public void picImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            return;
        }
        openSelection();
    }

    private void openSelection() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intentType));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPicture)), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentFishUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveFish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_order:
                orderMore();
                return true;
            case android.R.id.home:
                if (!mFishHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void orderMore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Place your order with a call or email");
        builder.setPositiveButton("Phone", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhone.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Email", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                String sender = mSupplierEmail.getText().toString().trim();
                String bodyMessage = "[Name of supplier] Hi,[Name of contact]. [Name of your society], we want to order more [item name, quantity] " +
                        mNameEditText.getText().toString().trim() + "./nThank you [PascalR2014, github account.]";

                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + sender));

                intent.putExtra(Intent.EXTRA_SUBJECT, "Fake new order, [to my inventory app],[Name of your society, number]");
                intent.putExtra(Intent.EXTRA_TEXT, bodyMessage);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(EditorActivity.this, R.string.handle_intent_email, Toast.LENGTH_SHORT).show();
                }
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFish();
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

    private void deleteFish() {
        if (mCurrentFishUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentFishUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_fish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_fish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    private void saveFish() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();
        String supplierEmailString = mSupplierEmail.getText().toString().trim();

        if (mCurrentFishUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                (TextUtils.isEmpty(supplierPhoneString) || TextUtils.isEmpty(supplierEmailString)) && imageUri == null) {

            return;
        }

        ContentValues values = new ContentValues();

        if (imageUri == null) {
            Toast.makeText(this, "Image item required", Toast.LENGTH_SHORT).show();
            return;
        } else values.put(FeedEntry.COLUMN_ITEM_IMAGE, imageUri.toString());

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Item name required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_ITEM_NAME, nameString);

        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Price required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_ITEM_PRICE, priceString);

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Item quantity required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_ITEM_QUANTITY, quantityString);

        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Supplier name required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, "Supplier phone required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        if (TextUtils.isEmpty(supplierEmailString)) {
            Toast.makeText(this, "Supplier email required", Toast.LENGTH_SHORT).show();
            return;
        }else values.put(FeedEntry.COLUMN_SUPPLIER_EMAIL, supplierEmailString);

        // New Product
        if (mCurrentFishUri == null) {

            // This is a NEW pet, so insert a new fish into the provider,
            // returning the content URI for the new fish.
            Uri newUri = getContentResolver().insert(FeedEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_fish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_fish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Existing product
            int rowsAffected = getContentResolver().update(mCurrentFishUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_fish_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_fish_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelection();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                mImage.setImageURI(imageUri);
                mImage.invalidate();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mFishHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
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
                mCurrentFishUri,        // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            // Find the columns of fish attributes that we're interested in
            int imageColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_ITEM_IMAGE);
            int nameColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_ITEM_QUANTITY);
            int supNameColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_SUPPLIER_NAME);
            int supPhoneColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_SUPPLIER_PHONE);
            int supEmailColumnIndex = cursor.getColumnIndex(FeedEntry.COLUMN_SUPPLIER_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String imageUriString = cursor.getString(imageColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supName = cursor.getString(supNameColumnIndex);
            String supPhone = cursor.getString(supPhoneColumnIndex);
            String supEmail = cursor.getString(supEmailColumnIndex);

            // Update the views on the screen with the values from the database
            imageUri = Uri.parse(imageUriString);
            mImage.setImageURI(imageUri);

            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityEditText.setText(String.valueOf(quantity));
            mSupplierName.setText(supName);
            mSupplierPhone.setText(supPhone);
            mSupplierEmail.setText(supEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierName.setText("");
        mSupplierEmail.setText("");
        mSupplierPhone.setText("");
    }

    public void decreaseQuantity(View view) {
        if (quantity <= 1) {
            Toast.makeText(this, "Quantity can't be under 1 item.", Toast.LENGTH_SHORT).show();
        } else {
            quantity--;
            mQuantityEditText.setText(String.valueOf(quantity));
        }
    }

    public void increaseQuantity(View view) {
        quantity++;
        mQuantityEditText.setText(String.valueOf(quantity));
    }
}
