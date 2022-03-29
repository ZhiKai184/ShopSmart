package com.sp.p2002640assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class DataInput extends AppCompatActivity {
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemQuantity;
    private EditText itemExpiry;
    private RadioGroup itemTypes;
    private Button buttonSave;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;

    private TextView location = null;
    private GPSTracker gpsTracker;

    private Helper helper = null;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private String restaurantID = "";
    public byte[] byteArray;

    ImageView mImageView;
    Button mChooseBtn;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);
        itemName = findViewById(R.id.item_name);
        itemPrice = findViewById(R.id.item_price);
        itemQuantity = findViewById(R.id.item_quantity);
        itemExpiry = findViewById(R.id.item_expiry);
        itemTypes = findViewById(R.id.item_types);

        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(onSave);
        helper = new Helper(this);

        location = findViewById(R.id.location);
        gpsTracker = new GPSTracker(DataInput.this);

        restaurantID = getIntent().getStringExtra("ID");
        if (restaurantID != null) {
            load();
        }

        mImageView = findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_image_btn);

        //handle button click
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }

            }
        });
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    //handle result of runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //permission was granted
                    pickImageFromGallery();
                } else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            mImageView.setImageURI(data.getData());

            mImageView.setDrawingCacheEnabled(true);

            mImageView.buildDrawingCache();

            Bitmap bm = mImageView.getDrawingCache();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byteArray = stream.toByteArray();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        gpsTracker.stopUsingGPS();
    }

    private void load() {
        Cursor c = helper.getById(restaurantID);
        c.moveToFirst();

        latitude = helper.getLatitude(c);
        longitude = helper.getLongitude(c);
        Intent intent;
        intent = new Intent(DataInput.this, ItemMap.class);
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.details_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.get_location) {
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                location.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));
                // \n is for new line
                Toast.makeText(getApplicationContext(), "Your location is - \nLat; " + latitude
                        + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }
            return (true);
        } else if (item.getItemId() == R.id.show_map) {
            //Get my current location
            myLatitude = gpsTracker.getLatitude();
            myLongitude = gpsTracker.getLongitude();

            Intent intent = new Intent(this, ItemMap.class);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("MYLATITUDE", myLatitude);
            intent.putExtra("MYLONGITUDE", myLongitude);
            intent.putExtra("NAME", itemName.getText().toString());
            startActivity(intent);
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (byteArray == null) {
                Toast.makeText(v.getContext(), "You need to input an image!", Toast.LENGTH_LONG).show();
                Intent intent;
                intent = new Intent(DataInput.this, DataInput.class);
                startActivity(intent);

            } else {
                String nameStr = itemName.getText().toString();
                String priceStr = itemPrice.getText().toString();
                String expirystr = itemExpiry.getText().toString();
                String quantityStr = itemQuantity.getText().toString();
                String item_Types = "";

                switch (itemTypes.getCheckedRadioButtonId()) {
                    case R.id.groceries:
                        item_Types = "Groceries";
                        break;
                    case R.id.snacks:
                        item_Types = "Snacks";
                        break;
                    case R.id.drinks:
                        item_Types = "Drinks";
                        break;
                    case R.id.health:
                        item_Types = "Health";
                        break;
                    case R.id.others:
                        item_Types = "Others";
                        break;
                }

                if (restaurantID == null) {
                    helper.insert(nameStr, priceStr, expirystr, quantityStr, item_Types, latitude, longitude, byteArray);
                } else {
                    helper.update(restaurantID, nameStr, priceStr, expirystr, quantityStr, item_Types, latitude, longitude, byteArray);
                }

                //To finish the activity
                finish();
            }
        }
    };
}