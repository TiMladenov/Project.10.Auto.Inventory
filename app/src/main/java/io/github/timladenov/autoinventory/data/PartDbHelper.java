package io.github.timladenov.autoinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.github.timladenov.autoinventory.data.PartContract.PartEntry;

public class PartDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "parts_inventory.db";
    public static final int DATABASE_VERSION = 1;

    public PartDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + PartEntry.TABLE_NAME + "("
                + PartEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PartEntry.COLUMN_PART_PICTURE + " TEXT, "
                + PartEntry.COLUMN_TYPE_PART + " INTEGER NOT NULL, "
                + PartEntry.COLUMN_NAME_PART + " TEXT NOT NULL, "
                + PartEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + PartEntry.COLUMN_FITS_CAR + " TEXT NOT NULL, "
                + PartEntry.COLUMN_PART_DATA + " TEXT NOT NULL, "
                + PartEntry.COLUMN_PRICE + " REAL NOT NULL);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
