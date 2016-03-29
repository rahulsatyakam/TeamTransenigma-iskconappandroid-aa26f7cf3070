package com.transenigma.iskconapp;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class edit_event extends AppCompatActivity implements AlertDialogRadio.AlertPositiveListener {

    EditText myBlockAddress, myStreet, myCity, myState;
    String myEventObjectId = "" , blockAddress ="", street = "", city = "", state="",  country="", eventName="", myDescription="",
            FlagFromEvents="" , feedback="", selectedImagePath="", tags = "", finalAddress = "";
    String[] parts;
    ArrayList<String> tagsList = new ArrayList<String>();
    Date endDateTime, startDateTime;
    ParseFile imageFile;
    double latitude=0, longitude=0;
    Spinner autocomplete_country;
    Boolean validate= true;
    ProgressDialog progressDialog;
    Boolean changedAddress = false;

    @Bind(R.id.myEditUploadImage)ImageView myEditUploadImage;
    @Bind(R.id.myEditEventName)EditText myEditEventName;
    @Bind(R.id.myEditShowAddress)TextView myEditShowAddress;
    @Bind(R.id.myEditStartDateTime)EditText myEditStartDateTime;
    @Bind(R.id.myEditEndDateTime)EditText myEditEndDateTime;
    @Bind(R.id.myEditEventDescription)EditText myEditEventDescription;
    @Bind(R.id.myEditEventTarget)EditText myEditEventTarget;
    @Bind(R.id.myEditEventTags)EditText myEditEventTags;
    @Bind(R.id.myEditBtn)Button myEditBtn;
    @Bind(R.id.myContactName)EditText myContactName;
    @Bind(R.id.myContactEmail)EditText myContactEmail;
    @Bind(R.id.myContactNo)EditText myContactNo;

    private static final int PICKFILE_REQUEST_CODE = 1;
    Date startDate = new Date();
    Date endDate = new Date();
    int position, target;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        ButterKnife.bind(this);
        //myEditBtn.setEnabled(false);

        context = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                /** Instantiating the DialogFragment class */
                AlertDialogRadio alert = new AlertDialogRadio();
                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();
                /** Storing the selected item's index in the bundle object */
                b.putInt("position", position);
                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);
                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                alert.show(manager, "alert_dialog_radio");
            }
        };
        myEditEventTarget.setOnClickListener(listener);
        myEditEventDescription.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.myEditEventDescription) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            myEventObjectId = bundle.getString("myEventObjectId");
        }
        getEventDataFromParse(myEventObjectId);
    }

    private void getEventDataFromParse(String myEventObjectId) {
        final String myEventObjectId1 =  myEventObjectId;
        Log.i("Radhe", "inside  getEventDataFromParse "+ myEventObjectId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.getInBackground(myEventObjectId , new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    blockAddress = object.getString("blockAddress");
                    street = object.getString("street");
                    city = object.getString("city");
                    state = object.getString("state");
                    country = object.getString("country");
                    myDescription = object.getString("description");
                    eventName = object.getString("eventName");
                    startDateTime = object.getDate("startDateTime");
                    endDateTime = object.getDate("endDateTime");
                    latitude = object.getDouble("latitude");
                    longitude = object.getDouble("longitude");
                    imageFile = object.getParseFile("image");
                    tagsList = (ArrayList<String>) object.get("tags");
                    target = object.getInt("target");
                    myContactEmail.setText(object.getString("ContactEmail"));
                    myContactName.setText(object.getString("ContactName"));
                    myContactNo.setText(object.getString("ContactNo"));
                    setContent();
                } else {
                    Log.i("Radhe", "Could not fetch data from Parse of Id "+ myEventObjectId1 +" due to the exception "+e);
                }
            }
        });
    }

    private void setContent() {
        //myEventDetailImg.setImageResource(R.drawable.img);
        myEditEventName.setText(eventName);
        myEditShowAddress.setText(blockAddress+","+street+","+city+","+state+","+country);
        Log.i("Radhe","Event address is "+myEditShowAddress.getText().toString());
        myEditEventDescription.setText(myDescription);

        String[] parts = startDateTime.toString().split(" ");
        String showStartDate = parts[0]+" "+parts[1]+" "+parts[2]+",";
        String delegate = "hh:mm aaa";
        showStartDate = showStartDate +(String) android.text.format.DateFormat.format(delegate,startDateTime);
        showStartDate = showStartDate.replace(",", System.getProperty("line.separator"));
        myEditStartDateTime.setText(showStartDate);

        String[] endParts = endDateTime.toString().split(" ");
        String showEndDate = endParts[0]+" "+endParts[1]+" "+endParts[2]+",";
        String delegate2 = "hh:mm aaa";
        showEndDate = showEndDate +(String) android.text.format.DateFormat.format(delegate2,endDateTime);
        showEndDate = showEndDate.replace(",", System.getProperty("line.separator"));
        myEditEndDateTime.setText(showEndDate);

//        if(checkIfCurrentUserLiked())
//            myLikeBtn.setImageResource(R.drawable.fill_heart);
//        else myLikeBtn.setImageResource(R.drawable.empty_heart);

        if(tagsList!=null) {
            String gotTags = "";
            int i = 0;
            for (; i < tagsList.size() - 1; i++)
                gotTags = gotTags + tagsList.get(i) + " , ";
            gotTags = gotTags + tagsList.get(i);
            myEditEventTags.setText(gotTags);
        }
        switch(target){
            case 0:
                myEditEventTarget.setText("WorldWide");
                break;
            case 1:
                myEditEventTarget.setText("Event Country");
                break;
            case 2:
                myEditEventTarget.setText("Event State");
                break;
            case 3:
                myEditEventTarget.setText("Event City");
                break;
            default:
                break;
        }

        loadImages( imageFile , myEditUploadImage);
        String src = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=13&size=600x600&sensor=false";
        //Picasso.with(this).load(src).into(myEventMapImg);
    }

    private void loadImages(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                    } else {
                    }
                }
            });
        } else {
            img.setImageResource(R.drawable.mayapur);
        }
    }

    public void onPositiveClick(int position) {
        this.position = position;
        myEditEventTarget.setText(TargetOptions.code[position]);
    }

    public void clickStartDateTime(View v) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                //time = calendar.getTimeInMillis();
                startDate = calendar.getTime();
                startDateTime = startDate;
                Date today = new Date();
                if(today.compareTo(startDate)>=0){
                    validate = false;
                    Toast.makeText(edit_event.this,"enter a valid start date",Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] parts = startDate.toString().split(" ");
                String showStartDate = parts[0]+" "+parts[1]+" "+parts[2];
                String delegate = "hh:mm aaa";
                showStartDate = showStartDate +"\n"+(String) DateFormat.format(delegate,startDate);
                showStartDate = showStartDate.replace("\\\n", System.getProperty("line.separator"));
                myEditStartDateTime.setText(showStartDate);
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
//        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(startDateListener).setInitialDate(new Date()).build().show();
    }

    public void clickEndDateTime(View v) {
        final View dialogView = View.inflate(this, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                //time = calendar.getTimeInMillis();
                endDate = calendar.getTime();
                endDateTime = endDate;
                Date today = new Date();
                if(today.compareTo(endDate)>=0){
                    validate = false;
                    Toast.makeText(edit_event.this,"enter a valid end date",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(startDateTime.compareTo(endDateTime)>=0) {
                    validate = false;
                    Toast.makeText(edit_event.this,"End date must be greater than the start Date",Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] parts = endDate.toString().split(" ");
                String showEndDate = parts[0]+" "+parts[1]+" "+parts[2];
                String delegate = "hh:mm aaa";
                showEndDate = showEndDate + "\n"+(String) DateFormat.format(delegate,endDate);
                showEndDate = showEndDate.replace("\\\n", System.getProperty("line.separator"));
                myEditEndDateTime.setText(showEndDate);
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
//        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(endDateListener).setInitialDate(new Date()).build().show();
    }

    public void clickUploadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICKFILE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data!=null) {
            if (requestCode == PICKFILE_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                selectedImagePath = getPath(selectedImageUri);
                Log.i("Radhe","Uri is "+ selectedImageUri);
                myEditUploadImage.setImageURI(selectedImageUri);
            }
            Log.i("Radhe","Hari fetched image ");
        }
        else{
            Toast.makeText(this, "Unable to take image, select from another location", Toast.LENGTH_LONG).show();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void clickEventTag(View view){
        final CharSequence[] items = {" Kirtan "," Lecture "," Harinaam-Sankirtan "," Prashadam "," Presentation "};
// arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select The Tags")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int i=0;
                        for(; i<seletedItems.size()-1; i++)
                            tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                        tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                        myEditEventTags.setText(tags);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void clickShowAddress(View view){
        LayoutInflater factory = LayoutInflater.from(this);

//text_entry is an Layout XML file containing two text field to display in alert dialog
        final View textEntryView = factory.inflate(R.layout.address_popup, null);

        myBlockAddress = (EditText) textEntryView.findViewById(R.id.myBlockAddress);
        myStreet = (EditText) textEntryView.findViewById(R.id.myStreet);
        myCity = (EditText) textEntryView.findViewById(R.id.myCity);
        myState = (EditText) textEntryView.findViewById(R.id.myState);

        autocomplete_country = (Spinner) textEntryView.findViewById(R.id.autocomplete_country);
        Locale[] locale = Locale.getAvailableLocales();
        final ArrayList<String> countries = new ArrayList<String>();
        String myCountry;
        for( Locale loc : locale ){
            myCountry = loc.getDisplayCountry();
            if( myCountry.length() > 0 && !countries.contains(myCountry) ){
                countries.add( myCountry );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);
        countries.add("Country");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        autocomplete_country.setAdapter(adapter);
        autocomplete_country.setSelection(adapter.getCount() - 1);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(R.drawable.icon).setTitle("Enter the Event Address:").setView(textEntryView).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String address = "";
                        address = myBlockAddress.getText().toString()+","+myStreet.getText().toString()+","+myCity.getText().toString()
                                +","+myState.getText().toString()+","+autocomplete_country.getSelectedItem().toString();
                        finalAddress = myStreet.getText().toString()+","+myCity.getText().toString()
                                +","+myState.getText().toString()+","+autocomplete_country.getSelectedItem().toString();
                        if(address.equals(""))
                            myEditShowAddress.setText(R.string.Event_Address);
                        else myEditShowAddress.setText(address);
                        blockAddress = myBlockAddress.getText().toString();
                        street = myStreet.getText().toString();
                        city = myCity.getText().toString();
                        state = myState.getText().toString();
                        country = autocomplete_country.getSelectedItem().toString();
                        changedAddress = true;
                        myEditBtn.setEnabled(true);
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
     /*
     * User clicked cancel so do some stuff
     */
                    }
                });
        alert.show();
    }

    public void clickEdit(View view){
        if(!validate) return;
        myEditBtn.setEnabled(false);
        Toast.makeText(this,"Please Wait!", Toast.LENGTH_LONG).show();
        progressDialog = ProgressDialog.show( this, "Please Wait!", "Your Event is being added ,it may take few minutes", true);
        String address = street+","+city+","+state+","+country;
        saveLatLngToParse(showAddressOnMap(finalAddress));
    }

    private LatLng showAddressOnMap(String searchedLocation) {
        //Toast.makeText(this,"Drag Marker to Event Place or Touch & Hold Marker at event Place",Toast.LENGTH_SHORT).show();

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
//                locality = address.getLocality();
//                Country = address.getCountryName();
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

    private void saveLatLngToParse(final LatLng latLng) {
        if(!tags.equals("")) parts = (tags.replaceAll("\\s","")).split(",");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.getInBackground(myEventObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject testObject, ParseException e) {
                if (e == null) {
                    final ParseObject parseObject = testObject;
                    testObject.put("eventName", myEditEventName.getText().toString());
                    testObject.put("blockAddress", blockAddress);
                    testObject.put("street", street);
                    testObject.put("city", city);
                    testObject.put("state", state);
                    testObject.put("country", country);
                    testObject.put("description",myEditEventDescription.getText().toString());
                    testObject.put("target", getTargetValue(myEditEventTarget.getText().toString()));
                    testObject.put("startDateTime", startDateTime);
                    testObject.put("endDateTime", endDateTime);
                    testObject.put("ContactName", myContactName.getText().toString());
                    testObject.put("ContactNo", myContactNo.getText().toString());
                    testObject.put("ContactEmail", myContactEmail.getText().toString());
                    if(changedAddress) {
                        testObject.put("latitude", latLng.latitude);
                        testObject.put("longitude", latLng.longitude);
                    }
                    if(!tags.equals("")) testObject.put("tags", new ArrayList<String>(Arrays.asList(parts)));
                    if(!selectedImagePath.equals(""))  testObject.put("image",getFileFromUri());
                    testObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                progressDialog.dismiss();
                                myEventObjectId = parseObject.getObjectId();
                                Intent intent = new Intent(context, ShowEventToUser.class);
                                Toast.makeText(context, "Your event has been sent for verification", Toast.LENGTH_LONG).show();
                                intent.putExtra("myEventObjectId", myEventObjectId);
                                Log.i("Radhe", "inside clickConfirmLocation object id " + myEventObjectId);
                                startActivity(intent);
                                finish();
                            }else {
                                Log.i("Radhe", "Could not edit the event due to error : " + e.toString());
                                Toast.makeText(context, "Could not edit the event due to error : " + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    // something went wrong
                }
            }
        });

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

    private ParseFile getFileFromUri() {
        Uri myUri = Uri.parse("file://"+selectedImagePath);
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
}
