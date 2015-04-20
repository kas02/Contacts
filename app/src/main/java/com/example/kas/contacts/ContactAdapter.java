package com.example.kas.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends BaseAdapter{
    private List<Contact> list;
    private LayoutInflater layoutInflater;

    public ContactAdapter(Context context, List<Contact> list) {
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        Contact contact = getContact(position);
        return contact.getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.contact_layout, parent, false);
        }

        Contact contact = getContact(position);

        TextView textName = (TextView) view.findViewById(R.id.name);
        TextView textPhone = (TextView) view.findViewById(R.id.phone);
        ImageView locationIcon = (ImageView) view.findViewById(R.id.locationIcon);

        textName.setText(contact.getName());
        textPhone.setText(contact.getPhone());

        if (contact.isLocation()) {
            locationIcon.setImageResource(R.drawable.ic_is_location);
        } else {
            locationIcon.setImageResource(R.drawable.ic_no_location);
        }

        locationIcon.setTag(getItemId(position));

        return view;
    }

    private Contact getContact(int position) {
        return (Contact) getItem(position);
    }
 }
