package com.ultimatesoftil.citron.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.datetimepicker.time.RadialPickerLayout;

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

import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;

import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.NotificationListAdapter;
import com.ultimatesoftil.citron.adapters.OrderListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.FButton;
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.util.Utils;
import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Mike on 02/08/2018.
 */

public class AddClientOrderFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,View.OnClickListener {

    private Spinner spinner;
    private LinearLayout parent;
    private TextInputEditText quantity,cname,phone,email,address,mobile;
    private String name=null;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private Camera camera;
    private String userID;
    private DatabaseReference myRef;
    private FButton saveOrder;
    private ImageButton back;
    private ListView orderDetails;
    private TextInputEditText sum;
    private Client client;
    private EditText pt[]=null;
    private EditText ot[]=null;
    private EditText dt[]=null;
    private Spinner sp[]=null;
    private String[]links=null;
    private ImageButton[]addimgs=null;
    private EditText cmt=null;
    private Calendar calendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private double total=0;
    private int counter=0;
    private Product[]temp;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private String DownloadUrl;
    private ListView notificationlist;
    private String pushedref;
    private static final String TIME_PATTERN = "HH:mm";
    private ListView notifications;
    private ArrayList<Product> notificationProducs=new ArrayList<>();
    private ArrayList<Order> orders=new ArrayList<>();
    private TextView date;


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = (Spinner)view. findViewById(R.id.order_spinner);

        parent=(LinearLayout)view.findViewById(R.id.fields1);
        quantity=(TextInputEditText)view.findViewById(R.id.order_quantity);
        email=(TextInputEditText)view.findViewById(R.id.add_email);
        address=(TextInputEditText)view.findViewById(R.id.order_address);
        cname=(TextInputEditText)view.findViewById(R.id.order_name);
        phone=(TextInputEditText)view.findViewById(R.id.order_phone);
        mobile=(TextInputEditText)view.findViewById(R.id.order_mobile);
        saveOrder=(FButton)view.findViewById(R.id.FButton);
        back=(ImageButton)view.findViewById(R.id.add_back);

