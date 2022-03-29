package com.sp.p2002640assignment;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sp.p2002640assignment.databinding.ActivityItemmapBinding;

public class ItemMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lon;
    private String itemName;
    private double myLat;
    private double myLon;
    private LatLng ITEM;
    private LatLng ME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        lat = getIntent().getDoubleExtra("LATITUDE", 0);
        lon = getIntent().getDoubleExtra("LONGITUDE", 0);
        itemName = getIntent().getStringExtra("NAME");
        myLat = getIntent().getDoubleExtra("MYLATITUDE", 0);
        myLon = getIntent().getDoubleExtra("MYLONGITUDE", 0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.item_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ITEM = new LatLng (lat, lon);
        ME = new LatLng(myLat, myLon);

        Marker restaurant = mMap.addMarker(new MarkerOptions().position(ITEM).title(itemName));
        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME")
                .snippet("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITEM, 15));
    }
}