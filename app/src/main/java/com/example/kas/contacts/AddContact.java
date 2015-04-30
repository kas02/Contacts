package com.example.kas.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddContact extends Activity {

    private LocationManager locationManager;
    String latitude;
    String longitude;
    public final static String TAG = "MyLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_layout);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    } //end of onCreate()

    public void onClickAddButton(View view) {

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editPhone = (EditText) findViewById(R.id.editPhone);

        final String LOCATION_MIME_TYPE = "vnd.android.cursor.item/com.example.kas.contacts.location";

        if (editName.getText().toString().equals("") || editPhone.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), R.string.fill_fields, Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<ContentProviderOperation> cpo = new ArrayList<>();
            cpo.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_TYPE, null)
                    .withValue(RawContacts.ACCOUNT_NAME, null)
                    .build());

            cpo.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(StructuredName.DISPLAY_NAME, editName.getText().toString())
                    .build());

            cpo.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, editPhone.getText().toString())
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build());

            if ((latitude != null && !latitude.equals("")) && (longitude != null && !longitude.equals(""))) {
                String isLocation = "1";
                cpo.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, LOCATION_MIME_TYPE)
                        .withValue(Phone.NUMBER, isLocation)
                        .withValue(Phone.TYPE, latitude)
                        .withValue(Phone.LABEL, longitude)
                        .build());
            } else {
                String isLocation = "0";
                cpo.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, LOCATION_MIME_TYPE)
                        .withValue(Phone.NUMBER, isLocation)
                        .build());
            }
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, cpo);
                Toast.makeText(getApplicationContext(), R.string.contact_saved, Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.can_not_save_contact, Toast.LENGTH_SHORT).show();
                Log.e (TAG, "Exception: " + e.getMessage());
            }
        }
    } //end of onClickAddButton()

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 5, 10, locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            getLatLon(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            getLatLon(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    private void getLatLon(Location location) {
        latitude =  String.format("%1$.6f", location.getLatitude());
        longitude = String.format("%1$.6f", location.getLongitude());
     }
} //end of class AddContact
