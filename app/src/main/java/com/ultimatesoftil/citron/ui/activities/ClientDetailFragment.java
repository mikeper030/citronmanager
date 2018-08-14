package com.ultimatesoftil.citron.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.Bind;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ultimatesoftil.citron.FirebaseAuth.EmailLogin;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.OrderListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.ui.base.BaseActivity;
import com.ultimatesoftil.citron.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

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
    private ImageView def1;
    private TextView def2;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ListView orderlist;
    private OrderListAdapter adapter;
    private ArrayList<Order> orders=new ArrayList<>();
    private Button addOrder;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
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
            loadBackdrop();
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
        if(client!=null) {
            name.setText(client.getName() != null ? client.getName() : "");
            email.setText(client.getEmail() != null ? client.getEmail() : "");
            address.setText(client.getAddress() != null ? client.getAddress() : "");
            mobile.setText(client.getPhone() != null ? client.getPhone() : "");
            home.setText(client.getHomephone() != null ? client.getHomephone() : "");
            setUpOrders();
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

    }

    private void setUpOrders() {
//        orders.add(dataSnapshot.getValue(Order.class));
//            adapter=new OrderListAdapter(getActivity(),orders);
//            orderlist.setAdapter(adapter);
        if(orders.size()==0) {

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
                    orders.add(dataSnapshot.getValue(Order.class));
                    adapter = new OrderListAdapter(getActivity(), orders, client);
                    orderlist.setAdapter(adapter);

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

    private void loadBackdrop() {
    //    Glide.with(this).load(dummyItem.photoId).centerCrop().into(backdropImg);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // your logic
                return true;
        }
        return super.onOptionsItemSelected(item);
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
