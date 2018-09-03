package com.ultimatesoftil.citron.ui.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.FButton;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.ui.base.SmsBatch;
import com.ultimatesoftil.citron.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericOrdersActivity extends AppCompatActivity {
    private TableLayout t1;
    int sum1=0,sum2=0,sum3=0,sum4=0,sum5=0;
    private ArrayList<Client> clients=new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private HashMap<String,Order> orderHashMap=new HashMap<>();
    private int counter=0;
    private ProgressDialog progressDialog;
    private FButton sendLate,sendDue;
    private TableRow  tr_bottom;
    private TableRow tr_head;
    private ArrayList<Client> lateClients=new ArrayList<>();
    private ArrayList<Client> dueClients=new ArrayList<>();
    int c_purchased = 0, c_interested = 0, l_interested = 0, l_purchased = 0,c_late=0;
    double due=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Utils.isTabletDevice(this)){
            setContentView(R.layout.gen_order);
        }else {
            setContentView(R.layout.gen_order_small);

        }
        sendDue=(FButton)findViewById(R.id.owes_sms);
        sendLate =(FButton)findViewById(R.id.late_sms);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_primary_light));
        }
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        userID=user.getUid();

        getData();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog= new ProgressDialog(GenericOrdersActivity.this);
                progressDialog.setMessage("טוען מידע אנא המתן...");
                progressDialog.show();
            }
        });

    }

    private void setClientsData() {

        @SuppressLint("HandlerLeak") final Handler handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what != 0) {
                    switch (msg.what) {
                        case 1:
                            t1.addView(tr_head);
                            break;
                        case 2:
                            break;
                        case 3:
                           t1.addView(tr_bottom);
                            progressDialog.dismiss();
                            if(!Utils.isTabletDevice(GenericOrdersActivity.this))
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            break;
                        case 4:
                           sendLate.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                if(lateClients.size()>0){
                                    Bundle bundle=new Bundle();
                                    bundle.putString("clients","clients");
                                    bundle.putSerializable("clients",lateClients);
                                    SmsBatch frag=new SmsBatch();
                                    frag.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag).addToBackStack(null).commit();
                                }
                                else{
                                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "לא נמצאו לקוחות מאחרים", Snackbar.LENGTH_LONG);
                                    View v = snack.getView();
                                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                                    params.gravity = Gravity.TOP | Gravity.CENTER;
                                    v.setLayoutParams(params);
                                    snack.show();
                                }

                               }
                           });
                           sendDue.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   if(dueClients.size()>0){
                                       Bundle bundle=new Bundle();
                                       bundle.putSerializable("owe",dueClients);
                                       SmsBatch frag=new SmsBatch();
                                       frag.setArguments(bundle);
                                       getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag).addToBackStack(null).commit();
                                   }else{
                                       Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "לא נמצאו לקוחות חייבים", Snackbar.LENGTH_LONG);
                                       View v = snack.getView();
                                       FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                                       params.gravity = Gravity.TOP | Gravity.CENTER;
                                       v.setLayoutParams(params);
                                       snack.show();
                                   }
                               }
                           });
                            break;

                    }

                } else {
                    switch (msg.arg1) {

                        case 4:
                            TableRow tableRow=(TableRow)msg.obj;
                            t1.addView(tableRow);
                            break;

                    }
                }
            }
        };
        Log.d("size of clients",String.valueOf(clients.size()));
        t1 = (TableLayout) findViewById(R.id.main_table);
        tr_head = new TableRow(this);
        //  tr_head.setId(10);
        tr_head.setBackgroundColor(getResources().getColor(R.color.material_gray1));
        tr_head.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                105));
        final TextView t11 = createHeaderTextView("שם הלקוח");
        TextView t12 = createHeaderTextView("אתרוגים בהתעניינות");
        t12.setPadding(50,0,0,0);
        TextView t13 = createHeaderTextView("לולבים בהזמנה");
        t13.setPadding(50,0,0,0);
        TextView t14 = createHeaderTextView("אתרוגים נרכשו");
        TextView t15 = createHeaderTextView("לולבים נרכשו");
        TextView t16=createHeaderTextView("אתרוגים באיחור");
        TextView t17=createHeaderTextView("חוב");
        t17.setPadding(80,0,50,0);

        tr_head.addView(t11, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f)));
        tr_head.addView(t12, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f)));
        tr_head.addView(t13, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f)));
        tr_head.addView(t14, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.58f)));
        tr_head.addView(t15, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.5f)));
         tr_head.addView(t16,new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,0.5f));
         tr_head.addView(t17,new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,0.5f));
        t1.addView(tr_head);



        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();




                        int counter=0;
                        for (int i = 0; i < clients.size(); i++) {
                            Log.d("orders",String.valueOf(clients.get(i).getOrders().size()));

                            if (clients.get(i).getOrders() != null) {
                                for (int j = 0; j < clients.get(i).getOrders().size(); j++) {
                                   if(clients.get(i).getOrders().get(j).getProducts()!=null){
                                    for (int k = 0; k < clients.get(i).getOrders().get(j).getProducts().size(); k++) {
                                        Product product = clients.get(i).getOrders().get(j).getProducts().get(k);
                                        //check if client has due
                                        if (product.getKind().equals("0") && product.getStatus() == 3 && product.getDue() != 0) {
                                            //add to due counter
                                            boolean add = true;
                                            due += product.getDue();
                                            for (int a = 0; a < dueClients.size(); a++) {
                                                if (clients.get(i).getName().equals(dueClients.get(a).getName())) {
                                                    add = false;
                                                }
                                            }
                                            if (add) {
                                                dueClients.add(clients.get(i));
                                                sum5 += 1;
                                            }
                                        } else if (((product.getKind().equals("1") && (product.getStatus() == 3) || (product.getKind().equals("1") && product.getStatus() == 4))) && product.getDue() != 0) {
                                            due += product.getDue();
                                            boolean add = true;
                                            for (int a = 0; a < dueClients.size(); a++) {
                                                if (clients.get(i).getName().equals(dueClients.get(a).getName())) {
                                                    add = false;
                                                }
                                            }
                                            if (add) {
                                                lateClients.add(clients.get(i));
                                                sum5 += 1;
                                            }
                                        }

                                        if (product.getKind().equals("0") && product.getStatus() == 1 && product.getNotification() < System.currentTimeMillis()) {
                                            //product is citron and client is runing late -- did not come back from authority
                                            boolean add = true;
                                            for (int a = 0; a < lateClients.size(); a++) {
                                                if (clients.get(i).getName().equals(lateClients.get(a).getName())) {
                                                    add = false;
                                                }
                                            }
                                            if (add) {
                                                lateClients.add(clients.get(i));
                                                sum5 += 1;
                                            }

                                            c_late += 1;

                                        }
//                                        if(product.getKind().equals("0")&&product.getStatus()==3){
//
//                                            boolean add=true;
//                                            for(int a=0;a<lateClients.size();a++){
//                                                if(clients.get(i).getName().equals(lateClients.get(a).getName())){
//                                                    add=false;
//                                                }
//                                            }
//                                            if (add)
//                                                lateClients.add(clients.get(i));
//
//                                            c_late+=1;
//                                            sum5+=1;
//                                        }
                                        else
                                            //regular citron purchase
                                            if (product.getKind().equals("0") && product.getStatus() == 2 || product.getKind().equals("0") && product.getStatus() == 3) {
                                                c_purchased += 1;
                                                sum1 += 1;
                                            } else {
                                                //order still in  process
                                                if (product.getKind().equals("0") && product.getStatus() == 1) {
                                                    c_interested += 1;
                                                    sum2 += 1;
                                                }
                                            }
                                        //lulav regular purchase
                                        if (product.getKind().equals("1") && product.getStatus() == 1 || product.getKind().equals("1") && product.getStatus() == 3) {
                                            l_purchased += 1;
                                            sum3 += 1;
                                        } else {
                                            //
                                            if ((product.getKind().equals("1") && product.getStatus() == 2) || (product.getKind().equals("1") && product.getStatus() == 4)) {
                                                l_interested += 1;
                                                sum4 += 1;
                                            }


                                        }
                                    }
                                    }
                                }

                            }

                           TableRow content = new TableRow(getBaseContext());
                            //  tr_head.setId(10);
                            if(counter%2!=0) content.setBackgroundColor(Color.GRAY);else
                                content.setBackgroundColor(Color.WHITE);


                            content.setLayoutParams(new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    85));
                            TextView t111 = createRegTextView(clients.get(i).getName());
                            t111.setPadding(0,0,0,0);
                            TextView t112 = createRegTextView(String.valueOf(c_interested));
                            t112.setPadding(150,0,0,0);
                            TextView t113 = createRegTextView(String.valueOf(l_interested));
                            t113.setPadding(150,0,0,0);
                            TextView t114 = createRegTextView(String.valueOf(c_purchased));
                            t114.setPadding(150,0,0,0);
                            TextView t115 = createRegTextView(String.valueOf(l_purchased));
                            t115.setPadding(130,0,0,0);
                            TextView t116=createRegTextView(String.valueOf(c_late));
                            t116.setPadding(100,0,0,0);
                            TextView t117=createRegTextView(String.valueOf(due));
                            t117.setPadding(130,0,0,0);
                            if(counter%2==0){
                                t111.setTextColor(Color.GRAY);
                                t112.setTextColor(Color.GRAY);
                                t113.setTextColor(Color.GRAY);
                                t114.setTextColor(Color.GRAY);
                                t115.setTextColor(Color.GRAY);
                                t116.setTextColor(Color.GRAY);
                                t117.setTextColor(Color.GRAY);
                            }

                            content.addView(t111, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                            content.addView(t112, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                            content.addView(t113, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                            content.addView(t114, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                            content.addView(t115, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                            content.addView(t116,(new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,0.8f)));
                           content.addView(t117,new TableRow.LayoutParams(35,TableRow.LayoutParams.WRAP_CONTENT,0.8f));

                            Message qemsg =handler.obtainMessage();
                            qemsg.obj=content;
                            qemsg.arg1 = 4;
                            handler.sendMessage(qemsg);


                            l_interested= 0;
                            c_interested=0;
                            l_purchased=0;
                            c_purchased=0;
                            c_late=0;
                            due=0;
                            counter++;
                        }
                        tr_bottom = new TableRow(getBaseContext());
                        //  tr_head.setId(10);

                        tr_bottom.setBackgroundColor(getResources().getColor(R.color.material_gray1));
                        tr_bottom.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                105));
                        TextView t = createHeaderTextView("סה\"כ");

                        TextView r1=createHeaderTextView(String.valueOf(sum2));
                        TextView r2=createHeaderTextView(String.valueOf(sum4));
                        TextView r3=createHeaderTextView(String.valueOf(sum1));
                        TextView r4=createHeaderTextView(String.valueOf(sum3));
                        TextView r5=createHeaderTextView(String.valueOf(sum5));
                        TextView r6=createHeaderTextView(String.valueOf(due));
                        t.setPadding(0,0,0,0);
                        r1.setPadding(155,0,20,0);
                        r2.setPadding(170,0,0,0);
                        r3.setPadding(170,0,0,0);
                        r4.setPadding(160,0,0,0);
                        r5.setPadding(125,0,0,0);
                        r6.setPadding(120,0,0,0);

                        tr_bottom.addView(t, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r1,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r2, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r3,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r4, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r5, (new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        tr_bottom.addView(r6,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.8f)));
                        handler.sendEmptyMessage(3);
                        handler.sendEmptyMessage(4);

                    }


        }).start();

    }

    private TextView createRegTextView(String text){
        TextView textView= new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(16);
        textView.setText(text);
        return textView;

    }

    private TextView createHeaderTextView(String text){
        TextView textView= new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.white));

        textView.setTextSize(20);
        textView.setText(text);
        return textView;

    }
    private void getData() {


        myRef.child("users").child(userID).child("clients").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.child("orders").getChildren()){
                    orderHashMap.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Order.class));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(int i=0;i<clients.size();i++){
                    if(clients.get(i).getName().equals(dataSnapshot.child("details").child("name").getValue(String.class))){
                        clients.remove(i);


                    }
                }if(clients.size()==0){
                    Log.d("EMPTY","list");


                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUpdates(DataSnapshot dataSnapshot) {
        try {
            orderHashMap.clear();
            for(DataSnapshot dataSnapshot1:dataSnapshot.child("orders").getChildren()){
               orderHashMap.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Order.class));
            }

            Client client=dataSnapshot.child("details").getValue(Client.class);
            for (int i=0;i<clients.size();i++){
                if(clients.get(i).getName().equals(client.getName()))
                    return;

            }
            ArrayList<Order>orders= new ArrayList<>();


            for (Map.Entry<String, Order> entry : orderHashMap.entrySet()) {
                orders.add(entry.getValue());
                // ...
            }
            client.setOrders(orders);
            clients.add(client);
             counter++;




             if(counter==dataSnapshot.getChildrenCount()){
                setClientsData();

            }
        }catch (Exception e){

        }
    }
}
