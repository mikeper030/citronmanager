package com.ultimatesoftil.citron.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.NotificationListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;

import java.util.ArrayList;

/**
 * Created by Mike Peretz on 07/08/2018.
 */

public class OrderDetailsFragment extends Fragment {
    private TextInputEditText name,mobile,phone,email,address,quantity;
    private Spinner spinner;
    private Client client;
    private Order order;
    private LinearLayout parent;
    EditText pt[]=null;
    EditText ot[]=null;
    EditText dt[]=null;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private ImageButton back;
    private DatabaseReference myRef;
    private Switch notificationsEnabled;
    private ListView notifications;
    private ArrayList<Product> notificationProducs=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_details,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = (TextInputEditText) view.findViewById(R.id.order_name);
        email = (TextInputEditText) view.findViewById(R.id.order_email);
        mobile = (TextInputEditText) view.findViewById(R.id.order_mobile);
        phone = (TextInputEditText) view.findViewById(R.id.order_phone);
        address = (TextInputEditText) view.findViewById(R.id.order_address);
        quantity = (TextInputEditText) view.findViewById(R.id.order_quantity);
        spinner = (Spinner) view.findViewById(R.id.order_spinner);
        parent = (LinearLayout) view.findViewById(R.id.fields2);
        back=(ImageButton)view.findViewById(R.id.fragment_orders_back);
        notifications=(ListView)view.findViewById(R.id.notif_list);
        notificationsEnabled=(Switch)view.findViewById(R.id.notif_switch);
        /// /Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        try {
            userID = user.getUid();
        } catch (Exception e) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
        }
        //===============================================================
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        final Bundle bundle = getArguments();
        if (bundle != null && bundle.getSerializable("client") != null) {
            client = (Client) bundle.getSerializable("client");
            name.setText(client.getName() != null ? client.getName() : "");
            email.setText(client.getEmail() != null ? client.getEmail() : "");
            mobile.setText(client.getPhone() != null ? client.getPhone() : "");
            phone.setText(client.getHomephone() != null ? client.getHomephone() : "");
            address.setText(client.getAddress() != null ? client.getAddress() : "");
            setUpNotificationsList();
            notificationsEnabled.setChecked(client.isNotifications_enabled());
            notificationsEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    client.setNotifications_enabled(b);
                    saveClient(client);
                }
            });

        }
        if (bundle != null && bundle.getSerializable("order") != null) {

            order = (Order) bundle.getSerializable("order");
            quantity.setText(String.valueOf(order.getQuantity()));
            for (int i = 0; i < 2; i++) {
                if(order.getProducts().get(0).getKind().equals(spinner.getItemAtPosition(i))){
                    spinner.setSelection(i);
               }
            }

            spinner.setEnabled(false);
            final int j = order.getQuantity();
            if (j > 100) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.max_100), Snackbar.LENGTH_SHORT).show();
                return;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   int k=0;
                    Log.d("creating views","fdf");
                    pt = new EditText[j];
                    ot = new EditText[j];
                    dt=new EditText[j];

                    for (int a = 0; a < j; a++,k++)
                        addPriceField(parent,a, pt);

                    addStatusText(parent,k);
                    k++;
                    for (int i = 0; i < j; i++) {
                      //  addStatusField(parent, a + 1, ot);
                     if(order.getProducts().get(i).getDue()!=0)
                          showOwe(parent,k,dt,i+1);
                         else
                          addStatusField(parent,k,i);

                     k++;
                    }
                    addNotes(parent);
                   // addInsertButton(parent);
                }
            });

        }
        // setUpScrollView(quantity);
         quantity.setEnabled(false);


    }




//need to implement status and comment injection


    public void addPriceField(LinearLayout parent, int parentindex, EditText[] editTexts) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.price_field, null);
        // Add the new row before the add field button.
         ImageButton image=rowView.findViewById(R.id.add_img_cr);
        final ProgressBar progressBar=rowView.findViewById(R.id.prg1);
         if(order.getProducts().get(parentindex).getPicLink()!=null){
            progressBar.setVisibility(View.VISIBLE);
             Glide.with(getActivity()).load(order.getProducts().get(parentindex).getPicLink()).placeholder(getResources().getDrawable(R.drawable.add_image)).listener(new RequestListener<String, GlideDrawable>() {
                 @Override
                 public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                     return false;
                 }

                 @Override
                 public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                     progressBar.setVisibility(View.INVISIBLE);
                     return false;
                 }
             }).into(image);
         }
        TextInputEditText input=rowView.findViewById(R.id.number_edit_text);
        editTexts[parentindex]=input;
        input.setText(String.valueOf(order.getProducts().get(parentindex).getPrice()));
        input.setHint(getResources().getString(R.string.price)+" "+parentindex+1);
        parent.addView(rowView, parent.getChildCount() );
    }
    public void showOwe(final LinearLayout parent, final int parentindex, final EditText[] texts, final int txt_ind){
        Log.d("index",String.valueOf(parentindex));


       // parent.removeViewAt(parentindex);

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.status_owe, null);
        final Spinner spinner=rowView.findViewById(R.id.status_sp2);
        EditText text=rowView.findViewById(R.id.status_o);
        texts[txt_ind-1]=text;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(2);
        if(order.getProducts().get(txt_ind-1).getDue()!=0){
            text.setText(String.valueOf(order.getProducts().get(txt_ind-1).getDue()));
        }
        parent.addView(rowView,parentindex);

    }
