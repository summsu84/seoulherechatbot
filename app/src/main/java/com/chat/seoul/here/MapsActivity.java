package com.chat.seoul.here;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.chat.seoul.here.module.model.place.PlaceModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PlaceModel placeModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Intent intent = getIntent();
        //placeModel = (PlaceModel) intent.getSerializableExtra("clickedPlace");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

        // Add a marker in Sydney and move the camera

        //위경도 좌표를 가져온다..
        //LatLng placeLatLng = new LatLng(Double.parseDouble(placeModel.getX_COORD()), Double.parseDouble(placeModel.getY_COORD()));
        LatLng placeLatLng = new LatLng(37.5634110, 126.9837813);
        mMap.addMarker(new MarkerOptions().position(placeLatLng).title("테스트 마크 입니다."));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng, 18));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 18));
    }


}
