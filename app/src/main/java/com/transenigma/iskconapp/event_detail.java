package com.transenigma.iskconapp;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.parse.ParseUser;
import com.transenigma.iskconapp.AlertDialogRadio.AlertPositiveListener;

import android.content.Intent;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.Bind;

import butterknife.ButterKnife;

public class event_detail extends AppCompatActivity implements AlertPositiveListener{

    EditText myBlockAddress, myStreet, myCity, myState;
    Spinner autocomplete_country;
    Date startDate = new Date();
    Date endDate = new Date();
    private String selectedImagePath;
    private static final int PICKFILE_REQUEST_CODE =1;
    Boolean filledAddress = false;
    Boolean validate = true;
    static String[] gender = new String[]{"Male", "Female" };
    static String[] ashram = new String[]{"Brahmachari", "Grihastha", "Vanaprashtha", "Sannyasa"};
    static String[] ageTarget = new String[]{"Below 20 years", "Between 20 to 35 years", "Between 35 to 50 years", "Above 50"};
    @Bind(R.id.myEventName)EditText myEventName;
//    @Bind(R.id.myBlockAddress)EditText myBlockAddress;
//    @Bind(R.id.myStreet)EditText myStreet;
//    @Bind(R.id.myCity)EditText myCity;
//    @Bind(R.id.myState)EditText myState;
//    @Bind(R.id.autocomplete_country)Spinner autocomplete_country;
    @Bind(R.id.myStartDateTime)EditText myStartDateTime;
    @Bind(R.id.myEndDateTime)EditText myEndDateTime;
    @Bind(R.id.myEventTags)EditText myEventTags;
    @Bind(R.id.myEventTarget)EditText myEventTarget;
    @Bind(R.id.myShowAddress)TextView myShowAddress;
    @Bind(R.id.myLoginBtn)Button myLoginBtn;
    @Bind(R.id.myEventDescription)EditText myEventDescription;
    @Bind(R.id.myUploadImage)ImageView myUploadImage;
    @Bind(R.id.myContactName)EditText myContactName;
    @Bind(R.id.myContactEmail)EditText myContactEmail;
    @Bind(R.id.myContactNo)EditText myContactNo;
    @Bind(R.id.myEventTargetAge)EditText myEventTargetAge;
    @Bind(R.id.myEventTargetGender)EditText myEventTargetGender;
    @Bind(R.id.myEventTargetAshram)EditText myEventTargetAshram;

    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        myLoginBtn.setEnabled(false);

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
        myEventTarget.setOnClickListener(listener);

        /****************************Set scrollable *********************************************************************/
        myEventDescription.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.myEventDescription) {
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
        myContactName.setText(ParseUser.getCurrentUser().getString("legalname")+ " ");
        if(!ParseUser.getCurrentUser().getString("surname").equals(null))
            myContactName.setText(ParseUser.getCurrentUser().getString("legalname")+ " "+ ParseUser.getCurrentUser().getString("surname"));
        myContactEmail.setText(ParseUser.getCurrentUser().getEmail());
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.i("Radhe", "Phone no is -: "+mPhoneNumber);
        myContactNo.setText(mPhoneNumber);
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
                Date today = new Date();
                if(today.compareTo(startDate)>=0){
                    validate = false;
                    Toast.makeText(event_detail.this,"enter a valid start date",Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] parts = startDate.toString().split(" ");
                String showStartDate = parts[0]+" "+parts[1]+" "+parts[2];
                String delegate = "hh:mm aaa";
                showStartDate = showStartDate +"\n"+(String) DateFormat.format(delegate,startDate);
                showStartDate = showStartDate.replace("\\\n", System.getProperty("line.separator"));
                myStartDateTime.setText(showStartDate);
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
//        new SlideDateTimePicker.Build er(getSupportFragmentManager()).setListener(startDateListener).setInitialDate(new Date()).build().show();
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
                Date today = new Date();
                if(today.compareTo(endDate)>=0){
                    validate = false;
                    Toast.makeText(event_detail.this,"enter a valid end date",Toast.LENGTH_SHORT).show();
                    return;
                }else if(startDate.compareTo(endDate)>=0) {
                    validate = false;
                    Toast.makeText(event_detail.this,"End date must be greater than the start Date",Toast.LENGTH_SHORT).show();
                    //myEndDateTime.setError("End date must be greater than the start Date");
                    return;
                }

                String[] parts = endDate.toString().split(" ");
                String showEndDate = parts[0]+" "+parts[1]+" "+parts[2];
                String delegate = "hh:mm aaa";
                showEndDate = showEndDate + "\n"+(String) DateFormat.format(delegate,endDate);
                showEndDate = showEndDate.replace("\\\n", System.getProperty("line.separator"));
                myEndDateTime.setText(showEndDate);
                alertDialog.dismiss();
            }});
        alertDialog.setView(dialogView);
        alertDialog.show();
