package io.github.timladenov.autoinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import io.github.timladenov.autoinventory.data.PartContract.PartEntry;

public class PartProvider extends ContentProvider {

    private PartDbHelper partHelper;
    private static final int PARTS = 100;
    private static final int PARTS_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PartContract.CONTENT_AUTHORITY, PartContract.PATH_ITEMS, PARTS);
        sUriMatcher.addURI(PartContract.CONTENT_AUTHORITY, PartContract.PATH_ITEMS + "/#", PARTS_ID);
    }

    @Override
    public boolean onCreate() {
        partHelper = new PartDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTS:
                return PartEntry.CONTENT_LIST_TYPE;
            case PARTS_ID:
                return PartEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("This URI: " + uri + ", is unknown with match: " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase sqLiteDatabase = partHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTS:
                cursor = sqLiteDatabase.query(PartEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PARTS_ID:
                selection = PartEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(PartEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTS:
                try{
                    return insertPart(uri, values);
                } catch (Exception e) {
                    Log.i("Error due to, ", e.getMessage());
                }
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPart(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = partHelper.getWritableDatabase();
        String partImage = values.getAsString(PartEntry.COLUMN_PART_PICTURE);
        if (partImage == null) {
            throw new IllegalArgumentException("Part item must have an image.");
        }

        String partName = values.getAsString(PartEntry.COLUMN_NAME_PART);
        if (partName == null || partName.isEmpty()) {
            throw new IllegalArgumentException("Part item must have a name.");
        }

        Integer partType = values.getAsInteger(PartEntry.COLUMN_TYPE_PART);
        if (partType == null || !PartEntry.isValidPartType(partType)) {
            throw new IllegalArgumentException("Part item must have correct type.");
        }

        Integer partQuantity = values.getAsInteger(PartEntry.COLUMN_QUANTITY);
        if (partQuantity != null && partQuantity < 0) {
            throw new IllegalArgumentException("Part item must have correct quantity.");
        }

        Integer partPrice = values.getAsInteger(PartEntry.COLUMN_PRICE);
        if (partPrice != null && partPrice < 0) {
            throw new IllegalArgumentException("Part item must have correct price.");
        }

        long id = sqLiteDatabase.insert(PartEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTS:
                return updateItem(uri, values, selection, selectionArgs);
            case PARTS_ID:
                selection = PartEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(PartEntry.COLUMN_PART_PICTURE)) {
            String partImage = values.getAsString(PartEntry.COLUMN_PART_PICTURE);
            if (partImage == null) {
                throw new IllegalArgumentException("The part must have a picture added.");
            }
        }

        if (values.containsKey(PartEntry.COLUMN_NAME_PART)) {
            String partName = values.getAsString(PartEntry.COLUMN_NAME_PART);
            if (partName == null) {
                throw new IllegalArgumentException("Part item must have a name.");
            }
        }

        if (values.containsKey(PartEntry.COLUMN_TYPE_PART)) {
            Integer partType = values.getAsInteger(PartEntry.COLUMN_TYPE_PART);
            if (partType == null || !PartEntry.isValidPartType(partType)) {
                throw new IllegalArgumentException("Part item must have correct type.");
            }
        }

        if (values.containsKey(PartEntry.COLUMN_QUANTITY)) {
            Integer partQuantity = values.getAsInteger(PartEntry.COLUMN_QUANTITY);
            if (partQuantity != null && partQuantity < 0) {
                throw new IllegalArgumentException("Part item must have correct quantity.");
            }
        }

        if (values.containsKey(PartEntry.COLUMN_FITS_CAR)) {
            String partFitsCar = values.getAsString(PartEntry.COLUMN_FITS_CAR);
            if (partFitsCar == null) {
                throw new IllegalArgumentException("Part item must contain info on what car it fits.");
            }
        }

        if (values.containsKey(PartEntry.COLUMN_PRICE)) {
            Double partPrice = values.getAsDouble(PartEntry.COLUMN_PRICE);
            if (partPrice != null && partPrice < 0) {
                throw new IllegalArgumentException("Item requires valid price.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase sqLiteDatabase = partHelper.getWritableDatabase();

        int updatedRows = sqLiteDatabase.update(PartEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqLiteDatabase = partHelper.getWritableDatabase();
        int deletedRows;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PARTS:
                deletedRows = sqLiteDatabase.delete(PartEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PARTS_ID:
                selection = PartEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = sqLiteDatabase.delete(PartEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRows;
    }
}
