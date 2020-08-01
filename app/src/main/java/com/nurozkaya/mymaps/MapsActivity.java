package com.nurozkaya.mymaps;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener { // harita hazır olduğunda yapılacak işlemler

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //kullanıcının yerini bulmak için
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) { // kullanıcının yeri değiştiğinde napılacağı

                /*//comment haline getirirsek son konumu göstericek
                mMap.clear(); //değişiklik yaptığımızda bir önceki marker silinir

                LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15)); // kamerayı zoomlamak için
          */

            }
        };



                //izin kontrolü
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else { // izin varsa
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener); // kullanıcının yerini al
            // 0 0 verirsem her saniye loc updatei yapmaya çalışır
        } */


                //izinlerin düşük sdk li versiyonlarda da çalışması için
                if (Build.VERSION.SDK_INT >= 23) { // marshmallow ve altını ayrı ayrı
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        // son bilinen lokasyonu tutma
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        System.out.println("lastLocation: " + lastLocation);
                        LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().title("Your Location").position(userLastLocation));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15));
                    }

                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    // son bilinen lokasyonu tutma
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    System.out.println("lastLocation: " + lastLocation);
                    LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().title("Your Location").position(userLastLocation));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15));

                }

                mMap.setOnMapLongClickListener(this);

            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                // kullanıcı izin verdiğinde olacaklar
                if (grantResults.length > 0) { // bir sonuç geldiyse
                    if (requestCode == 1) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                        }
                    }

                }


                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            }

    @Override
    public void onMapLongClick(LatLng latLng) { // kullanıcının tıkladığı yeri latLng a çeviren method

        mMap.clear(); // markerları temizler

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "" ;

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList != null && addressList.size() > 0) {

                if(addressList.get(0).getThoroughfare() != null){  // cadde adı alıyor
                    address = address + addressList.get(0).getThoroughfare();


                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address = address + addressList.get(0).getSubThoroughfare();

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

         // adresi alamadıysak
        if (address.matches("")) {
            address ="No Address";
        }

        // marker ekliyoruz
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));


    }
}
