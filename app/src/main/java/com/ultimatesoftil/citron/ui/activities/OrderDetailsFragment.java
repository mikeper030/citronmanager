package com.ultimatesoftil.citron.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.NotificationListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

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
    private int counter=0;
    private double duea=0;
    private TextInputEditText due;
    private Spinner payments;
    private int paymentindex=0;
    private boolean add=true;
    private ImageButton img1,img2,img3;
    private Camera camera;
    private TextInputEditText sum;
    private TextView def1;
    View updateBtn=null;
    private int[]counters=new int[3];
    private View checkrow=null;
    private View sendrow;
    private CheckBox send;
    private View notes=null;
    private String[] checklinks=new String[3];
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private double total=0;
    private TextInputEditText cmt=null;
    private Spinner sp[]=null;

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
        def1=(TextView)view.findViewById(R.id.notification_def);
        /// /Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
                    //alocating arrays
                    pt = new EditText[j];
                    ot = new EditText[j];
                    dt=new EditText[j];
                    sp=new Spinner[j];

                    for (int a = 0; a < j; a++,k++)
                        addPriceField(parent,a, pt);

                    addStatusText(parent,k);
                    k++;
                    for (int i = 0; i < j; i++) {
                      //  addStatusField(parent, a + 1, ot);
                     if(order.getProducts().get(i).getDue()!=0)
                          showOwe(parent,k,dt,i);
                         else
                          addStatusField(parent,k,i);

                     k++;
                    }
                    addtotal(parent);
                    addDue(parent);
                    addPaymentmethod(parent);
                    //if(order.getPaymenttype()!=2)
                    addNotes(parent);
                   // addInsertButton(parent);
                }
            });

        }
        // setUpScrollView(quantity);
         quantity.setEnabled(false);


    }




