package com.ultimatesoftil.citron.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;

import java.util.ArrayList;

/**
 * Created by Mike on 21/08/2018.
 */

public class SmsListAdapter extends BaseAdapter {

    private Context context; //context
    private ArrayList<Client> items; //data source of the list adapter

    //public constructor
    public SmsListAdapter(Context context, ArrayList<Client> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.layout_list_view_row_items, parent, false);
        }

        // get current item to be displayed
       Client client = (Client) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.sms_text);
        CheckBox textViewItemDescription = (CheckBox)
                convertView.findViewById(R.id.sms_check);
        textViewItemDescription.setChecked(true);
        //sets the text for item name and item description from the current item object
        textViewItemName.setText(client.getName());


        // returns the view for the current row
        return convertView;
    }
}

