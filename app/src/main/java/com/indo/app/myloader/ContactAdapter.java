package com.indo.app.myloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by indo on 08/01/18.
 */

public class ContactAdapter extends CursorAdapter {


    public ContactAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_row,parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor != null){
            TextView tvName = (TextView) view.findViewById(R.id.tv_item_name);
            CircleImageView imgUser = (CircleImageView) view.findViewById(R.id.img_item_user);

            RelativeLayout rlItem = (RelativeLayout) view.findViewById(R.id.rl_tem);
            imgUser.setImageResource(R.drawable.ic_launcher_background);
            tvName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI )) != null){
                imgUser.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))));
            }else {
                imgUser.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
