package com.sp.p2002640assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class InfoDisplay extends AppCompatActivity {
    private Cursor model = null;
    private Helper helper = null;
    private RestaurantAdapter adapter = null;
    private ListView list;
    private TextView empty = null;
    private ImageButton edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);
        helper = new Helper(this);
        list = findViewById(R.id.items);
        model = helper.getAll();
        adapter = new RestaurantAdapter(this, model, 0);
        list.setOnItemClickListener(onListClick);
        list.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            model.moveToPosition(position);
            String recordID = helper.getID(model);
            Intent intent;
            intent = new Intent(InfoDisplay.this, DataInput.class);
            intent.putExtra("ID", recordID);
            startActivity(intent);
        }
    };

    static class RestaurantHolder {
        private TextView itemname = null;
        private TextView itemprice = null;
        private TextView itemquantity = null;
        private TextView itemtype = null;
        private TextView itemexpiry = null;
        private ImageView icon = null;

        RestaurantHolder(View row) {
            itemname = row.findViewById(R.id.name);
            icon = row.findViewById(R.id.icon);
            itemprice = row.findViewById(R.id.price);
            itemquantity = row.findViewById(R.id.quantity);
            itemtype = row.findViewById(R.id.type);
            itemexpiry = row.findViewById(R.id.expiry);
        }

        void populateFrom(Cursor c, Helper helper) {
            itemname.setText(helper.getItemName(c));
            String displayprice = "Price: " + helper.getItemPrice(c) + " SGD";
            itemprice.setText(displayprice);
        String displaytype = "Type: " + helper.getItemType(c);
            itemtype.setText(displaytype);
        String displayquantity = "Quantity: " + helper.getItemQuantity(c);
            itemquantity.setText(displayquantity);
        String displayexpiry = "Expiry date: " + helper.getItemExpiry(c);
            itemexpiry.setText(displayexpiry);
        byte[] bytesImage = helper.getImage(c);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytesImage, 0, bytesImage.length);
            icon.setImageBitmap(bitmapImage);
    }
    }

    class RestaurantAdapter extends CursorAdapter {
        RestaurantAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            RestaurantHolder holder = (RestaurantHolder) view.getTag();
            holder.populateFrom(cursor, helper);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            RestaurantHolder holder = new RestaurantHolder(row);
            row.setTag(holder);
            return (row);
        }
    }
}
