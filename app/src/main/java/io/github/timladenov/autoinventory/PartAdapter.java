package io.github.timladenov.autoinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.github.timladenov.autoinventory.data.PartContract.PartEntry;

import static io.github.timladenov.autoinventory.R.id.part_fits;

public class PartAdapter extends CursorAdapter {

    private class ViewHolder {
        private ImageView partPicture;
        private TextView partName;
        private TextView partType;
        private TextView partInfo;
        private TextView partFits;
        private TextView partInStock;
        private TextView partQuantity;
        private TextView partPrice;
        private Button buttonSellPart;

        private ViewHolder(View view) {
            partPicture = (ImageView) view.findViewById(R.id.part_picture);
            partName = (TextView) view.findViewById(R.id.part_name);
            partType = (TextView) view.findViewById(R.id.part_type);
            partInfo = (TextView) view.findViewById(R.id.part_description);
            partFits = (TextView) view.findViewById(part_fits);
            partInStock = (TextView) view.findViewById(R.id.parts_in_stock);
            partQuantity = (TextView) view.findViewById(R.id.part_quantity);
            partPrice = (TextView) view.findViewById(R.id.part_price);
            buttonSellPart = (Button) view.findViewById(R.id.sell_button);
        }
    }

    public PartAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    private final Uri interim_img_uri = Uri.parse(
            "android.resource://io.github.timladenov.autoinventory/drawable/news_image_2;\\n"
    );

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final long id = cursor.getLong(cursor.getColumnIndex(PartEntry._ID));
        int partImageIndex = cursor.getColumnIndex(PartEntry.COLUMN_PART_PICTURE);
        int partNameIndex = cursor.getColumnIndex(PartEntry.COLUMN_NAME_PART);
        int partTypeIndex = cursor.getColumnIndex(PartEntry.COLUMN_TYPE_PART);
        int partDataIndex = cursor.getColumnIndex(PartEntry.COLUMN_PART_DATA);
        int partFitsIndex = cursor.getColumnIndex(PartEntry.COLUMN_FITS_CAR);
        int partQuantityIndex = cursor.getColumnIndex(PartEntry.COLUMN_QUANTITY);
        int partPriceIndex = cursor.getColumnIndex(PartEntry.COLUMN_PRICE);

        String imageString = cursor.getString(partImageIndex);
        Uri imageUri = Uri.parse(imageString);
        String partNameString = cursor.getString(partNameIndex);
        String partFitsString = cursor.getString(partFitsIndex);
        String partInfo = cursor.getString(partDataIndex);
        int partType = cursor.getInt(partTypeIndex);
        final int stockParts = cursor.getInt(partQuantityIndex);
        if(stockParts > 0) {
            viewHolder.buttonSellPart.setEnabled(true);
            viewHolder.buttonSellPart.setTextColor((ContextCompat.getColor(context, R.color.color_black)));
        }

        double partPrice = cursor.getDouble(partPriceIndex);

        viewHolder.partQuantity.setText(Integer.toString(stockParts));
        if (stockParts == 0) {
            viewHolder.partInStock.setText(R.string.out_of_stock);
            viewHolder.partInStock.setTextColor(ContextCompat.getColor(context, R.color.no_quantity));
        } else {
            viewHolder.partInStock.setText(R.string.in_stock);
            viewHolder.partInStock.setTextColor(ContextCompat.getColor(context, R.color.yes_quantity));
        }

        viewHolder.partPicture.setImageURI(imageUri);
        viewHolder.partPicture.invalidate();

        if (viewHolder.partPicture == null) {
            viewHolder.partPicture.setImageURI(interim_img_uri);
            viewHolder.partPicture.invalidate();
        }

        viewHolder.partName.setText(partNameString);
        viewHolder.partInfo.setText(partInfo);
        viewHolder.partFits.setText(partFitsString);

        switch (partType) {
            case PartEntry.PART_INTERIOR:
                viewHolder.partType.setText(R.string.options_interior);
                break;
            case PartEntry.PART_EXTERIOR:
                viewHolder.partType.setText(R.string.options_exterior);
                break;
            case PartEntry.PART_SUSPENSION:
                viewHolder.partType.setText(R.string.options_suspension);
                break;
            case PartEntry.PART_ENGINE:
                viewHolder.partType.setText(R.string.options_engine);
                break;
            case PartEntry.PART_GEARBOX:
                viewHolder.partType.setText(R.string.options_gearbox);
                break;
            case PartEntry.PART_AC:
                viewHolder.partType.setText(R.string.options_ac);
                break;
            case PartEntry.PART_ELECTRICAL:
                viewHolder.partType.setText(R.string.options_electrical);
                break;
            default:
                viewHolder.partType.setText(R.string.unknown_type);
        }

        viewHolder.partPrice.setText(Double.toString(partPrice));

        viewHolder.buttonSellPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stockParts > 0) {
                    int newStockPartQuanity = stockParts - 1;
                    ContentValues values = new ContentValues();
                    Uri currentItemUri = ContentUris.withAppendedId(PartEntry.CONTENT_URI, id);

                    values.put(PartEntry.COLUMN_QUANTITY, newStockPartQuanity);
                    context.getContentResolver().update(currentItemUri, values, null, null);
                    viewHolder.partQuantity.setText(Integer.toString(newStockPartQuanity));
                } else {
                    Toast.makeText(context, context.getString(R.string.warning_out_of_stock), Toast.LENGTH_SHORT).show();
                    viewHolder.buttonSellPart.setTextColor((ContextCompat.getColor(context, R.color.color_gray)));
                    viewHolder.buttonSellPart.setEnabled(false);
                }
            }
        });
    }
}
