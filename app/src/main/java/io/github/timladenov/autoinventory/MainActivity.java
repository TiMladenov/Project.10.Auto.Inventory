package io.github.timladenov.autoinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import io.github.timladenov.autoinventory.data.PartContract.PartEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;
    private final int INITIATE_PRICE = 30;
    private final int INITIATE_QUANTITY = 0;
    private PartAdapter partAdapter;
    private ListView itemsList;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsList = (ListView) findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_layout_view);
        partAdapter = new PartAdapter(this, null);
        itemsList.setEmptyView(emptyView);
        itemsList.setAdapter(partAdapter);

        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri selectedItemURI = ContentUris.withAppendedId(PartEntry.CONTENT_URI, id);
                intent.setData(selectedItemURI);

                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_example_part:
                insertDummyPart();
                return true;
            case R.id.remove_all_parts:
                deleteAllPartsConfirmation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyPart() {
        ContentValues values = new ContentValues();
        values.put(PartEntry.COLUMN_PART_PICTURE, getString(R.string.placeholder_img));
        values.put(PartEntry.COLUMN_NAME_PART, getString(R.string.example_part_name));
        values.put(PartEntry.COLUMN_TYPE_PART, PartEntry.PART_UNKNOWN);
        values.put(PartEntry.COLUMN_FITS_CAR, getString(R.string.example_part_fits));
        values.put(PartEntry.COLUMN_PART_DATA, getString(R.string.example_part_data));
        values.put(PartEntry.COLUMN_QUANTITY, INITIATE_QUANTITY);
        values.put(PartEntry.COLUMN_PRICE, INITIATE_PRICE);

        Uri newUri = getContentResolver().insert(PartEntry.CONTENT_URI, values);
    }

    private void deleteAllPartsConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.confirmation_delete_all_parts));
        alertDialogBuilder.setPositiveButton(getString(R.string.confirmation_delete_part_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeAllParts();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.confirmation_delete_part_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog newAlertDialog = alertDialogBuilder.create();
        newAlertDialog.show();
    }

    private void removeAllParts() {
        if (PartEntry.CONTENT_URI != null) {
            int rowsDeleted = getContentResolver().delete(
                    PartEntry.CONTENT_URI,
                    null,
                    null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.confirmation_error_delete_parts),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.confirmation_all_parts_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PartEntry._ID,
                PartEntry.COLUMN_PART_PICTURE,
                PartEntry.COLUMN_NAME_PART,
                PartEntry.COLUMN_TYPE_PART,
                PartEntry.COLUMN_PART_DATA,
                PartEntry.COLUMN_FITS_CAR,
                PartEntry.COLUMN_QUANTITY,
                PartEntry.COLUMN_PRICE};

        return new CursorLoader(this,
                PartEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        partAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        partAdapter.swapCursor(null);
    }
}
