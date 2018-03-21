package com.jason.marks.googlemapscrash;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private List<Marker> curDrawnMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lock orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        FragmentManager fm = getSupportFragmentManager();

        SupportMapFragment mapFragment = new SupportMapFragment();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (googleMap != null) {
                    addMarkers(googleMap);
                }
            }
        });

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.map_container, mapFragment, "map");
        ft.commit();
        fm.executePendingTransactions();
    }




    private String LatLngCoords = "39.01,-76.89%2039.39,-77.42%2039.67,-78.08%2039.71,-78.19%2039.65,-79.7%2039.58,-79.97%2040.15,-80.2%2040.19,-80.24%2040.05,-80.66%2040.07,-80.85%2040.01,-81.62%2039.94,-82.38%2039.95,-82.99%2039.95,-83.01%2039.98,-83.11%2039.9,-83.86%2039.87,-84.18%2039.86,-84.95%2039.83,-85.7%2039.79,-86.14%2039.76,-86.14%2039.53,-86.88%2039.42,-87.63%2039.17,-88.38%2039.1,-88.58%2038.91,-89.28%2038.75,-89.9%2038.74,-89.91%2038.63,-90.15%2038.62,-90.19%2038.61,-90.2%2038.54,-90.46%2038.26,-91.11%2037.97,-91.75%2037.76,-92.45%2037.33,-92.95%2037.14,-93.72%2037.05,-94.48%2036.66,-95.09%2036.17,-95.73%2036.16,-95.82%2036.09,-96.04%2035.75,-96.68%2035.6,-97.43%2035.47,-97.71%2035.53,-98.45%2035.44,-99.17%2035.23,-99.92%2035.22,-100.65%2035.22,-101.4%2035.21,-102.17%2035.19,-102.96%2035.16,-103.69%2035.02,-104.45%2034.99,-105.22%2035.0,-105.95%2035.11,-106.65%2035.03,-107.39%2035.34,-108.01%2035.53,-108.73%2035.21,-109.35%2034.98,-110.06%2035.07,-110.84%2035.22,-111.57%2035.22,-112.28%2035.31,-112.99%2035.16,-113.68%2034.73,-114.29%2034.83,-115.03%2034.72,-115.75%2034.79,-116.45%2034.89,-117.01%2034.87,-117.08%2035.14,-118.47%2035.35,-119.03%2035.38,-119.04%2035.93,-119.28%2036.46,-119.48%2036.49,-119.47%2036.55,-119.46%2036.6,-119.46%2036.73,-119.47%2036.77,-119.42";

    private void addMarkers (final GoogleMap map) {
        String latLngCoords[] = LatLngCoords.split("%20");
        curDrawnMarkers = new ArrayList<>(latLngCoords.length);
        Drawable iconDrawable = getResources().getDrawable(R.drawable.ic_error_black_48dp);
        Bitmap iconBitmap = Bitmap.createScaledBitmap(((BitmapDrawable) iconDrawable).getBitmap(), getIconImageSize(), getIconImageSize(), false);
        for (String latLng : latLngCoords) {
            String latLngSingleCoords[] = latLng.split(",");
            curDrawnMarkers.add(map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(latLngSingleCoords[0]), Double.parseDouble(latLngSingleCoords[1])))
                    .icon(BitmapDescriptorFactory.fromBitmap(iconBitmap))));
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.5, -98.35), 4));

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                applyZOrderToMarkers(map);
            }
        });
    }

    /**
     * Iterates over the visible markers on the map and applies a precedence so the marker closest
     * to the center of the map is brought to the top of the other markers.
     */
    private void applyZOrderToMarkers(GoogleMap map) {
        final LatLngBounds currentViewBounds =
                map.getProjection().getVisibleRegion().latLngBounds;
        final LatLng center = currentViewBounds.getCenter();
        final SortedMap<Float, Marker> markerSortedMap = new TreeMap<>();

        for (Marker m : curDrawnMarkers) {
            if (!m.isVisible()) {
                continue;
            }

            LatLng markerPosition = m.getPosition();

            float distanceToCenter[] = new float[3];
            Location.distanceBetween(
                    markerPosition.latitude,
                    markerPosition.longitude,
                    center.latitude,
                    center.longitude,
                    distanceToCenter);
            Log.d("DistanceBetween", Float.toString(distanceToCenter[0]));
            /*  if this distance already exists in the map then don't add it. it will be below by
                default
              */
            if (!markerSortedMap.containsKey(distanceToCenter[0])) {
                markerSortedMap.put(distanceToCenter[0], m);
            }
        }

        int zIndexAmount = markerSortedMap.size();
        for (Map.Entry<Float, Marker> entry : markerSortedMap.entrySet()) {
            Log.d("ZIndex", entry.getValue().getPosition().toString() + " " + zIndexAmount);
            entry.getValue().setZIndex(zIndexAmount <= 0 ? 0 : zIndexAmount--);
        }
    }

    public int getIconImageSize() {
        DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, metrics);
    }


}