//    public void removeAllFields(LinearLayout parent) {
//        parent.removeAllViews();
//    }
    private void addStatusField(final LinearLayout parent, final int parentindex,int itemindex) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.status_field, null);
        final Spinner spinner=rowView.findViewById(R.id.status_sp1);
        dt=new EditText[Integer.parseInt(quantity.getText().toString())];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
            spinner.setSelection(order.getProducts().get(itemindex).getStatus());

//        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//
//            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                if(item.equals(getResources().getString(R.string.owe2))){
//                    int n=Integer.parseInt(quantity.getText().toString());
//                    EditText text=rowView.findViewById(R.id.status_o);
//                    editTexts[i-1]=text;
//
//                    showOwe(parent,i+n,dt,i);
//                }
//            }
//        });
        parent.addView(rowView, parentindex );
    }
    private void addStatusText(LinearLayout parent,int parentindex) {
        TextView tv = new TextView(getActivity());

        tv.setText(getResources().getString(R.string.order_status) );
        tv.setHeight(75);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.white));
        parent.addView(tv,parentindex);
    }
    private void addNotes(LinearLayout parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.notes_field, null);
       EditText comment=rowView.findViewById(R.id.notes_ord);
       comment.setText(order.getComment()!=null?order.getComment():"");
        parent.addView(rowView, parent.getChildCount() );
    }
//    private void addInsertButton(final LinearLayout parent) {
//        Button button= new Button(getActivity());
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(validateClientFields()){
//                    client= getClient();
//                    saveClient(client);
//                }else{
//                    Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.client_details_missing),Snackbar.LENGTH_SHORT).show();
//                    return;
//                }
//                if(validateFields()){
//
//                    Order order=getOrder();
//                    saveOrder(order);
//
//                }else{
//
//                }
//
//            }
//        });
//        button.setText(getResources().getString(R.string.insert));
//        parent.addView(button,parent.getChildCount());
//    }
    private boolean validateClientFields() {
        if((name.getText().toString().trim().length()==0||name.getText().toString().equals(getResources().getString(R.string.full_name)))||(mobile.getText().toString().trim().length()==0
                ||mobile.getText().toString().equals(getResources().getString(R.string.mobile)))){
            return false;
        }
        return true;
    }
    private boolean validateFields() {
        boolean val=true;
        if(TextUtils.isEmpty(quantity.getText().toString()))
            val=false;
        if(val)
            for(int i=0;i<Integer.parseInt(quantity.getText().toString());i++) {
                if (TextUtils.isEmpty(pt[i].getText().toString()))
                    val=false;
            }
        return val;
    }
//    private Order getOrder(){
//
//        int i=Integer.parseInt(quantity.getText().toString());
//        order.setQuantity(Integer.parseInt(quantity.getText().toString()));
//        ArrayList<Product> products=new ArrayList<>();
//        for(int j=0;j<i;j++){
//            Product product= new Product();
//            for (int a=0;a<i;a++){
//            product.setKind(spinner.getItems().get(spinner.getSelectedIndex()).toString());
//
//            product.setPrice(pt[j]!=null?pt[j].getText().toString():null);
//            try {
//                product.setDue(dt[j]!=null?Double.parseDouble(dt[j].getText().toString()):null);
//
//            }catch (Exception E){
//                E.printStackTrace();
//                product.setDue(0);
//
//            }
//            //product.setStatus();
//            products.add(product);
//
//        }
//        order.setTime(System.currentTimeMillis());
//        order.setProducts(products);
//
//        return order;
//    }
    private Client getClient() {

        String names = name.getText().toString() != null ? name.getText().toString() : "";
        String phon = mobile.getText().toString() != null ? mobile.getText().toString() : "";
        client.setName(names);
        client.setPhone(phon);
        client.setTime(System.currentTimeMillis());
        if(!TextUtils.isEmpty(address.getText().toString()))
            client.setAddress(address.getText().toString());
        if(!TextUtils.isEmpty(email.getText().toString()))
            client.setEmail(email.getText().toString());
        if(!TextUtils.isEmpty(phone.getText().toString()))
            client.setHomephone(phone.getText().toString());

        return client;


    }
    private void setUpNotificationsList() {
        final int[] count = {0};


        myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                   Order order = dataSnapshot1.getValue(Order.class);
                   orders.add(order);
                   count[0]++;
               }
               if(count[0]==dataSnapshot.getChildrenCount()) {
                   for (int i = 0; i < orders.size(); i++) {
                       for (int j = 0; j < orders.get(i).getProducts().size(); j++) {
                           if (orders.get(i).getProducts().get(j).getStatus() == 1) {
                               notificationProducs.add(orders.get(i).getProducts().get(j));
                           }
                       }
                       NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notificationProducs,orders,client,true);
                       notifications.setAdapter(adapter);

                   }
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void saveClient(Client client) {
        myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


    }

    public void saveOrder(Order order){
        myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").push().setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


    }
//    private void setUpScrollView(final TextInputEditText quantity) {
//        quantity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                if (quantity.getText().toString().equals("")) {
//                    removeAllFields(parent);
//                } else {
//                    removeAllFields(parent);
//                    String q = quantity.getText().toString();
//                    final int j = order.getQuantity();
//                    if(j>100){
//                        Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.max_100),Snackbar.LENGTH_SHORT).show();
//                        return;
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pt = new EditText[j];
//                            for (int a = 0; a < j; a++)
//                                addPriceField(parent, a + 1, pt);
//
//                            addStatusText(parent);
//
//                            ot =new EditText[j];
//                            for (int a = 0; a < j; a++)
//                                addStatusField(parent, a + 1, ot);
//
//                            addNotes(parent);
//                            addInsertButton(parent);
//                        }
//                    });
//
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }
}
