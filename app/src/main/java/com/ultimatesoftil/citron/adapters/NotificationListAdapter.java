package com.ultimatesoftil.citron.adapters;

import android.app.AlarmManager;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.util.Utils;

import java.util.ArrayList;

/**
 * Created by Mike on 12/08/2018.
 */

public class NotificationListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Product> items; //dat
    private ArrayList<Order> orders;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private Client client;
    private boolean is_white;
    public NotificationListAdapter(Context context, ArrayList<Product> items,ArrayList<Order>orders,Client client,boolean is_white) {
        this.context = context;
        this.items = items;
        this.orders=orders;
        this.client=client;
        this.is_white=is_white;
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
           if(is_white)
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item, container, false);
            else
               convertView = LayoutInflater.from(context).inflate(R.layout.notification_item2, container, false);
        }
        String [] products=context.getResources().getStringArray(R.array.products);
        final Product item = (Product) getItem(position);
        ((TextView) convertView.findViewById(R.id.notif_item_product)).setText(products[Integer.parseInt(item.getKind())]);
        for(int i=0;i<orders.size();i++){
            Order order=orders.get(i);
            for(int j=0;j<order.getProducts().size();j++){
                if(item.getTime()==order.getProducts().get(j).getTime()){
                    ((TextView) convertView.findViewById(R.id.notif_item_date)).setText(Utils.FormatMillis(orders.get(i).getTime()));

                }
            }
        }
        if(item.getNotification()!=0){
            ((TextView) convertView.findViewById(R.id.textView12)).setVisibility(View.VISIBLE);

            ((TextView) convertView.findViewById(R.id.notification_item_expire)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.notification_item_expire)).setText(Utils.FormatMillis(item.getNotification()));

        }
//        ((TextView) convertView.findViewById(R.id.list_item_added)).setText(item.getDateTimeFormatted());
        Switch switche=(Switch) convertView.findViewById(R.id.switch1);
        switche.setChecked(item.isNotification_checked());
        switche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(!b){
                   AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                   if(item.getIntent()!=null){
                       alarmManager.cancel(item.getIntent());
                   }
               }
               item.setNotification_checked(b);
               saveUpdate(item);
            }
        });


        return convertView;
    }

    private void saveUpdate(Product item) {
      for(int i=0;i<orders.size();i++){
          Order order=orders.get(i);
          for(int j=0;j<order.getProducts().size();j++){
              if(item.getTime()==order.getProducts().get(j).getTime()){
                 order.getProducts().set(j,item);
                 saveOrder(order,item.getTime());
              }
          }
      }
    }

    private void saveOrder(final Order order,long time) {
        Query query=myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").orderByChild("time").equalTo(order.getTime());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("snapshot",dataSnapshot.getKey());
              String ref=  dataSnapshot.getKey();
              myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").child(ref).setValue(order);
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
