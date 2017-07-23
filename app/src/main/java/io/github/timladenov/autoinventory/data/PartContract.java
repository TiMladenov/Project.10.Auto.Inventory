package io.github.timladenov.autoinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class PartContract {

    public static final String CONTENT_AUTHORITY = "io.github.timladenov.autoinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "parts";

    private PartContract() {}


    public static final class PartEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);
        public static final String TABLE_NAME = "part";
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TYPE_PART = "type_part";
        public static final String COLUMN_NAME_PART = "part_name";
        public static final String COLUMN_QUANTITY = "part_quantity";
        public static final String COLUMN_FITS_CAR = "fits_car";
        public static final String COLUMN_PART_PICTURE = "part_picture";
        public static final String COLUMN_PART_DATA = "part_data";
        public static final String COLUMN_PRICE = "price";

        public static final int PART_UNKNOWN = 0;
        public static final int PART_INTERIOR = 1;
        public static final int PART_EXTERIOR = 2;
        public static final int PART_SUSPENSION = 3;
        public static final int PART_ENGINE = 4;
        public static final int PART_GEARBOX = 5;
        public static final int PART_AC = 6;
        public static final int PART_ELECTRICAL = 7;

        public static boolean isValidPartType(int type) {
            if (type == PART_UNKNOWN || type == PART_INTERIOR ||
                    type == PART_EXTERIOR || type == PART_SUSPENSION ||
                    type == PART_ENGINE || type == PART_GEARBOX ||
                    type == PART_AC || type == PART_ELECTRICAL) {
                return true;
            }
            return false;
        }
    }
}