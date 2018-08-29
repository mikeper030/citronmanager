package com.ultimatesoftil.citron.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.pdf.PdfPCell;
import com.ultimatesoftil.citron.FirebaseAuth.EmailLogin;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.ClientListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.Product;
import com.ultimatesoftil.citron.ui.base.BaseActivity;

/**
 * Shows a list of all available quotes.
 * <p/>
 * Created by Mike Peretz on 28.08.2018.
 */
public class ClientListFragment extends ListFragment {
    public static ArrayList<Client> clients=new ArrayList<>();
    public static ArrayList<Client>data=new ArrayList<>();
    private Callback callback = dummyCallback;
    private FloatingActionButton fab;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ClientListAdapter adapter;
    private TextView textView;
    private ProgressBar progressBar;
    private HashMap<String,Order> orderso=new HashMap<>();
    private TextView notfound;

    /**
     * A callback interface. Called whenever a item has been selected.
     */
    public interface Callback {
        void onItemSelected(Client client);
    }

    /**
     * A dummy no-op implementation of the Callback interface. Only used when no active Activity is present.
     */
    private static final Callback dummyCallback = new Callback() {
        @Override
        public void onItemSelected(Client client) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_list,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab=(FloatingActionButton)view.findViewById(R.id.fab);
        textView= (TextView) view.findViewById(R.id.main_def);
        textView.setVisibility(View.VISIBLE);
        progressBar=(ProgressBar)view.findViewById(R.id.prg3);
        notfound=(TextView)view.findViewById(R.id.ter);
        setTimer(progressBar);
        textView.setText(R.string.no_items);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddClientOrderFragment fragobj = new AddClientOrderFragment();
                getActivity().getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragobj).addToBackStack(null).commit();
//                HashMap<Client,Order>data= BaseActivity.data;
//                for (Map.Entry<Client, Order> entry : data.entrySet()) {
//                    Client client = entry.getKey();
//                    Order order = entry.getValue();
//                    // ...
//                    myRef.child("users").child(userID).child("clients").child(client.getName()).child("details").setValue(client);
//                    myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").push().setValue(order);
//                }

            }
        });

        initAuth();
        /// /Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();

        try {
            userID = user.getUid();
            setUpRecyclerView();
        } catch (Exception e) {
            startActivity(new Intent(getActivity(),EmailLogin.class));
            getActivity().finish();
            Snackbar.make(getActivity().findViewById(android.R.id.content),getResources().getString(R.string.no_connection),Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sample_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("שם לקוח או מספר נייד");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.toLowerCase();
                    ArrayList<Client> newList = new ArrayList<>();
                    if (clients != null&&clients.size()>0) {
                        for (Client client : clients) {
                            if (client.getName().toLowerCase().contains(newText.toLowerCase()) || client.getPhone().contains(newText) ) {
                                newList.add(client);
                            }
                        }

                        adapter = new ClientListAdapter( getActivity(), newList);
                        setListAdapter(adapter);
                        adapter.setFilter(newList, newText);
                        if (newList.size() == 0) {
                            notfound.setVisibility(View.VISIBLE);
                            Log.d("defff", "visible");
                        } else {
                        notfound.setVisibility(View.INVISIBLE);
                            Log.d("defff", "invisible");
                        }

                    }
                    return true;

                }

            });
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }


    }
    private void setTimer(final ProgressBar progressBar){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);

            }
        }, 3000);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
               startActivity(new Intent(getActivity(),SettingsActivity.class));

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        // notify callback about the selected list item
        Client client=clients.get(position);
        callback.onItemSelected(client);
    }

    /**
     * onAttach(Context) is not called on pre API 23 versions of Android.
     * onAttach(Activity) is deprecated but still necessary on older devices.
     */
    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /**
     * Deprecated on API 23 but still necessary for pre API 23 devices.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /**
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        if (!(context instanceof Callback)) {
            throw new IllegalStateException("Activity must implement callback interface.");
        }

        callback = (Callback) context;
    }



    public ClientListFragment() {
    }
    private void initAuth() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(getActivity(),EmailLogin.class));
        }
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // Sign in logic here.
                    startActivity(new Intent(getActivity(), EmailLogin.class));
                }
            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if(clients!=null&&clients.size()>0){
           Log.d("list","update");
            adapter=new ClientListAdapter(getActivity(),clients);
            setListAdapter(adapter);
            setUpRecyclerView();

        }
    }

    private void setUpRecyclerView() {

        myRef.child("users").child(userID).child("clients").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
                textView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.child("orders").getChildren()){
                    orderso.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Order.class));
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(int i=0;i<clients.size();i++){
                    if(clients.get(i).getName().equals(dataSnapshot.child("details").child("name").getValue(String.class))){
                        clients.remove(i);
                        data.remove(i);
                       adapter= new ClientListAdapter(getActivity(),clients);
                     setListAdapter(adapter);
                    }
                }if(clients.size()==0){
                    Log.d("EMPTY","list");
                    textView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setText(R.string.no_items);

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
           orderso.clear();
             for(DataSnapshot dataSnapshot1:dataSnapshot.child("orders").getChildren()){
            orderso.put(dataSnapshot1.getKey(),dataSnapshot1.getValue(Order.class));
        }

        Client client=dataSnapshot.child("details").getValue(Client.class);
        for (int i=0;i<clients.size();i++){
            if(clients.get(i).getName().equals(client.getName()))
                return;

        }
        ArrayList<Order>orders= new ArrayList<>();


             for (Map.Entry<String, Order> entry : orderso.entrySet()) {
                orders.add(entry.getValue());
                 // ...
             }
             client.setOrders(orders);
             clients.add(client);
         data.add(client);
        adapter= new ClientListAdapter(getActivity(),clients);
        setListAdapter(adapter);
    }catch (Exception e){
         clients.clear();
             adapter= new ClientListAdapter(getActivity(),clients);
         setListAdapter(adapter);
         }
    }
}
