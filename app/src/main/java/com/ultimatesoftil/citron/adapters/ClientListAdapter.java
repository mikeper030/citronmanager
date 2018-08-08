package com.ultimatesoftil.citron.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.ui.activities.MainActivity;

/**
 * Created by Mike on 01/08/2018.
 */

public class ClientListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Client> items; //dat
    public ClientListAdapter(Context context, ArrayList<Client> items) {
        this.context = context;
        this.items = items;
    }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_client, container, false);
            }

            final Client item = (Client) getItem(position);
            ((TextView) convertView.findViewById(R.id.list_item_name)).setText(item.getName());
            ((TextView) convertView.findViewById(R.id.list_item_phone)).setText(context.getResources().getString(R.string.mobile)+":"+" "+item.getPhone());
            ((TextView) convertView.findViewById(R.id.list_item_added)).setText(item.getDateTimeFormatted());
            ((ImageButton)convertView.findViewById(R.id.client_menu)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });
//            final ImageView img = (ImageView) convertView.findViewById(R.id.thumbnail);
//            Glide.with(getActivity()).load(item.photoId).asBitmap().fitCenter().into(new BitmapImageViewTarget(img) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    img.setImageDrawable(circularBitmapDrawable);
//                }
//            });

            return convertView;
        }

}
