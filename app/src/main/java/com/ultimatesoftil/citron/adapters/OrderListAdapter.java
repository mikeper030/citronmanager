package com.ultimatesoftil.citron.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.ui.activities.AddClientFragment;
import com.ultimatesoftil.citron.ui.activities.OrderDetailsFragment;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;

/**
 * Created by Mike on 06/08/2018.
 */

public class OrderListAdapter extends BaseAdapter{
    private Context context; //context
    private ArrayList<Order> orders; //dat
    private Client client;
    public OrderListAdapter(Context context, ArrayList<Order> items,Client client) {
        this.context = context;
        this.orders = items;
        this.client=client;
    }
    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            final int[] counter = {1};

            convertView = LayoutInflater.from(context).inflate(R.layout.order_item1, container, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageButton cart=view.findViewById(R.id.order_add);
                    LayoutParams params = view.getLayoutParams();
                    final int closed= (int) context.getResources().getDimension(R.dimen.card_height_closed);
                    final int open= (int)context.getResources().getDimension(R.dimen.card_height_open);

                   if(counter[0] %2==0) {
                     cart.setVisibility(View.INVISIBLE);
                     params.height = closed;
                     view.setLayoutParams(params);

                 }

                    else  {
                       //item expaned state
                        params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,open);
                        view.setLayoutParams(params);
                        cart.setVisibility(View.VISIBLE);
                        cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                             //open order details
                                Order order=orders.get(position);
                                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("order",order);
                                bundle.putSerializable("client",client);
                                OrderDetailsFragment fragobj = new OrderDetailsFragment();
                                fragobj.setArguments(bundle);
                                activity.getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
                            }
                        });

                    }
                counter[0]++;
                    Log.d("counter",String.valueOf(counter[0]));
                }
            });
        }

        final Order item = (Order) getItem(position);
        ((TextView) convertView.findViewById(R.id.textView5)).setText(item.getDateTimeFormatted());
        ((TextView) convertView.findViewById(R.id.textView6)).setText(String.valueOf(position));

        return convertView;
    }

}
