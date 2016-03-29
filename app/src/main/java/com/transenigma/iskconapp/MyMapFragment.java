package com.transenigma.iskconapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MyMapFragment extends Fragment {

    //ImageButton myCentresBtn;
    String currentTitle = "";
    LatLng myLatLng = new LatLng(24.135260, 77.981031);
    GPSTracker gps;
    Context context;
    String parseClassName = "";
    List<MyMapFragment.LatLngInfo> latLngInfoList = Collections.emptyList();
    MapView mMapView;
    private GoogleMap googleMap;
    private static final String ARG_POSITION = "position";
    private double latitude, longitude;

    private int position;

    public static MyMapFragment newInstance(int position) {
        MyMapFragment f = new MyMapFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);

        //Log.i("Radhe", "In Fragment On Create Latitude is "+latitude+" and longitude is "+longitude);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myLatLng = ((ISKCON_Centres) getActivity()).getMyLatLng();
        View v = inflater.inflate(R.layout.fragment_centres, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        //myCentresBtn = (ImageButton) v.findViewById(R.id.myCentresBtn);
        //myCentresBtn.setVisibility(View.INVISIBLE);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                try {
                    //myCentresBtn.setVisibility(View.VISIBLE);
                    currentTitle = marker.getTitle();
                    Intent intent = new Intent(getActivity(), centres_detail.class);
                    intent.putExtra("title", currentTitle);
                    getActivity().startActivity(intent);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.i("Radhe", "ArrayIndexOutOfBoundsException Occured "+e.toString());
                }

            }
        });

//        myCentresBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), centres_detail.class);
//                intent.putExtra("title", currentTitle);
//                getActivity().startActivity(intent);
//            }
//        });
        context = v.getContext();
        showIndividualItems();
        return v;
    }

    private void showIndividualItems() {
        switch (position) {
            case 0: parseClassName = "Near_Me";
                break;
            case 1: parseClassName = "Restraunts";
                break;
            case 2: parseClassName = "Farms";
                break;
            case 3: parseClassName = "Temples";
                break;
            case 4: parseClassName = "Bhakti_Centres";
                break;
            case 5: parseClassName = "Preaching_Centres";
                break;
            default: parseClassName = "Near_Me";
                break;
        }

        fetchAllLatLangsFromParse();
//        putData( 22.509027, 88.339746, 1 , "Govindas, Kolkata" );
//        putData( 19.655698, 72.965586, 2 , "Govardhan Eco Village" );
//        putData( 22.509027, 88.339746, 3 , "Sri Sri Radha Govinda Mandir" );
//        putData( 40.723965, -73.988516, 4 , "Bhakti Centre New York" );
//        putData( 22.322437, 87.298047, 5 , "Rameshwaram  Voice" );
    }

    private void putData(double lat, double lng, int label, String title) {
        ParseObject gameScore = new ParseObject("centres");
        gameScore.put("latitude", lat);
        gameScore.put("longitude", lng);
        gameScore.put("label", label);
        gameScore.put("title", title);
        gameScore.saveInBackground();
    }

    private void fetchAllLatLangsFromParse() {

        final ProgressDialog progress = ProgressDialog.show( context, "Please Wait!!", "This might take few minutes", true );
        ParseQuery<ParseObject> query = ParseQuery.getQuery("centres");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null) {
                    latLngInfoList = new ArrayList<MyMapFragment.LatLngInfo>();
                    for (ParseObject post : list) {
                        latLngInfoList.add(new LatLngInfo(new LatLng(post.getDouble("latitude"), post.getDouble("longitude")), post.getInt("label"), post.getString("title")));
                        showLatLangsOnMap(latLngInfoList);
                        progress.dismiss();
                    }
                }
            }
        });
    }

    private void showLatLangsOnMap(List<LatLngInfo> latLngInfoList) {

        for(LatLngInfo latLngInfo : latLngInfoList)
        {
            MarkerOptions marker = new MarkerOptions().position(latLngInfo.getLatLng()).title(latLngInfo.getTitle());

            switch (latLngInfo.getLabel()) {
                case 1: marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.restraunts));
                    break;
                case 2: marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.farms));
                    break;
                case 3: marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.temples));
                    break;
                case 4: marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bhakti_centres));
                    break;
                case 5: marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.preaching_centres));
                    break;
                default: marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    break;
            }
            if(position == latLngInfo.getLabel()) googleMap.addMarker(marker);
            if(position==0) googleMap.addMarker(marker);
        }

        //Log.i("Radhe", "In Fragment Latitude is "+latitude+" and longitude is "+longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).zoom(6).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public class LatLngInfo {
        LatLng latLng;
        int label;
        String title;
        public LatLngInfo(LatLng latLng, int label , String title){
            this.latLng = latLng;
            this.label = label;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public int getLabel() {
            return label;
        }
    }
}