//        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(endDateListener).setInitialDate(new Date()).build().show();
    }

    @Override
    public void onPositiveClick(int position) {
        this.position = position;
        myEventTarget.setText(TargetOptions.code[position]);
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
                myUploadImage.setImageURI(selectedImageUri);
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
                            //Log.i("Radhe","clicked "+indexSelected);
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tags="";
                        int i=0;
                        for(; i<seletedItems.size()-1; i++)
                            tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                        tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                        myEventTags.setText(tags);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void clickTargetAge(View view){
        final CharSequence[] items = {" Below 20 years ", " Between 20 to 35 years ", " Between 35 to 50 years ", " Above 50 "};
// arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select the age group")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            //Log.i("Radhe","clicked "+indexSelected);
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tags="";
                        int i=0;
                        for(; i<seletedItems.size()-1; i++)
                            tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                        tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                        myEventTargetAge.setText(tags);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void clickTargetGender(View view){
        final CharSequence[] items = {" Male ", " Female " };
// arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select The Tags")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            //Log.i("Radhe","clicked "+indexSelected);
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tags="";
                        int i=0;
                        for(; i<seletedItems.size()-1; i++)
                            tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                        tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                        myEventTargetGender.setText(tags);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void clickTargetAshram(View view){
        final CharSequence[] items ={" Brahmachari ", " Grihastha ", " Vanaprashtha ", " Sannyasa "};
// arraylist to keep the selected items
        final ArrayList seletedItems=new ArrayList();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select The Tags")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            //Log.i("Radhe","clicked "+indexSelected);
                            // If the user checked the item, add it to the selected items
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String tags="";
                        int i=0;
                        for(; i<seletedItems.size()-1; i++)
                            tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                        tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                        myEventTargetAshram.setText(tags);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void clickNext(View view){
        if(!validate) {
            Toast.makeText(event_detail.this,"Check the end and start dates", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), add_event.class);
        intent.putExtra("eventName", myEventName.getText().toString());
        intent.putExtra("blockAddress", myBlockAddress.getText().toString());
        intent.putExtra("street", myStreet.getText().toString());
        intent.putExtra("city", myCity.getText().toString());
        intent.putExtra("state", myState.getText().toString());
        intent.putExtra("country", autocomplete_country.getSelectedItem().toString());
        intent.putExtra("description", myEventDescription.getText().toString());
        intent.putExtra("uri", selectedImagePath);
        intent.putExtra("startDateTime", startDate);
        intent.putExtra("endDateTime", endDate);
        intent.putExtra("targetAudience", myEventTarget.getText().toString());
        intent.putExtra("targetAge", myEventTargetAge.getText().toString());
        intent.putExtra("targetAshram", myEventTargetAshram.getText().toString());
        intent.putExtra("targetGender", myEventTargetGender.getText().toString());
        intent.putExtra("tags", myEventTags.getText().toString());
        intent.putExtra("ContactName", myContactName.getText().toString());
        intent.putExtra("ContactNo", myContactNo.getText().toString());
        intent.putExtra("ContactEmail", myContactEmail.getText().toString());

        startActivity(intent);
        finish();
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
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
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
                        if(address.equals(""))
                            myShowAddress.setText(R.string.Event_Address);
                        else myShowAddress.setText(address);
                        myLoginBtn.setEnabled(true);
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

}
