package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class add_event extends FragmentActivity implements OnMapReadyCallback {

    @Bind(R.id.mySearchText) EditText mySearchText;
    @Bind(R.id.myConfirmLocation) Button myConfirmLocation;
    ProgressDialog progressDialog;
    Marker marker;
    String eventName="", blockAddress="", street="", city="", state="", country="", description="", Country="", locality="",
            myEventObjectId ="", targetAudience="", imageUri="", tags = "", ContactName="", ContactNo = "", ContactEmail ="",
            targetAge = "",targetAshram="", targetGender="";
    Date startDateTime, endDateTime;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    Context context = this;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            eventName = bundle.getString("eventName");
            blockAddress = bundle.getString("blockAddress");
            street = bundle.getString("street");
            city = bundle.getString("city");
            state = bundle.getString("state");
            country = bundle.getString("country");
            description = bundle.getString("description");
            imageUri = bundle.getString("uri");
            targetAudience = bundle.getString("targetAudience");
            startDateTime = (Date)bundle.get("startDateTime");
            endDateTime = (Date)bundle.get("endDateTime");
            tags = (String)bundle.get("tags");
            ContactEmail = (String)bundle.get("ContactEmail");
            ContactName = (String)bundle.get("ContactName");
            ContactNo = (String)bundle.get("ContactNo");
            targetAge = (String)bundle.get("targetAge");
            targetAshram = (String)bundle.get("targetAshram");
            targetGender = (String)bundle.get("targetGender");

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        setUpMapIfNeeded();

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                googleMap.clear();
                setMarker(new LatLng(position.target.latitude,position.target.longitude), googleMap.getCameraPosition().zoom);
            }
        });
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null)
            {
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){

                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.infowindow,null);
                        TextView myTitle = (TextView)v.findViewById(R.id.myTitle);
                        TextView mySnippet = (TextView)v.findViewById(R.id.mySnippet);

                        LatLng ll = marker.getPosition();
                        myTitle.setText(marker.getTitle());
                        mySnippet.setText(marker.getSnippet());

                        return v;
                    }
                });

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Geocoder gc = new Geocoder(add_event.this);
                        List<Address> list = null;
                        saveLatLngToParse(latLng);
                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        Address address = list.get(0);
                        locality = address.getLocality();
                        Country = address.getCountryName();
                        setMarker(latLng, googleMap.getCameraPosition().zoom);
                        marker.showInfoWindow();
                    }
                });

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        Geocoder gc = new Geocoder(add_event.this);
                        List<Address> list = null;
                        LatLng latLng = marker.getPosition();
                        saveLatLngToParse(latLng);
                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        if (list != null & list.size() > 0) {
                            Address address = list.get(0);
                            marker.setTitle(address.getLocality());
                            marker.setSnippet(address.getCountryName());
                            setMarker(latLng,googleMap.getCameraPosition().zoom);
                            marker.showInfoWindow();
                        }
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                    }
                });


                setUpMap();
            }
        }
    }

    public void clickConfirmLocation(View view) {
        myConfirmLocation.setEnabled(false);
        Toast.makeText(this,"Please Wait!", Toast.LENGTH_LONG).show();
        showProcessDialogBox();
        saveLatLngToParse(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
//        Intent intent = new Intent(this,detail.class);
//        //getEventObejectId();
//        intent.putExtra("myEventObjectId",myEventObjectId);
//        Log.i("Radhe", "inside clickConfirmLocation object id "+myEventObjectId);
//        startActivity(intent);
    }

    private void getEventObejectId() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.whereEqualTo("eventName", eventName);
        query.whereEqualTo("blockAddress", blockAddress);
        query.whereEqualTo("street", street);
        query.whereEqualTo("city", city);
        query.whereEqualTo("state", state);
        query.whereEqualTo("country", country);
        query.whereEqualTo("description", description);
        query.whereEqualTo("startDateTime", startDateTime);
        query.whereEqualTo("endDateTime", endDateTime);
        query.whereEqualTo("latitude", marker.getPosition().latitude);
        query.whereEqualTo("longitude", marker.getPosition().longitude);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.i("Radhe", "The getFirst request failed.");
                } else {
                    myEventObjectId = object.getObjectId();
                }
            }
        });

    }

    private void saveLatLngToParse(final LatLng latLng) {
        String[] parts = (tags.replaceAll("\\s","")).split(",");
        String[] partsAge = (targetAge.replaceAll("\\s","")).split(",");
        String[] partsGender = (targetGender.replaceAll("\\s","")).split(",");
        String[] partsAshram = (targetAshram.replaceAll("\\s","")).split(",");
        ParseObject testObject = new ParseObject("events");
        final ParseObject parseObject = testObject;
        testObject.put("eventName", eventName);
        testObject.put("blockAddress", blockAddress);
        testObject.put("street", street);
        testObject.put("city", city);
        testObject.put("state", state);
        testObject.put("country", country);
        testObject.put("description",description);
        testObject.put("target", getTargetValue(targetAudience));
        testObject.put("startDateTime", startDateTime);
        testObject.put("endDateTime", endDateTime);
        testObject.put("latitude", latLng.latitude);
        testObject.put("longitude", latLng.longitude);
        testObject.put("tags", new ArrayList<String>(Arrays.asList(parts)));
        testObject.put("TargetAge", new ArrayList<String>(Arrays.asList(partsAge)));
        testObject.put("TargetGender", new ArrayList<String>(Arrays.asList(partsGender)));
        testObject.put("TargetAshram", new ArrayList<String>(Arrays.asList(partsAshram)));
        testObject.put("image",getFileFromUri());
        testObject.put("ContactName",ContactName);
        testObject.put("ContactNo",ContactNo);
        testObject.put("ContactEmail",ContactEmail);
        testObject.put("user", ParseUser.getCurrentUser());

        testObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        progressDialog.dismiss();
                        myEventObjectId = parseObject.getObjectId();
                        Intent intent = new Intent(getApplicationContext(), ShowEventToUser.class);
                        Toast.makeText(context, "Your event has been sent for verification", Toast.LENGTH_LONG).show();
                        intent.putExtra("myEventObjectId", myEventObjectId);
                        Log.i("Radhe", "inside clickConfirmLocation object id " + myEventObjectId);
                        startActivity(intent);
                        finish();
                    }else {
                        Log.i("Radhe", "Could not add the event to Parse due to error " + e.toString());
                        Toast.makeText(context, "Could not add the event to Parse due to error " + e.toString(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
        });

//        testObject.saveInBackground(new SaveCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    myEventObjectId = testObject.getObjectId();
//                } else {
//                    // Failure!
//                }
//            }
//        }, new ProgressCallback() {
//            public void done(Integer percentDone) {
//                // Update your progress spinner here. percentDone will be between 0 and 100.
//            }
//        });

    }
    private void showProcessDialogBox() {
         progressDialog = ProgressDialog.show( this, "Please Wait!", "Your Event is being added ,it may take few minutes", true);
//        progressDialog = new ProgressDialog(this , R.style.AppTheme_Dark_Dialog);
//        //progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Please wait while Your event is being added...");
//        progressDialog.show();
        Log.i("Radhe","Inside Process Dialog Box");
    }
    private ParseFile getFileFromUri() {
        Uri myUri = Uri.parse("file://"+imageUri);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img3);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);
            int scale = getScale(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/scale, bitmap.getHeight()/scale, false);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Radhe", "IOException in getting bitmap from URI "+e);
        }
        return conversionBitmapToParseFile(bitmap);
    }

    private int getScale(int width, int height) {
        int scale = 1;
        if(width>350)
            scale = width/350;

        return scale;
    }

    private ParseFile conversionBitmapToParseFile(Bitmap imageBitmap) {

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    private int getTargetValue(String targetAudience) {
        int a= 47;
        switch (targetAudience) {
            case "Event City":  a = 3;
            break;
            case "Event State":  a = 2;
            break;
            case "Event Country":  a = 1;
            break;
            case "WorldWide":  a = 0;
            break;
            default: a = 47;
                break;
        }
        return a;
    }

    private void setUpMap() {
        Toast.makeText(this,"Drag Marker to Event Place or Touch & Hold Marker at event Place",Toast.LENGTH_SHORT).show();
        String address = street+","+city+","+state+","+country;
        Log.i("Radhe", "Radhe! Address is " + address);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        LatLng latLng = showAddressOnMap(address);
        setMarker(latLng,10);
    }

    private LatLng showAddressOnMap(String searchedLocation) {
        Toast.makeText(this,"Drag Marker to Event Place or Touch & Hold Marker at event Place",Toast.LENGTH_SHORT).show();

        LatLng latLng = new LatLng(0,0);
        List<Address> addressList = null;
        if(searchedLocation!=null || !searchedLocation.equals(""))
        {
            Log.i("Radhe", "Radhe! Address is " + searchedLocation);
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(searchedLocation, 1);
                Log.i("Radhe", "Radhe! No IOException. AddressList is "+ addressList);
            } catch (IOException e) {
                Log.i("Radhe", "Radhe! IOException is "+e);
                e.printStackTrace();
                return latLng;
            }

            if(addressList.size()>0 & addressList!=null) {
                Log.i("Radhe", "Radhe! AddressList.size is "+addressList.size());
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                locality = address.getLocality();
                Country = address.getCountryName();
                Log.i("Radhe", "Radhe! addressList "+addressList.size()+"  /******/  "+addressList.get(0));
            }
            else
            {
                Log.i("Radhe", "Radhe Place not found");
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please Enter a Place",Toast.LENGTH_SHORT).show();
        }
        return latLng;
    }

    public void clickSearchBtn(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String searchedLocation = mySearchText.getText().toString();
        LatLng latLng = showAddressOnMap(searchedLocation);
        setMarker(latLng, googleMap.getCameraPosition().zoom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }


    private void goToCurrentLocation() {

    }

    public void setMarker(LatLng latLng , float zoom) {
        if(latLng!=null) {

            if(marker!=null)  marker.remove();

            marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(locality).snippet(Country).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //Zoom in Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