        progressBar=(ProgressBar) view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        notifications=(ListView)view.findViewById(R.id.notif_list_create);

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
        }
        calendar = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.products));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               String item=adapterView.getItemAtPosition(i).toString();
                name=item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
                   final int j = Integer.parseInt(q);
                   if(j>100){
                       Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.max_100),Snackbar.LENGTH_SHORT).show();
                       return;
                   }
                      getActivity().runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              addimgs=new ImageButton[j];
                              links=new String[j];
                              temp=new Product[j];
                              sp=new Spinner[j];
                              pt = new EditText[j];
                              for (int a = 0; a < j; a++)
                                  addPriceField(parent, a + 1, pt);

                              addStatusText(parent);

                              ot =new EditText[j];
                              for (int a = 0; a < j; a++)
                                  addStatusField(parent, a + 1, ot);

                              addtotal(parent);
                              addNotes(parent);
                              addInsertButton(parent);
                          }
                      });

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
                   client= getClient();
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
    Bundle bundle=getArguments();
    if(bundle!=null&&bundle.getSerializable("client")!=null){
        Client client=(Client) bundle.getSerializable("client");
        cname.setText(client.getName()!=null?client.getName():"");
        phone.setText(client.getHomephone()!=null?client.getHomephone():"");
        mobile.setText(client.getPhone()!=null?client.getPhone():"");
        address.setText(client.getAddress()!=null?client.getAddress():"");
        email.setText(client.getEmail()!=null?client.getEmail():"");
        setUpOrderList();

    }
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_client,container,false);

    }
    public void addPriceField(final LinearLayout parent, final int i, EditText[] editTexts) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.price_field, null);
        // Add the new row before the add field button.
        TextInputEditText input=rowView.findViewById(R.id.number_edit_text);
        editTexts[i-1]=input;
        input.setHint(getResources().getString(R.string.price)+" "+i);
        setSumListener(input);


        final ImageButton picture=rowView.findViewById(R.id.add_img_cr);
        addimgs[i-1]=picture;
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (picture.getDrawable().getConstantState() !=
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
                                    counter=i-1;
                                    takePicture();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.watch), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    GalleryViewerFragment fragment = new GalleryViewerFragment();
                                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    Bundle bundle = new Bundle();
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    temp[i-1].getRawImage().compress(Bitmap.CompressFormat.PNG, 100, stream);
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
                    counter = i - 1;
                    takePicture();
                }
            }
        });

        parent.addView(rowView, parent.getChildCount() );
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
        EditText text = rowView.findViewById(R.id.status_o);
        texts[i - 1] = text;
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

                 parent.addView(rowView,index);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        parent.addView(rowView,index);
    }

            public void removeAllFields(LinearLayout parent) {
        parent.removeAllViews();
    }


    private void addStatusField (final LinearLayout parent, final int i, final EditText[] editTexts) {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LayoutInflater inflater1 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.status_field, null);
        final Spinner spinner=rowView.findViewById(R.id.status_sp1);
        dt=new EditText[Integer.parseInt(quantity.getText().toString())];
        sp[i-1]=spinner;

        spinner.setTag("spinner"+i);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), R.layout.simple_spinner_my, getResources().getStringArray(R.array.status));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                if (selectedItem.equals(getResources().getString(R.string.owe2))) {
                    int n = Integer.parseInt(quantity.getText().toString());
                    EditText text = rowView.findViewById(R.id.status_o);
                    editTexts[i - 1] = text;

                    showOwe(parent, i + n, dt, i);

                } else if (selectedItem.equals("נשלח לבדיקה")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("בחר זמן להתראה");

                    View view1=inflater1.inflate(R.layout.notification_dailog,null);
                    // this is set the view from XML inside AlertDialog
                    alert.setView(view1);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    date=(TextView)view1.findViewById(R.id.date_display);
                    Button select = (Button) view1.findViewById(R.id.select_custom);
                    select.setOnClickListener(AddClientOrderFragment.this);
                    Spinner pre=(Spinner)view1.findViewById(R.id.select_def);



                    pre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    alert.setCancelable(false);

                    alert.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alert.setPositiveButton("שמור", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           if(System.currentTimeMillis()-calendar.getTimeInMillis()>0){
                               
                           }
                        }
                    });
                    AlertDialog dialog = alert.create();

                    dialog.show();



                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        parent.addView(rowView, parent.getChildCount() );
    }
    private void addStatusText(LinearLayout parent) {
        TextView tv = new TextView(getActivity());
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setText(getResources().getString(R.string.order_status) );
        parent.addView(tv,parent.getChildCount());
    }
    private void addNotes(LinearLayout parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.notes_field, null);
        cmt =rowView.findViewById(R.id.notes_ord);
        parent.addView(rowView, parent.getChildCount() );
    }
    private void addtotal(LinearLayout parent) {
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView=inflater.inflate(R.layout.total_field,null);
         sum=rowView.findViewById(R.id.order_total);
         parent.addView(rowView,parent.getChildCount());

    }

    private void addInsertButton(final LinearLayout parent) {
        Button button= new Button(getActivity());
        button.setTextColor(getResources().getColor(R.color.white));
        button.setBackgroundColor(getResources().getColor(R.color.theme_primary_light));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateClientFields()){
                     client= getClient();
                    saveClient(client);
                }else{
                    Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.client_details_missing),Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(validateFields()){

                    Order order=getOrder();
                   saveOrder(order);
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
            Order order=dataSnapshot.getValue(Order.class);

            orders.add(order);
            OrderListAdapter adapter= new OrderListAdapter(getActivity(),orders,client);
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
    myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").setValue(client, new DatabaseReference.CompletionListener() {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        progressBar.setVisibility(View.INVISIBLE);
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

        public void saveOrder(final Order order){
        progressBar.setVisibility(View.VISIBLE);
//            myRef.child("users").child(userID).child("clients").child(client.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                   Client client= dataSnapshot.getValue(Client.class);
//                   Log.d("val",dataSnapshot.getValue().toString());
//                   // client.getOrders()!=null&&client.getOrders().size()>0
//                    if (client.getOrders()!=null&&client.getOrders().size()>0) {
//                        Log.d("val",String.valueOf(client.getOrders().size()));
//                        client.getOrders().add(order);
//                        //client.setOrders(raw);
                        myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").push().setValue(order, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                pushedref = databaseReference.getKey();
                                if (Integer.parseInt(quantity.getText().toString()) > 0) {
                                    for (int j = 0; j < 1; j++) {
                                        int[] def = new int[1];
                                        def[0] = 0;
                                        if (temp != null && temp[j] != null && temp[j].getRawImage() != null) {
                                            try {
                                                File img = saveBitmapToImg(temp[0].getRawImage(), String.valueOf(System.currentTimeMillis()));
                                                uploadFile(Uri.fromFile(img), pushedref, def);
                                                //product.setPicLink(imglink);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                 }
                                      }
                                  }
                                } else
                                    progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
//                    }else {
//                       Log.d("DATASNAPHOT","null");
//                        ArrayList<Order> orders= new ArrayList<>();
//                        orders.add(order);
//                        client.setOrders(orders);
//                        myRef.child("users").child(userID).child("clients").child(cname.getText().toString()).setValue(client, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                pushedref = databaseReference.getKey();
//                                if (Integer.parseInt(quantity.getText().toString()) > 0) {
//                                    for (int j = 0; j < 1; j++) {
//                                        int[] def = new int[1];
//                                        def[0] = 0;
//                                        if (temp != null && temp[j] != null && temp[j].getRawImage() != null) {
//                                            try {
//                                                File img = saveBitmapToImg(temp[0].getRawImage(), String.valueOf(System.currentTimeMillis()));
//                                                uploadFile(Uri.fromFile(img), pushedref, def);
//                                                //product.setPicLink(imglink);
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                } else
//                                    progressBar.setVisibility(View.INVISIBLE);
//                            }
//                        });
//                  }
//
//
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
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
  private Order getOrder() {
      Order order= new Order();
      long time=System.currentTimeMillis();
      int i=Integer.parseInt(quantity.getText().toString());
      order.setQuantity(Integer.parseInt(quantity.getText().toString()));
      ArrayList<Product> products=new ArrayList<>();
      for(int j=0;j<i;j++){
          Product product= new Product();
          product.setKind(String.valueOf(spinner.getSelectedItemPosition()));

          product.setTime(System.currentTimeMillis());
          product.setPrice(pt[j]!=null?Double.parseDouble(pt[j].getText().toString()):null);
          try {
              product.setDue(dt[j]!=null?Double.parseDouble(dt[j].getText().toString()):null);
          }catch (Exception E){
              E.printStackTrace();
              product.setDue(0);

          }

          product.setStatus(sp[j].getSelectedItemPosition());
          //check if product image taken


          products.add(product);

      }
      order.setTotal(total);
      order.setComment(cmt.getText().toString()==null?"":cmt.getText().toString());
      order.setTime(time);
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
            if(!TextUtils.isEmpty(address.getText().toString()))
             client.setAddress(address.getText().toString());
            if(!TextUtils.isEmpty(email.getText().toString()))
               client.setEmail(email.getText().toString());
            if(!TextUtils.isEmpty(phone.getText().toString()))
                client.setHomephone(phone.getText().toString());

            return client;


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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                try {
                    //savebitmap(bitmap, itemname.getText().toString());
                    Product product=new Product();
                    product.setRawImage(bitmap);
                    temp[counter]=product;
                    Log.d("counter",String.valueOf(counter));
                   addimgs[counter].setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        openImageInGallery(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/MyNotes/pictures/" + itemname.getText().toString() + ".jpg"));
//                    }
//                });
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.error_cam), Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    private String uploadFile(Uri filePath, final String pushedref, final int[] index) {

        final String[] id = {null};
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
          // id[0] =UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                             @SuppressWarnings("VisibleForTests") final String  downloadUrl =
                                    taskSnapshot.getMetadata().getDownloadUrl().toString();
                            myRef.child("users").child(userID).child("clients").child(cname.getText().toString()).child("orders").child(pushedref).child("products").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<ArrayList<Product>> t = new GenericTypeIndicator<ArrayList<Product>>() {};
                                    final ArrayList<Product> products=dataSnapshot.getValue(t);
                                    products.get(index[0]).setPicLink(downloadUrl);
                                    links[0]=downloadUrl;
                                    myRef.child("users").child(userID).child("clients").child(cname.getText().toString()).child("orders").child(pushedref).child("products").setValue(products, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                           index[0]++;

                                           if(index[0]<Integer.parseInt(quantity.getText().toString())){
                                           try {
                                                File img = saveBitmapToImg(temp[index[0]].getRawImage(), String.valueOf(System.currentTimeMillis()));
                                                Uri uri= Uri.fromFile(img);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                uploadFile(uri,pushedref,index);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }else {
                                               progressBar.setVisibility(View.INVISIBLE);
                                               getActivity().getSupportFragmentManager().popBackStack();
                                           }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            // Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    return id[0];
    }

    public static File saveBitmapToImg(Bitmap bmp, String filename) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()+"/salesmanager/pictures/"+filename+
                ".jpg");
        File g = new File(Environment.getExternalStorageDirectory()+"/salesmanager/pictures/");
        if(!g.exists())
            g.mkdirs();

        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;

    }
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getActivity().getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        date.setText(Utils.FormatCalendar(calendar));
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.select_custom){
            DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getActivity().getFragmentManager(), "datePicker");
        }
    }
}
