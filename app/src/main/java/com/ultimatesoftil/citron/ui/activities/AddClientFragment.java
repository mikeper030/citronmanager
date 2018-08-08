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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.OrderListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.FButton;
import com.ultimatesoftil.citron.models.Product;

import java.util.ArrayList;

/**
 * Created by Mike on 02/08/2018.
 */

public class AddClientFragment extends Fragment {

    private MaterialSpinner spinner;
    private LinearLayout parent;
    private TextInputEditText quantity,cname,phone,email,address,mobile;
    private String name=null;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private DatabaseReference myRef;
    private FButton saveOrder;
    private ImageButton back;
    private ListView orderDetails;
    EditText pt[]=null;
    EditText ot[]=null;
    EditText dt[]=null;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (MaterialSpinner)view. findViewById(R.id.spinner);
        spinner.setItems(getResources().getStringArray(R.array.products));
        parent=(LinearLayout)view.findViewById(R.id.fields1);
        quantity=(TextInputEditText)view.findViewById(R.id.add_quantity);
        email=(TextInputEditText)view.findViewById(R.id.add_email);
        address=(TextInputEditText)view.findViewById(R.id.order_address);
        cname=(TextInputEditText)view.findViewById(R.id.order_name);
        phone=(TextInputEditText)view.findViewById(R.id.order_phone);
        mobile=(TextInputEditText)view.findViewById(R.id.order_mobile);
        saveOrder=(FButton)view.findViewById(R.id.FButton);
        back=(ImageButton)view.findViewById(R.id.add_back);
        orderDetails=(ListView)view.findViewById(R.id.add_sum_list);
        /// /Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        try {
            userID = user.getUid();
        } catch (Exception e) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.no_connection),Snackbar.LENGTH_SHORT).show();
        }
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                name=item;
            }
        });
           quantity.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               if (quantity.getText().toString().equals("")) {
                   removeAllFields(parent);
               } else {
                   removeAllFields(parent);
                   String q = quantity.getText().toString();
                   int j = Integer.parseInt(q);
                    pt = new EditText[j];
                   for (int a = 0; a < j; a++)
                       addPriceField(parent, a + 1, pt);

                   addStatusText(parent);

                    ot =new EditText[j];
                   for (int a = 0; a < j; a++)
                       addStatusField(parent, a + 1, ot);

                   addNotes(parent);
                   addInsertButton(parent);
               }
           }
           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
     saveOrder.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
               if(validateClientFields()){
                 Client client= getClient();
                 saveClient(client);
                 if(validateFields()){
                     Order order=getOrder();
                     saveOrder(order);
                     getActivity().getSupportFragmentManager().popBackStack();
                 }else {
                     getActivity().getSupportFragmentManager().popBackStack();
                 }
             }else{
                 Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.client_details_missing),Snackbar.LENGTH_SHORT).show();
                 return;
             }
         }
     });
    back.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    });
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_client,container,false);

    }
    public void addPriceField(LinearLayout parent,int i,EditText[] editTexts) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.price_field, null);
        // Add the new row before the add field button.
        TextInputEditText input=rowView.findViewById(R.id.number_edit_text);
        editTexts[i-1]=input;
        input.setHint(getResources().getString(R.string.price)+" "+i);
        parent.addView(rowView, parent.getChildCount() );
    }
    public void showOwe(final LinearLayout parent, final int index, final EditText[] texts, final int i){
        Log.d("index",String.valueOf(index));
        parent.removeViewAt(index);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.status_owe, null);
        final MaterialSpinner spinner=rowView.findViewById(R.id.status_sp2);
        EditText text=rowView.findViewById(R.id.status_o);
        texts[i-1]=text;
        spinner.setItems(getResources().getStringArray(R.array.status));
        spinner.setSelectedIndex(2);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
             if(!item.equals(getResources().getString(R.string.owe2))){
                 parent.removeViewAt(index);
                 final View rowView = inflater.inflate(R.layout.status_field, null);
                 MaterialSpinner spinner1=rowView.findViewById(R.id.status_sp1);
                 spinner1.setItems(getResources().getStringArray(R.array.status));
                 spinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                     @Override
                     public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                         if (item.equals(getResources().getString(R.string.owe2))) {
                             Log.d("add", "owe");
                             int n = Integer.parseInt(quantity.getText().toString());
                             showOwe(parent, index,texts,i);
                         }
                     }
                 });
                 parent.addView(rowView,index);

             }
            }
        });
        parent.addView(rowView,index);

    }
    public void removeAllFields(LinearLayout parent) {
        parent.removeAllViews();
    }
    private void addStatusField(final LinearLayout parent, final int i, final EditText[] editTexts) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.status_field, null);
        final MaterialSpinner spinner=rowView.findViewById(R.id.status_sp1);
        dt=new EditText[Integer.parseInt(quantity.getText().toString())];
        spinner.setItems(getResources().getStringArray(R.array.status));
        spinner.setTag("spinner"+i);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                if(item.equals(getResources().getString(R.string.owe2))){
                 int n=Integer.parseInt(quantity.getText().toString());
                 EditText text=rowView.findViewById(R.id.status_o);
                 editTexts[i-1]=text;

                    showOwe(parent,i+n,dt,i);
             }
            }
    });
        parent.addView(rowView, parent.getChildCount() );
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
    private void addInsertButton(final LinearLayout parent) {
        Button button= new Button(getActivity());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateClientFields()){
                    Client client= getClient();
                    saveClient(client);
                }else{
                    Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.client_details_missing),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(validateFields()){

                    Order order=getOrder();
                    saveOrder(order);
                    setUpOrderList();
                }else{

                }

            }
        });
        button.setText(getResources().getString(R.string.insert));
        parent.addView(button,parent.getChildCount());
    }

    private void setUpOrderList() {
        final ArrayList<Order> orders=new ArrayList<>();
        myRef.child("users").child(userID).child("clients").child(cname.getText().toString()).child("orders").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        orders.add(dataSnapshot.getValue(Order.class));
            OrderListAdapter adapter= new OrderListAdapter(getActivity(),orders);
            orderDetails.setAdapter(adapter);
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

    private void saveClient(Client client) {
    myRef.child("users").child(userID).child("clients").child(client.getName()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

        }
    });


    }

    private boolean validateClientFields() {
            if((cname.getText().toString().trim().length()==0||cname.getText().toString().equals(getResources().getString(R.string.full_name)))||(mobile.getText().toString().trim().length()==0
            ||mobile.getText().toString().equals(getResources().getString(R.string.mobile)))){
        return false;
    }
    return true;
    }

        public void saveOrder(Order order){
        myRef.child("users").child(userID).child("clients").child(cname.getText().toString()).child("orders").push().setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });


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
      Order order= new Order();
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

            String name = cname.getText().toString() != null ? cname.getText().toString() : "";
            String phon = mobile.getText().toString() != null ? mobile.getText().toString() : "";

            Client client = new Client();
            client.setName(name);
            client.setPhone(phon);
            client.setTime(System.currentTimeMillis());
            return client;


  }
}
