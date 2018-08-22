package com.ultimatesoftil.citron.ui.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.ultimatesoftil.citron.FirebaseAuth.EmailLogin;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.ClientListAdapter;
import com.ultimatesoftil.citron.adapters.NotificationListAdapter;
import com.ultimatesoftil.citron.adapters.OrderListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.ui.base.BaseActivity;
import com.ultimatesoftil.citron.ui.base.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Shows the quote detail page.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class ClientDetailFragment extends BaseFragment {

    /**
     * The argument represents the dummy item ID of this fragment.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content of this fragment.
     */
    private Client client;
    private TextInputEditText name,address,mobile,home,email;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private ImageView defff1;
    private ImageView def1;
    private TextView def2,defff2;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ListView orderlist;
    private OrderListAdapter adapter;
    private ArrayList<Order> orders=new ArrayList<>();
    private Button addOrder;
    private ListView notifications;
    private Switch aswitch;
    private ArrayList<Product> notificationProducs= new ArrayList<>();
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    private ImageView backdropImg;
    private FloatingActionButton send;
    private FloatingActionButton pic;
    private Camera camera;
    private FirebaseStorage storage;
    private StorageReference storageReference;

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
    private void uploadFile2(Uri filepath){
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
               client.setImglink(downloadUrl);
                myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").setValue(client);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {

                try {
                   File f =saveBitmapToImg(bitmap, "background_img");
                   uploadFile2(Uri.fromFile(f));
                   Glide.with(getActivity()).load(bitmap).fitCenter().into(backdropImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.error_cam), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        try {
            userID = user.getUid();
        } catch (Exception e) {
            startActivity(new Intent(getActivity(),EmailLogin.class));
            getActivity().finish();
            Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.no_connection),Snackbar.LENGTH_SHORT).show();
        }

        if (getArguments().getSerializable("client")!=null) {
            // load client item by using the passed client object.
            client=(Client) getArguments().getSerializable("client");

        }else {
            client=getFirstClient();

        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_client_spec);

        if (!((BaseActivity) getActivity()).providesActivityToolbar()) {
            // No Toolbar present. Set include_toolbar:
            ((BaseActivity) getActivity()).setToolbar((Toolbar) rootView.findViewById(R.id.toolbar));
        }

        if (client != null) {

            collapsingToolbar.setTitle(client.getName());
//            author.setText(dummyItem.author);
//            quote.setText(dummyItem.content);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name=(TextInputEditText)view.findViewById(R.id.dtl_name);
        email=(TextInputEditText)view.findViewById(R.id.dtl_email);
        address=(TextInputEditText)view.findViewById(R.id.dtl_address);
        mobile=(TextInputEditText)view.findViewById(R.id.dtl_mobile);
        home=(TextInputEditText)view.findViewById(R.id.dtl_h_p);
        orderlist=(ListView)view.findViewById(R.id.order_list);
        addOrder=(Button)view.findViewById(R.id.order_add);
        def1=(ImageView)view.findViewById(R.id.default_add_order);
        def2=(TextView)view.findViewById(R.id.textView9);
        notifications=(ListView)view.findViewById(R.id.details_notification_list);
        defff1=(ImageView) view.findViewById(R.id.deff1);
        defff2=(TextView)view.findViewById(R.id.deff2);
        send=(FloatingActionButton)view.findViewById(R.id.c_sms);
        aswitch=(Switch)view.findViewById(R.id.switch2);
        backdropImg=(ImageView)view.findViewById(R.id.backdrop);
        pic=(FloatingActionButton) view.findViewById(R.id.cam);
        loadBackdrop();
        if(client!=null) {
            name.setText(client.getName() != null ? client.getName() : "");
            email.setText(client.getEmail() != null ? client.getEmail() : "");
            address.setText(client.getAddress() != null ? client.getAddress() : "");
            mobile.setText(client.getPhone() != null ? client.getPhone() : "");
            home.setText(client.getHomephone() != null ? client.getHomephone() : "");
            aswitch.setChecked(client.isNotifications_enabled());
            aswitch.setText(client.isNotifications_enabled()? "פעיל":"כבוי");
            aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        compoundButton.setText("פעיל");
                        client.setNotifications_enabled(true);
                        myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").child("notifications_enabled").setValue(true);
                    }else {
                        compoundButton.setText("כבוי");
                        client.setNotifications_enabled(false);
                        myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").child("notifications_enabled").setValue(false);

                    }
                    }
            });
            setUpOrders();
            setUpNotifications();
        }
        addOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddClientOrderFragment fragobj = new AddClientOrderFragment();
                Bundle bundle1=new Bundle();
                bundle1.putSerializable("client",client);
                fragobj.setArguments(bundle1);

                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
            }
        });
        def1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddClientOrderFragment fragobj = new AddClientOrderFragment();
                Bundle bundle1=new Bundle();
                bundle1.putSerializable("client",client);
                fragobj.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:"+client.getPhone());
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("", "");
                startActivity(it);
            }
        });
       pic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               takePicture();
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

    private void setUpNotifications() {
        final int[] count = {0};

      final ArrayList<Order>orders=new ArrayList<>();
        myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Order order = dataSnapshot1.getValue(Order.class);
                        orders.add(order);
                        count[0]++;
                    }
                    if (count[0] == dataSnapshot.getChildrenCount()) {
                        for (int i = 0; i < orders.size(); i++) {
                            for (int j = 0; j < orders.get(i).getProducts().size(); j++) {
                                if (orders.get(i).getProducts().get(j).getStatus() == 1) {
                                    notificationProducs.add(orders.get(i).getProducts().get(j));
                                }
                            }
                            NotificationListAdapter adapter = new NotificationListAdapter(getActivity(), notificationProducs, orders, client,false);
                            notifications.setAdapter(adapter);

                        }
                        defff1.setVisibility(View.INVISIBLE);
                        defff2.setVisibility(View.INVISIBLE);
                        Log.d("notifications  display",String.valueOf(notificationProducs.size()));
                    }

                } else {
                    defff1.setVisibility(View.VISIBLE);
                    defff2.setVisibility(View.VISIBLE);
                }
                if(notificationProducs.size()==0){
                    defff1.setVisibility(View.VISIBLE);
                    defff2.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUpOrders() {
//        orders.add(dataSnapshot.getValue(Order.class));
//            adapter=new OrderListAdapter(getActivity(),orders);
//            orderlist.setAdapter(adapter);


            Log.d("client", client.getName());
            Log.d("user", userID);

            myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChildren()) {
                        def1.setVisibility(View.VISIBLE);
                        def2.setVisibility(View.VISIBLE);
                        def1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AddClientOrderFragment fragobj = new AddClientOrderFragment();
                                Bundle bundle1=new Bundle();
                                bundle1.putSerializable("client",client);
                                fragobj.setArguments(bundle1);

                                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
                            }
                        });

                    } else {
                        def2.setVisibility(View.INVISIBLE);
                        def1.setVisibility(View.INVISIBLE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("dara",String.valueOf(dataSnapshot.child("time").getValue(Long.class)));
                    getUpdates(dataSnapshot);

                    if(orders.size()==0){
                        def1.setVisibility(View.VISIBLE);
                        def2.setVisibility(View.VISIBLE);
                        def1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AddClientOrderFragment fragobj = new AddClientOrderFragment();
                                Bundle bundle1=new Bundle();
                                bundle1.putSerializable("client",client);
                                fragobj.setArguments(bundle1);

                                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
                            }
                        });
                    }else{
                        def1.setVisibility(View.INVISIBLE);
                        def2.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    for(int i=0;i<orders.size();i++){
                        if(orders.get(i).getTime()==(dataSnapshot.child("time").getValue(Long.class))){
                            orders.remove(i);
                            adapter= new OrderListAdapter(getActivity(),orders,client);
                            orderlist.setAdapter(adapter);
                            if(orders.size()==0){
                                def1.setVisibility(View.VISIBLE);
                                def2.setVisibility(View.VISIBLE);
                            }else{
                                def1.setVisibility(View.INVISIBLE);
                                def2.setVisibility(View.INVISIBLE);
                            }
                        }
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
       Order order=dataSnapshot.getValue(Order.class);
        for (int i=0;i<orders.size();i++){
            if(orders.get(i).getTime()==order.getTime())
                return;

        }
        Log.d("size44",String.valueOf(orders.size()));
        orders.add(dataSnapshot.getValue(Order.class));

        adapter = new OrderListAdapter(getActivity(),orders, client);
        orderlist.setAdapter(adapter);
    }


    private void loadBackdrop() {
        if(client!=null&&client.getImglink()!=null){
            Glide.with(this).load(client.getImglink()).fitCenter().into(backdropImg);

        }else
        Glide.with(this).load(R.drawable.backdrop_default).fitCenter().into(backdropImg);
    }



    public static ClientDetailFragment newInstance(Client client) {
        ClientDetailFragment fragment = new ClientDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("client",client);
        fragment.setArguments(args);
        return fragment;
    }

    public ClientDetailFragment() {}
    private Client getFirstClient() {
        Query query= myRef.child("users").child(userID).child("clients").limitToFirst(1);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                client=dataSnapshot.child("details").getValue(Client.class);
                name.setText(client.getName() != null ? client.getName() : "");
                email.setText(client.getEmail() != null ? client.getEmail() : "");
                address.setText(client.getAddress() != null ? client.getAddress() : "");
                mobile.setText(client.getPhone() != null ? client.getPhone() : "");
                home.setText(client.getHomephone() != null ? client.getHomephone() : "");
                setUpOrders();
                setUpNotifications();
                aswitch.setChecked(client.isNotifications_enabled());
                aswitch.setText(client.isNotifications_enabled()? "פעיל":"כבוי");
                aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b) {
                            compoundButton.setText("פעיל");
                            client.setNotifications_enabled(true);
                            myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").child("notifications_enabled").setValue(true);
                        }else {
                            compoundButton.setText("כבוי");
                            client.setNotifications_enabled(false);
                            myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").child("notifications_enabled").setValue(false);

                        }
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
        return client;
    }

}
