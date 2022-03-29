package com.sp.p2002640assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME  = "infodisplay.dh";
    private static final int SCHEMA_VERSION = 1;

    public Helper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Will be called once when the database is not created
        db.execSQL("CREATE TABLE items_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " itemName TEXT, itemExpiry TEXT, itemPrice TEXT, itemType TEXT, itemQuantity TEXT, lat REAL, lon REAL, byteArray BLOB);");
    }
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion){
        //Will not be called until SCHEMA_VERSION increases
        //Here we can upgrade the database e.g. add more table
    }

    /* Read all records from items_table */
    public Cursor getAll(){
        return (getReadableDatabase().rawQuery(
                "SELECT _id, itemName, itemExpiry, itemPrice," +
                        " itemType, itemQuantity, lat, lon, byteArray FROM items_table ORDER BY itemName", null));
    }

    public Cursor getById(String id){
        String[] args = {id};
        return (getReadableDatabase().rawQuery(
                "SELECT _id, itemName, itemExpiry, itemPrice, " +
                        "itemType, itemQuantity, lat, lon, byteArray FROM items_table WHERE _ID = ?", args));
    }
    /* Writes a record into items_table */
    public void insert(String itemName, String itemExpiry,
                       String itemPrice, String itemType, String itemQuantity, double lat, double lon, byte[]byteArray) {
        ContentValues cv = new ContentValues();

        cv.put("itemName", itemName );
        cv.put("itemExpiry", itemExpiry);
        cv.put("itemPrice", itemPrice);
        cv.put("itemType", itemType);
        cv.put("itemQuantity", itemQuantity);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("byteArray", byteArray);

        getWritableDatabase().insert("items_table", "itemName", cv);
    }

    /* Update a particular record in items_table with id provided */

    public void update (String id, String itemName, String itemExpiry, String itemPrice,
                        String itemType, String itemQuantity, double lat, double lon, byte[]byteArray) {
        ContentValues cv= new ContentValues();
        String[] args ={id};
        cv.put("itemName", itemName);
        cv.put("itemExpiry", itemExpiry);
        cv.put("itemPrice", itemPrice);
        cv.put("itemType", itemType);
        cv.put("itemQuantity", itemQuantity);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("byteArray", byteArray);

        getWritableDatabase().update("items_table", cv, "_ID = ?", args);
    }

    public String getID(Cursor c) {return (c.getString(0));}

    public String getItemName(Cursor c){
        return (c.getString(1));
    }

    public String getItemPrice(Cursor c){
        return (c.getString(2));
    }

    public String getItemExpiry(Cursor c){
        return (c.getString(3));
    }

    public String getItemQuantity(Cursor c){return(c.getString(4)); }

    public String getItemType (Cursor c){ return (c.getString(5)); }

    public double getLatitude(Cursor c) {return (c.getDouble(6));}

    public double getLongitude(Cursor c) {return (c.getDouble(7));}

    public byte[] getImage(Cursor c) {return(c.getBlob(8));}
}

