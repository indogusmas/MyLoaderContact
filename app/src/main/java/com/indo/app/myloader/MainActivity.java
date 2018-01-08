package com.indo.app.myloader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    public static final String TAG = "ContactApp";
    private ListView lvContact;
    private ProgressBar progressBar;
    private ContactAdapter adapter;
    private final int CONTACT_LOAD_ID=110;
    private final int CONTACT_PHONE_ID=120;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvContact = (ListView)findViewById(R.id.lv_contact);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        lvContact.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        adapter = new ContactAdapter(MainActivity.this, null, true);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(CONTACT_PHONE_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader mCursorLoader = null;
        if (id == CONTACT_LOAD_ID){
            String[] projectionFields = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI
            };
            mCursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.Contacts.CONTENT_URI,
                    projectionFields,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+"ASC");
        }
        if (id == CONTACT_PHONE_ID){
            String[] phoneProjectFields = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            mCursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneProjectFields,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.TYPE + " = "+
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1",
                    new String[]{args.getString("id")},
                    null);
        }
        return  mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"loaderFinished");
        if (loader.getId() == CONTACT_LOAD_ID){
            if (data.getCount() > 0){
                lvContact.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter.swapCursor(data);
            }
        }
        if (loader.getId() == CONTACT_PHONE_ID){
            String contactNumber = null;
            if (data.moveToFirst()){
                contactNumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            Intent intent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel"+contactNumber));
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CONTACT_LOAD_ID){
            lvContact.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            adapter.swapCursor(null);
            Log.d(TAG, "LoaderReset");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(CONTACT_LOAD_ID);
        getSupportLoaderManager().destroyLoader(CONTACT_PHONE_ID);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = adapter.getCursor();
        //move to the selected contact
        cursor.moveToPosition(position);
        //Get the ID_value
        long mContactId = cursor.getLong(0);
        Log.d(TAG, "posisition"+position+ mContactId);
        getPhoneNumber(String.valueOf(mContactId));

    }

    private void getPhoneNumber(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("id", s);
        getSupportLoaderManager().restartLoader(CONTACT_PHONE_ID, bundle, this);
    }
}
