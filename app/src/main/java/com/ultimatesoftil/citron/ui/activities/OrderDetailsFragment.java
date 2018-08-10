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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;

import java.util.ArrayList;

/**
 * Created by Mike Peretz on 07/08/2018.
 */

public class OrderDetailsFragment extends Fragment {
    private TextInputEditText name,mobile,phone,email,address,quantity;
    private MaterialSpinner spinner;
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
        spinner = (MaterialSpinner) view.findViewById(R.id.order_spinner);
        spinner.setItems(getResources().getStringArray(R.array.products));
        parent = (LinearLayout) view.findViewById(R.id.fields2);
        back=(ImageButton)view.findViewById(R.id.fragment_orders_back);
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
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getSerializable("client") != null) {
            client = (Client) bundle.getSerializable("client");
            name.setText(client.getName() != null ? client.getName() : "");
            email.setText(client.getEmail() != null ? client.getEmail() : "");
            mobile.setText(client.getPhone() != null ? client.getPhone() : "");
            phone.setText(client.getHomephone() != null ? client.getHomephone() : "");
            address.setText(client.getAddress() != null ? client.getAddress() : "");

        }
        if (bundle != null && bundle.getSerializable("order") != null) {

            order = (Order) bundle.getSerializable("order");
            quantity.setText(String.valueOf(order.getQuantity()));
            for (int i = 0; i < spinner.getItems().size(); i++) {
                if (spinner.getItems().get(i).toString().equals(order.getProducts().get(0).getKind())) {
                    spinner.setSelectedIndex(i);
                    break;
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

                    addStatusText(parent);

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
        TextInputEditText input=rowView.findViewById(R.id.number_edit_text);
        editTexts[parentindex]=input;
        input.setText(order.getProducts().get(parentindex).getPrice());
        input.setHint(getResources().getString(R.string.price)+" "+parentindex+1);
        parent.addView(rowView, parent.getChildCount() );
    }
    public void showOwe(final LinearLayout parent, final int parentindex, final EditText[] texts, final int txt_ind){
        Log.d("index",String.valueOf(parentindex));


       // parent.removeViewAt(parentindex);

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.status_owe, null);
        final MaterialSpinner spinner=rowView.findViewById(R.id.status_sp2);
        EditText text=rowView.findViewById(R.id.status_o);
        texts[txt_ind-1]=text;
        spinner.setItems(getResources().getStringArray(R.array.status));
        spinner.setSelectedIndex(2);
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
        final MaterialSpinner spinner=rowView.findViewById(R.id.status_sp1);
        dt=new EditText[Integer.parseInt(quantity.getText().toString())];
        spinner.setItems(getResources().getStringArray(R.array.status));
        spinner.setTag("spinner"+parentindex);
        for(int i=0;i<spinner.getItems().size();i++) {
            if (order.getProducts().get(itemindex).getStatus().equals(spinner.getItems().get(0)))
                spinner.setSelectedIndex(0);
            if (order.getProducts().get(itemindex).getStatus().equals(spinner.getItems().get(1)))
                spinner.setSelectedIndex(1);
            if (order.getProducts().get(itemindex).getStatus().equals(spinner.getItems().get(2)))
                spinner.setSelectedIndex(2);
        }
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
    private void addStatusText(LinearLayout parent) {
        TextView tv = new TextView(getActivity());
        tv.setText(getResources().getString(R.string.order_status) );
        parent.addView(tv,parent.getChildCount());
    }
    private void addNotes(LinearLayout parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.notes_field, null);

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
    private Order getOrder(){

        int i=Integer.parseInt(quantity.getText().toString());
        order.setQuantity(Integer.parseInt(quantity.getText().toString()));
        ArrayList<Product> products=new ArrayList<>();
        for(int j=0;j<i;j++){
            Product product= new Product();
            product.setKind(spinner.getItems().get(spinner.getSelectedIndex()).toString());
            product.setPrice(pt[j]!=null?pt[j].getText().toString():null);
            try {
                product.setDue(dt[j]!=null?Double.parseDouble(dt[j].getText().toString()):null);

            }catch (Exception E){
                E.printStackTrace();
                product.setDue(0);

            }
            product.setStatus(ot[j]!=null?ot[j].getText().toString():null);
            products.add(product);

        }
        order.setTime(System.currentTimeMillis());
        order.setProducts(products);

        return order;
    }
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
    private void saveClient(Client client) {
        myRef.child("users").child(userID).child("clients").child(client.getName()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
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
