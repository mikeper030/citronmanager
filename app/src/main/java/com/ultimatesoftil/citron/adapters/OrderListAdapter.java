package com.ultimatesoftil.citron.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.ui.activities.OrderDetailsFragment;
import com.ultimatesoftil.citron.util.Utils;

import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;

/**
 * Created by Mike on 06/08/2018.
 */

public class OrderListAdapter extends BaseAdapter{
    private Context context; //context
    private ArrayList<Order> orders; //dat

    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private Client client;

    public OrderListAdapter(Context context, ArrayList<Order> items,Client client) {
        this.context = context;
        this.orders = items;
        this.client=client;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        try {
            userID = user.getUid();
        } catch (Exception e) {

        }
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
        final Activity activity= (Activity) convertView.getContext();
        final Order item = (Order) getItem(position);
        ((TextView) convertView.findViewById(R.id.textView5)).setText(Utils.FormatMillis(item.getTime()));
        if(item.getUpdated()!=0){
            TextView textView=(TextView)convertView.findViewById(R.id.edited);
            TextView date=(TextView)convertView.findViewById(R.id.edited_date);
            textView.setVisibility(View.VISIBLE);
            date.setText(String.valueOf(Utils.FormatMillis(item.getUpdated())));
            date.setVisibility(View.VISIBLE);


        }
        ((TextView) convertView.findViewById(R.id.textView6)).setText(String.valueOf(position+1));
        ((ImageButton)convertView.findViewById(R.id.orders_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(final MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.one1:
                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(context);
                                }
                                builder.setTitle(R.string.delete)
                                        .setMessage(R.string.sure_delete)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                deleteOrder(orders,position,activity);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                break;




                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        return convertView;
    }
   public void deleteOrder(ArrayList<Order> orders,int position, Activity activity){
        Order order=orders.get(position);
     Query query= myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").orderByChild("time").equalTo(order.getTime());
     query.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(DataSnapshot dataSnapshot, String s) {
             Log.d("deleted","order");
             myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").child(dataSnapshot.getKey()).setValue(null);
         }

         @Override
         public void onChildChanged(DataSnapshot dataSnapshot, String s) {

         }

         @Override
         public void onChildRemoved(DataSnapshot dataSnapshot) {

         }

         @Override
         public void onChildMoved(DataSnapshot dataSnapshot, String s) {

         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
   }
}