//need to implement status and comment injection

    private void addtotal(LinearLayout parent) {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.total_field,null);
        sum=rowView.findViewById(R.id.order_total);

        if(order.getTotal()!=0)
            sum.setText(String.valueOf(order.getTotal()));
         setSaveListener(sum,parent);
        parent.addView(rowView,parent.getChildCount());

    }
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
        input.setHint(getResources().getString(R.string.price)+" "+(parentindex+1));
        setSumListener(input);
        setSaveListener(input,parent);
        parent.addView(rowView, parent.getChildCount());
    }
    public void showOwe(final LinearLayout parent, final int index, final EditText[] texts, final int i) {
        Log.d("replace to owe", String.valueOf(index));

        parent.removeViewAt(index);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.status_owe, null);
        final Spinner spinner = rowView.findViewById(R.id.status_sp2);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final TextInputEditText text = rowView.findViewById(R.id.status_o);
        texts[i] = text;


        spinner.setSelection(3);
        //click listener for owe spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String item = adapterView.getItemAtPosition(pos).toString();
                if (!item.equals(getResources().getString(R.string.owe2))) {
                    Log.d("replace to status", String.valueOf(index));
                    parent.removeViewAt(index);
                    final View rowView = inflater.inflate(R.layout.status_field, null);
                    Spinner spinner1 = rowView.findViewById(R.id.status_sp1);

                    spinner1.setAdapter(adapter);
                    spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            String item=adapterView.getItemAtPosition(pos).toString();
                            if (item.equals(getResources().getString(R.string.owe2))) {
                                Log.d("replace to owe", String.valueOf(index));
                                showOwe(parent, index, texts, i);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    if(order.getProducts().get(i).getDue()!=0){
                        text.setText(String.valueOf(order.getProducts().get(i).getDue()));
                    }
                    parent.addView(rowView,index);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        parent.addView(rowView,index);
    }
//    public void showOwe(final LinearLayout parent, final int parentindex, final EditText[] texts, final int txt_ind){
//        Log.d("index",String.valueOf(parentindex));
//        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View rowView = inflater.inflate(R.layout.status_owe, null);
//        final Spinner spinner=rowView.findViewById(R.id.status_sp2);
//        EditText text=rowView.findViewById(R.id.status_o);
//        texts[txt_ind]=text;
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setSelection(2);
//        if(order.getProducts().get(txt_ind).getDue()!=0){
//            text.setText(String.valueOf(order.getProducts().get(txt_ind).getDue()));
//        }
//        parent.addView(rowView,parentindex);
//
//    }
//    public void removeAllFields(LinearLayout parent) {
//        parent.removeAllViews();
//    }
    private void addStatusField(final LinearLayout parent, final int i, final int itemindex) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.status_field, null);
        final Spinner spinner=rowView.findViewById(R.id.status_sp1);
        dt=new EditText[Integer.parseInt(quantity.getText().toString())];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp[itemindex]=spinner;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(counters[0]>order.getQuantity()-1) {
                    addInsertBtn(parent);
                    Log.d("update","7");
                }
                counters[0]++;
            if(pos==3){
                int n = Integer.parseInt(quantity.getText().toString());
                Log.d("index",String.valueOf(i));
                showOwe(parent, i, dt, itemindex);

            }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setAdapter(adapter);
            spinner.setSelection(order.getProducts().get(itemindex).getStatus());


        parent.addView(rowView, i );
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
        notes=rowView;
       cmt=rowView.findViewById(R.id.notes_ord);

        cmt.setText(order.getComment()!=null?order.getComment():"");
        setSaveListener(cmt,parent);
        parent.addView(rowView, parent.getChildCount());
    }

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
               if(notificationProducs.size()==0){
                 def1.setVisibility(View.VISIBLE);
               }else
                   def1.setVisibility(View.INVISIBLE);
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

    public void saveOrder(final Order order){
        Query query=myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").orderByChild("time").equalTo(order.getTime());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               String ref=dataSnapshot.getKey();
                myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").child(ref).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                     getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
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
private void addPaymentmethod(final LinearLayout parent){
    final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    TextView textView= new TextView(getActivity());
    textView.setTextColor(getResources().getColor(R.color.white));
    textView.setText("אמצעי תשלום:");
    textView.setHeight(50);
    textView.setGravity(Gravity.RIGHT|Gravity.CENTER);
    final View rowView = inflater.inflate(R.layout.payment_method, null);
    final Spinner spinner=rowView.findViewById(R.id.payment_sp1);
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.payments));
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    payments=spinner;

        spinner.setSelection(order.getPaymenttype());

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
            int i=0;
            paymentindex=pos;
            Log.d("pos",String.valueOf(pos));
            if (pos==2&&add){
                add=false;
                if(updateBtn!=null){
                    parent.removeView(updateBtn);
                    parent.removeView(notes);
                    updateBtn=null;

                }
                addCheck(parent);
                addSend(parent);
                if(notes!=null){
                    parent.removeView(notes);
                    addNotes(parent);
                }
            }else {
                if(!add){
                    add=true;

                    removeCheck(parent,i);
                    removeSend(parent);
                }
            }

            if(counters[1]>0) {
                Log.d("update","9");
                addInsertBtn(parent);
            }
            counters[1]++;


        }
        private void removeSend(LinearLayout parent) {
            parent.removeView(sendrow);
        }

        private void addSend(LinearLayout parent) {
            View rowView=inflater.inflate(R.layout.send_check,null);
            sendrow=rowView;
            send=rowView.findViewById(R.id.send_checkbox);
            parent.addView(rowView,parent.getChildCount());
           // addNotes(parent);
        }

        private void removeCheck(LinearLayout parent, int i) {
            parent.removeView(checkrow);
        }
        private int addCheck(final LinearLayout parent) {

            Log.d("update","8");
            LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView=inflater.inflate(R.layout.check_image,null);
            checkrow=rowView;
            img1=rowView.findViewById(R.id.s1);
            img2=rowView.findViewById(R.id.s2);
            img3=rowView.findViewById(R.id.s3);
            if(order.getImg1()!=null){
                Glide.with(getActivity()).load(order.getImg1()).into(img1);
            }
            if(order.getImg2()!=null){
                Glide.with(getActivity()).load(order.getImg2()).into(img2);
            }
            if(order.getImg3()!=null){
                Glide.with(getActivity()).load(order.getImg3()).into(img3);
            }
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (img1.getDrawable().getConstantState() !=
                            ContextCompat.getDrawable(getContext(), R.drawable.add_image).getConstantState()){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("!")
                                .setMessage(getResources().getString(R.string.switche))
                                .setPositiveButton(getResources().getString(R.string.pht), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter=-1;
                                        takePicture();
                                        addInsertBtn(parent);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.watch), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        GalleryViewerFragment fragment = new GalleryViewerFragment();
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        Bundle bundle = new Bundle();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        Bitmap bitmap = ((BitmapDrawable)img1.getDrawable()).getBitmap();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        bundle.putByteArray("link",byteArray);
                                        bundle.putBoolean("bitmap",true);
                                        fragment.setArguments(bundle);
                                        fragmentTransaction.add(android.R.id.content, fragment,"gallery");
                                        fragmentTransaction.addToBackStack("gallery");
                                        fragmentTransaction.commit();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }else {
                        counter=-1;
                        takePicture();
                    }
                }
            });
            img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (img2.getDrawable().getConstantState() !=
                            ContextCompat.getDrawable(getContext(), R.drawable.add_image).getConstantState()){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("!")
                                .setMessage(getResources().getString(R.string.switche))
                                .setPositiveButton(getResources().getString(R.string.pht), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter=-2;
                                        takePicture();
                                        addInsertBtn(parent);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.watch), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        GalleryViewerFragment fragment = new GalleryViewerFragment();
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        Bundle bundle = new Bundle();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        Bitmap bitmap = ((BitmapDrawable)img2.getDrawable()).getBitmap();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        bundle.putByteArray("link",byteArray);
                                        bundle.putBoolean("bitmap",true);
                                        fragment.setArguments(bundle);
                                        fragmentTransaction.add(android.R.id.content, fragment,"gallery");
                                        fragmentTransaction.addToBackStack("gallery");
                                        fragmentTransaction.commit();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }else {
                        counter=-2;
                        takePicture();
                    }
                }
            });
            img3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (img3.getDrawable().getConstantState() !=
                            ContextCompat.getDrawable(getContext(), R.drawable.add_image).getConstantState()){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getActivity());
                        }
                        builder.setTitle("!")
                                .setMessage(getResources().getString(R.string.switche))
                                .setPositiveButton(getResources().getString(R.string.pht), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        counter=-3;
                                        takePicture();
                                        addInsertBtn(parent);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.watch), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        GalleryViewerFragment fragment = new GalleryViewerFragment();
                                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                        Bundle bundle = new Bundle();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        Bitmap bitmap = ((BitmapDrawable)img3.getDrawable()).getBitmap();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byte[] byteArray = stream.toByteArray();

                                        bundle.putByteArray("link",byteArray);
                                        bundle.putBoolean("bitmap",true);
                                        fragment.setArguments(bundle);
                                        fragmentTransaction.add(android.R.id.content, fragment,"gallery");
                                        fragmentTransaction.addToBackStack("gallery");
                                        fragmentTransaction.commit();

                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }else {
                        counter=-3;
                        takePicture();
                    }
                }
            });

            parent.addView(rowView,parent.getChildCount());
        return parent.getChildCount();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
    parent.addView(textView,parent.getChildCount());
    parent.addView(rowView,parent.getChildCount());


}
    private void addInsertBtn(LinearLayout parent) {
        if (updateBtn == null) {
            Button button = new Button(getActivity());
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackgroundColor(getResources().getColor(R.color.theme_primary_light));
            button.setText(getResources().getString(R.string.update));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //update the order


                    if(validateFields()){

                        Order order=getOrder();
                        saveOrder(order);
                    }else{
                        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "נא הזן מחיר!", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)v.getLayoutParams();
                        params.gravity = Gravity.TOP|Gravity.CENTER;
                        v.setLayoutParams(params);
                        snack.show();

                    }

                }
            });
            updateBtn = button;
            parent.addView(button, parent.getChildCount());
        }
    }
    private void addDue(LinearLayout parent){
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.due_field,null);
        due=rowView.findViewById(R.id.order_due);
        setSaveListener(due,parent);
        if(order.getDue()!=0){
            due.setText(String.valueOf(order.getDue()));
        }
        parent.addView(rowView,parent.getChildCount());
    }

    private void setSaveListener(TextInputEditText element, final LinearLayout parent) {
     element.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//             if(updateBtn!=null){
//                 parent.removeView(updateBtn);
//             }
             addInsertBtn(parent);
             Log.d("update","10");
         }

         @Override
         public void afterTextChanged(Editable editable) {

         }
     });
    }

    private void takePicture() {

        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("salesmanager/pictures")
                .setName("jfnjfn")
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);
        try {
            camera.takePicture();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private Order getOrder() {

        //long time=System.currentTimeMillis();
        int i=Integer.parseInt(quantity.getText().toString());
        order.setQuantity(Integer.parseInt(quantity.getText().toString()));

        if(!TextUtils.isEmpty(due.getText().toString())){
            order.setDue(Double.parseDouble(due.getText().toString())!=0?Double.parseDouble(due.getText().toString()):0);
        }



        order.setPaymenttype(paymentindex);
        if (paymentindex==2){
            for(int j=0;j<3;j++){
                if (checklinks[j]!=null){
                    if(j==0)
                        order.setImg1(checklinks[j]);
                    if(j==1)
                        order.setImg2(checklinks[j]);
                    if(j==2)
                        order.setImg3(checklinks[j]);
                }
            }
        }
        ArrayList<Product> products=new ArrayList<>();
        for(int j=0;j<i;j++){
            Product product= new Product();
            product.setKind(String.valueOf(spinner.getSelectedItemPosition()));
           // product.setNotification(notifications!=null&&notifications[j]!=0?notifications[j]:0);

            //product.setTime(System.currentTimeMillis());
            product.setPrice(pt[j]!=null?Double.parseDouble(pt[j].getText().toString()):null);
            try {
                product.setDue(dt[j]!=null?Double.parseDouble(dt[j].getText().toString()):null);
            }catch (Exception E){
                E.printStackTrace();
                product.setDue(0);

            }

            product.setStatus(sp[j].getSelectedItemPosition());
            //check if product image taken
            //if(notifications[j]!=0){
           //     setNotification(product,j);
           // }

            products.add(product);

        }

        if(!TextUtils.isEmpty(sum.getText().toString()))
        order.setTotal(Double.parseDouble(sum.getText().toString()));
        order.setUpdated(System.currentTimeMillis());
        order.setComment(cmt.getText().toString()==null?"":cmt.getText().toString());
       // order.setTime(time);
        //implement updated time here
        order.setProducts(products);
        return order;

    }
    private void uploadFile2(Uri filepath, final String pushedref, final String name, final int index){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                @SuppressWarnings("VisibleForTests") final String  downloadUrl =
                        taskSnapshot.getMetadata().getDownloadUrl().toString();
                myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").child(pushedref).child(name).setValue(downloadUrl);
                checklinks[index]=downloadUrl;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setSumListener(TextInputEditText input) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int j, int i1, int i2) {
                total=0;
                for(int i=0;i<pt.length;i++) {
                    if (pt[i] != null && !TextUtils.isEmpty(pt[i].getText()) ) {
                        total += Double.parseDouble(pt[i].getText().toString());
                    }
                }
                sum.setText(String.valueOf(total));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
