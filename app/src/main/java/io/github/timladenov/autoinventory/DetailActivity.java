package io.github.timladenov.autoinventory;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import io.github.timladenov.autoinventory.data.PartContract.PartEntry;

import static io.github.timladenov.autoinventory.R.id.order_layout;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int MAIN_LOADER_ID = 0;
    private static final int RW_PERMISSION = 2;
    private static final int SET_IMAGE = 0;

    private Button partCountDecr;
    private Button partCountIncr;
    private Button placeOrder;
    private EditText partName;
    private EditText partData;
    private EditText fitsCar;
    private EditText partPrice;
    private TextView changeQuantity;
    private ImageView partImage;
    private Spinner optionsSpinner;
    private Uri selectedPartUri;
    private Uri selectedPartImgUri;
    private View orderLayout;
    private LinearLayout orderButtonLayout;
    private int partsQuantity;
    private int partItem_Id = 0;

    private boolean updated_item = false;
    private boolean save_item = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            updated_item = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        selectedPartUri = intent.getData();

        partImage = (ImageView) findViewById(R.id.part_picture);
        partName = (EditText) findViewById(R.id.part_name);
        partData = (EditText) findViewById(R.id.part_data);
        fitsCar = (EditText) findViewById(R.id.fits_car);
        partPrice = (EditText) findViewById(R.id.part_price);
        changeQuantity = (TextView) findViewById(R.id.part_quantity);
        optionsSpinner = (Spinner) findViewById(R.id.type_spinner);
        partCountDecr = (Button) findViewById(R.id.dec_part_quantity);
        partCountIncr = (Button) findViewById(R.id.add_part_quantity);
        placeOrder = (Button) findViewById(R.id.part_order_button);
        orderLayout = findViewById(order_layout);
        orderLayout.setVisibility(View.GONE);
        orderButtonLayout = (LinearLayout) findViewById(R.id.order_button_layout);
        orderButtonLayout.setVisibility(View.GONE);

        partImage.setOnClickListener(this);
        partCountDecr.setOnClickListener(this);
        partCountIncr.setOnClickListener(this);
        placeOrder.setOnClickListener(this);

        partName.setOnTouchListener(touchListener);
        partData.setOnTouchListener(touchListener);
        fitsCar.setOnTouchListener(touchListener);
        partPrice.setOnTouchListener(touchListener);
        changeQuantity.setOnTouchListener(touchListener);
        optionsSpinner.setOnTouchListener(touchListener);
        partCountDecr.setOnTouchListener(touchListener);
        partCountIncr.setOnTouchListener(touchListener);
        partsQuantity = 0;

        if (savedInstanceState != null) {
            partsQuantity = savedInstanceState.getInt("partsQuantity");
        }

        if (selectedPartUri == null) {
            setTitle(getString(R.string.add_part));
        } else {
            setTitle(getString(R.string.edit_part));
            orderLayout.setVisibility(View.VISIBLE);
            orderButtonLayout = (LinearLayout) findViewById(R.id.order_button_layout);
            orderButtonLayout.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(MAIN_LOADER_ID, null, this);
        }
        createSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("partsQuantity", partsQuantity);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.part_picture:
                if (ContextCompat.checkSelfPermission
                        (DetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission
                                (DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE))
                    {} else {
                        ActivityCompat.requestPermissions(
                                DetailActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, RW_PERMISSION);
                    }
                } else {
                    partImage.setEnabled(true);
                    pickImage();
                }
                updated_item = true;
                break;
            case R.id.add_part_quantity:
                if (partsQuantity < 20) {
                    partsQuantity++;
                    changeQuantity.setText(Integer.toString(partsQuantity));
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.warning_limit_quantity),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.dec_part_quantity:
                if (partsQuantity > 0) {
                    partsQuantity--;
                    changeQuantity.setText(Integer.toString(partsQuantity));
                } else {
                    Toast.makeText(DetailActivity.this, getString(R.string.warning_negative_quantity),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.part_order_button:
                if(partsQuantity <= 0) {
                    Toast.makeText(this, getString(R.string.warning_no_items), Toast.LENGTH_SHORT).show();
                } else {
                    Intent orderParts = new Intent(Intent.ACTION_SENDTO);
                    String supplier_order = getString(R.string.order_supplier_email);
                    orderParts.setData(Uri.parse(supplier_order));
                    if (!partName.getText().toString().isEmpty()) {
                        orderParts.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_supplier_email_subject_1) + partName.getText().toString().trim());
                    } else {
                        orderParts.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_supplier_email_subject_2));
                    }
                    orderParts.putExtra(Intent.EXTRA_TEXT, getString(R.string.order_supplier_email_body));
                    if (orderParts.resolveActivity(getPackageManager()) != null) {
                        startActivity(orderParts);
                    }
                }

                break;
        }
    }

    private void createSpinner() {
        final ArrayAdapter optionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        optionsSpinner.setAdapter(optionsAdapter);

        optionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpinnerItem = parent.getItemAtPosition(position).toString();
                if (!TextUtils.isEmpty(selectedSpinnerItem)) {
                    if (selectedSpinnerItem.equals(getString(R.string.options_unknown))) {
                        partItem_Id = PartEntry.PART_UNKNOWN;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_interior))) {
                        partItem_Id = PartEntry.PART_INTERIOR;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_exterior))) {
                        partItem_Id = PartEntry.PART_EXTERIOR;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_suspension))) {
                        partItem_Id = PartEntry.PART_SUSPENSION;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_engine))) {
                        partItem_Id = PartEntry.PART_ENGINE;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_gearbox))) {
                        partItem_Id = PartEntry.PART_GEARBOX;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_ac))) {
                        partItem_Id = PartEntry.PART_AC;
                    } else if (selectedSpinnerItem.equals(getString(R.string.options_electrical))) {
                        partItem_Id = PartEntry.PART_ELECTRICAL;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                partItem_Id = PartEntry.PART_UNKNOWN;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RW_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedPartImgUri = data.getData();
                partImage.setImageURI(selectedPartImgUri);
                partImage.setImageBitmap(extractUriImage(selectedPartImgUri));
                partImage.invalidate();
            }
        }
    }

    public Bitmap extractUriImage(Uri uri) {
        if (uri == null || uri.toString().isEmpty()) {
            return null;
        }

        int imgWidth = partImage.getWidth();
        int imgHeight = partImage.getHeight();

        InputStream inputStream = null;
        try {
            inputStream = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bitmapfactory = new BitmapFactory.Options();
            bitmapfactory.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, bitmapfactory);

            int resourceW = bitmapfactory.outWidth;
            int resourceH = bitmapfactory.outHeight;
            int scaleFactor = Math.min(resourceW / imgWidth, resourceH / imgHeight);

            bitmapfactory.inJustDecodeBounds = false;
            bitmapfactory.inSampleSize = scaleFactor;
            bitmapfactory.inPurgeable = true;

            inputStream = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapfactory);
            inputStream.close();
            return bitmap;
        } catch (FileNotFoundException noFile) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException ioe) {
                return null;
            }
        }
    }

    private void pickImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.selection)), SET_IMAGE);
    }

    private boolean saveNewPart() {
        ContentValues values = new ContentValues();
        double price = 0;

        if (selectedPartUri == null &&
                TextUtils.isEmpty(partName.getText().toString().trim()) &&
                TextUtils.isEmpty(partData.getText().toString().trim()) &&
                TextUtils.isEmpty(fitsCar.getText().toString().trim()) &&
                TextUtils.isEmpty(changeQuantity.getText().toString().trim()) &&
                TextUtils.isEmpty(partPrice.getText().toString().trim()) &&
                partItem_Id == PartEntry.PART_UNKNOWN && selectedPartImgUri == null) {

            save_item = false;
            Toast.makeText(this, getString(R.string.warning_fill_information), Toast.LENGTH_SHORT).show();
            return save_item;
        }

        if (selectedPartImgUri == null) {
            save_item = false;
            Toast.makeText(this, getString(R.string.warning_fill_information),
                    Toast.LENGTH_SHORT).show();

            return save_item;
        }
        values.put(PartEntry.COLUMN_PART_PICTURE, selectedPartImgUri.toString());

        if (TextUtils.isEmpty(partName.getText().toString().trim())) {
            save_item = false;
            Toast.makeText(this, getString(R.string.warning_enter_part_name),
                    Toast.LENGTH_SHORT).show();

            return save_item;
        }
        values.put(PartEntry.COLUMN_NAME_PART, partName.getText().toString().trim());
        values.put(PartEntry.COLUMN_PART_DATA, partData.getText().toString().trim());
        values.put(PartEntry.COLUMN_TYPE_PART, partItem_Id);
        values.put(PartEntry.COLUMN_FITS_CAR, fitsCar.getText().toString().trim());

        if (changeQuantity.getText().toString().trim().isEmpty()) {
            values.put(PartEntry.COLUMN_QUANTITY, 0);
        } else {
            values.put(PartEntry.COLUMN_QUANTITY, partsQuantity);
        }

        if (!TextUtils.isEmpty(partPrice.getText().toString().trim())) {
            price = Double.parseDouble(partPrice.getText().toString().trim());
        }
        values.put(PartEntry.COLUMN_PRICE, price);

        if (selectedPartUri == null) {
            Uri newUri = getContentResolver().insert(PartEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.message_error_saving_part),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_saved_part),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int updatedRows = getContentResolver().update(
                    selectedPartUri,
                    values,
                    null,
                    null);
            if (updatedRows == 0) {
                Toast.makeText(this, getString(R.string.message_error_updating_part),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_updated_part),
                        Toast.LENGTH_SHORT).show();
            }
        }
        save_item = true;
        return save_item;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_part:
                if (saveNewPart()) {
                    finish();
                }
                return true;
            case R.id.delete_part:
                deleteDialog();
                return true;
            case android.R.id.home:
                if (!updated_item) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener quitDialog =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };
                saveDialog(quitDialog);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!updated_item) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener quitDialog =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                };

        saveDialog(quitDialog);
    }

    private void saveDialog(
            DialogInterface.OnClickListener quitDialog) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.confirmation_window_1));
        alertBuilder.setPositiveButton(getString(R.string.confirmation_window_quit), quitDialog);
        alertBuilder.setNegativeButton(getString(R.string.confirmation_window_stay),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void deleteDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.confirmation_delete_part));
        alertBuilder.setPositiveButton(getString(R.string.confirmation_delete_part_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePart();
                        finish();
                    }
                });
        alertBuilder.setNegativeButton(getString(R.string.confirmation_delete_part_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void deletePart() {
        if (selectedPartUri != null) {
            int deletedParts = getContentResolver().delete(
                    selectedPartUri,
                    null,
                    null);

            if (deletedParts == 0) {
                Toast.makeText(this, getString(R.string.message_part_delete_fail),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.message_part_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
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
                PartEntry.COLUMN_PRICE,
                PartEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                selectedPartUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int partImageIndex = cursor.getColumnIndex(PartEntry.COLUMN_PART_PICTURE);
            int partNameIndex = cursor.getColumnIndex(PartEntry.COLUMN_NAME_PART);
            int partTypeIndex = cursor.getColumnIndex(PartEntry.COLUMN_TYPE_PART);
            int partDataIndex = cursor.getColumnIndex(PartEntry.COLUMN_PART_DATA);
            int partFitsIndex = cursor.getColumnIndex(PartEntry.COLUMN_FITS_CAR);
            int partQuantityIndex = cursor.getColumnIndex(PartEntry.COLUMN_QUANTITY);
            int partPriceIndex = cursor.getColumnIndex(PartEntry.COLUMN_PRICE);

            String imageString = cursor.getString(partImageIndex);
            String partNameString = cursor.getString(partNameIndex);
            String partInfo = cursor.getString(partDataIndex);
            int partType = cursor.getInt(partTypeIndex);
            String partFitsString = cursor.getString(partFitsIndex);
            double partsPrice = cursor.getDouble(partPriceIndex);
            partsQuantity = cursor.getInt(partQuantityIndex);

            selectedPartImgUri = Uri.parse(imageString);
            partImage.setImageURI(selectedPartImgUri);
            partName.setText(partNameString);
            partData.setText(partInfo);
            fitsCar.setText(partFitsString);
            partPrice.setText(Double.toString(partsPrice));
            changeQuantity.setText(Integer.toString(partsQuantity));

            switch (partType) {
                case PartEntry.PART_INTERIOR:
                    optionsSpinner.setSelection(1);
                    break;
                case PartEntry.PART_EXTERIOR:
                    optionsSpinner.setSelection(2);
                    break;
                case PartEntry.PART_SUSPENSION:
                    optionsSpinner.setSelection(3);
                    break;
                case PartEntry.PART_ENGINE:
                    optionsSpinner.setSelection(4);
                    break;
                case PartEntry.PART_GEARBOX:
                    optionsSpinner.setSelection(5);
                    break;
                case PartEntry.PART_AC:
                    optionsSpinner.setSelection(6);
                    break;
                case PartEntry.PART_ELECTRICAL:
                    optionsSpinner.setSelection(7);
                    break;
                default:
                    optionsSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        optionsSpinner.setSelection(PartEntry.PART_UNKNOWN);
        partName.setText("");
        partData.setText("");
        fitsCar.setText("");
        changeQuantity.setText("");
        partPrice.setText("");
    }
}
