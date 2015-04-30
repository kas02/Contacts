package com.example.kas.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    public final static String TAG = "MyLogs";

    private static final String MIME_NAME = "vnd.android.cursor.item/name";
    private static final String MIME_PHONE = "vnd.android.cursor.item/phone_v2";
    private static final String MIME_EMAIL = "vnd.android.cursor.item/email_v2";
    private static final String MIME_LOCATION = "vnd.android.cursor.item/com.example.kas.contacts.location";
    private static ArrayList<String[]> allData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
    }

    @Override
    protected void onResume() {
       super.onResume();
       showList();
    }

    private void showList(){
        ListView contactsList = (ListView) findViewById(R.id.contactsView);
        ContactAdapter adapter = new ContactAdapter(this, initData());
        contactsList.setAdapter(adapter);

        contactsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialog(id);
                //Log.i (TAG, position + "  " + id);
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(contactsList);
    } //end of showList()

    private List<Contact> initData() {
        List<Contact> list = new ArrayList<>();
        int id;
        String[] data = new String[5];


        Uri uri = Data.CONTENT_URI;

        String[] rejection = {
                Data.MIMETYPE,
                Data.CONTACT_ID,
                Data.DATA1};

        String selection =
                Data.MIMETYPE + "='" + MIME_NAME + "' OR " +
                Data.MIMETYPE + "='" + MIME_PHONE + "' OR " +
                Data.MIMETYPE + "='" + MIME_EMAIL + "' OR " +
                Data.MIMETYPE + "='" + MIME_LOCATION + "'";

        Cursor c = getContentResolver().query(uri, rejection, selection, null, Data.CONTACT_ID);
        c.moveToFirst();
        try {
            id = 0;
            do {
                if (id != c.getInt(c.getColumnIndex(Data.CONTACT_ID))) {
                    allData.add(new String[]{data[0], data[1], data[2], data[3], data[4]});
                    id = c.getInt(c.getColumnIndex(Data.CONTACT_ID));
                    data[0] = c.getString(c.getColumnIndex(Data.CONTACT_ID));
                    data[1] = null;
                    data[2] = null;
                    data[3] = null;
                    data[4] = null;
                }
                switch (c.getString(c.getColumnIndex(Data.MIMETYPE))) {
                    case MIME_NAME:
                        data[1] = c.getString(c.getColumnIndex(Data.DATA1));
                        break;
                    case MIME_PHONE:
                        data[2] = c.getString(c.getColumnIndex(Data.DATA1));
                        break;
                    case MIME_EMAIL:
                        data[3] = c.getString(c.getColumnIndex(Data.DATA1));
                        break;
                    case MIME_LOCATION:
                        data[4] = c.getString(c.getColumnIndex(Data.DATA1));
                        break;
                }

            } while (c.moveToNext());
            allData.add(new String[]{data[0], data[1], data[2], data[3],data[4]});
        } catch (IndexOutOfBoundsException e){
           Log.e(TAG, "IndexOutOfBoundsException");
        }

        c.close();

        for (int i = 1; i < allData.size(); i++){
            list.add(new Contact(
                    Integer.parseInt(allData.get(i)[0]),
                    (allData.get(i)[1] == null ? allData.get(i)[3] : allData.get(i)[1]),
                    (allData.get(i)[2] == null ? "" : allData.get(i)[2]),
                    (allData.get(i)[4] != null)));
        }
        return list;
    } // end of initData()


    public void addContact(View view) {
        Intent intent = new Intent(MainActivity.this, AddContact.class);
        startActivity(intent);
        onPause();
    }


    public void openMap (View view) {
        long id = (Long) view.getTag();
        String idStr = Long.toString(id);
        String lat;
        String lon;

        String[] rejection = {
                Data.MIMETYPE,
                Data.CONTACT_ID,
                Data.DATA1,
                Data.DATA2,
                Data.DATA3};

        String selection = Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + MIME_LOCATION + "'";
        String[] selectionArg = {idStr};
        Cursor c = getContentResolver().query(Data.CONTENT_URI, rejection, selection, selectionArg, null, null);

        try{
            c.moveToFirst();
            if (c.getInt(c.getColumnIndex(Data.DATA1)) == 1) {
                lat = c.getString(c.getColumnIndex(Data.DATA2)).replace(',', '.');
                lon = c.getString(c.getColumnIndex(Data.DATA3)).replace(',', '.');

                String geoURI = String.format("geo:0,0?q=%s,%s", lat, lon);
                Uri geo = Uri.parse(geoURI);
                Intent geoMap = new Intent(Intent.ACTION_VIEW, geo);
                startActivity(geoMap);
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_location, Toast.LENGTH_SHORT).show();
            }
        } catch (IndexOutOfBoundsException e){
                Toast.makeText(getApplicationContext(), R.string.no_location, Toast.LENGTH_SHORT).show();
        }

        c.close();
    } //end of openMap()

    private void showDialog(final long id){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder
                .setTitle(R.string.warning)
                .setIcon(R.drawable.ic_action_warning)
                .setMessage(R.string.delete_question)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContact(id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    } //end of showDialog()

    private void deleteContact(final long id) {

        Uri uri = Contacts.CONTENT_URI;
        String[] rejection = {Contacts._ID, Contacts.NAME_RAW_CONTACT_ID};
        String selection = Contacts._ID + "=?";
        String[] selectionArg = {String.valueOf(id)};
        Cursor c = getContentResolver().query(uri, rejection, selection, selectionArg, null);
        String RAW_CONTACT_ID;

        try {
            c.moveToFirst();
            RAW_CONTACT_ID = c.getString(c.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));

            ArrayList<ContentProviderOperation> ops = new ArrayList<>();

            ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                    .withSelection(RawContacts.CONTACT_ID + "=?", new String[]{String.valueOf(id)})
                    .build());

            ops.add(ContentProviderOperation.newDelete(Data.CONTENT_URI)
                    .withSelection(Data.RAW_CONTACT_ID + "=?", new String[]{RAW_CONTACT_ID})
                    .build());

            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                onResume();
                //Log.i(TAG, "Contact deleted");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        } catch (IndexOutOfBoundsException e){
            Log.e(TAG, "IndexOutOfBoundsException");
        }
        c.close();
    } //end of deleteContact()
} //end of class MainActivity
