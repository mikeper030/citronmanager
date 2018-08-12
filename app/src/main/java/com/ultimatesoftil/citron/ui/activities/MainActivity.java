package com.ultimatesoftil.citron.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ultimatesoftil.citron.FirebaseAuth.EmailLogin;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.ui.base.BaseActivity;
import com.ultimatesoftil.citron.util.LogUtil;

import java.util.ArrayList;

/**
 * Lists all available quotes. This Activity supports a single pane (= smartphones) and a two pane mode (= large screens with >= 600dp width).
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class MainActivity extends BaseActivity implements ClientListFragment.Callback {
    /**
     * Whether or not the activity is running on a device with a large screen
     */
    private boolean twoPaneMode;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = auth.getCurrentUser();



        try {
            userID = user.getUid();
        } catch (Exception e) {
            startActivity(new Intent(MainActivity.this,EmailLogin.class));
            finish();
            Snackbar.make(findViewById(android.R.id.content),getResources().getString(R.string.no_connection),Snackbar.LENGTH_SHORT).show();
        }
        if(user==null||userID==null){
            startActivity(new Intent(MainActivity.this,EmailLogin.class));
            finish();
        }else{
            setupToolbar();
            Log.d("user",user.getUid());
            if (isTwoPaneLayoutUsed()) {
                twoPaneMode = true;
                LogUtil.logD("TEST","TWO POANE TASDFES");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                enableActiveItemState();
            }

            if (savedInstanceState == null && twoPaneMode) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setupDetailFragment();
            }
        }

    }

    /**
     * Called when an item has been selected
     *
     * @param client the selected client object
     */
    @Override
    public void onItemSelected(Client client) {
        if (twoPaneMode) {
            // Show the quote detail information by replacing the DetailFragment via transaction.
            ClientDetailFragment fragment = ClientDetailFragment.newInstance(client);
            getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
        } else {
            // Start the detail activity in single pane mode.
            Intent detailIntent = new Intent(MainActivity.this, ClientDetailActivity.class);
            detailIntent.putExtra("client", client);
            Log.d("client",client.getName());
            startActivity(detailIntent);
        }
    }

    private void setupToolbar() {
        final ActionBar ab = getActionBarToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupDetailFragment() {

        ClientDetailFragment fragment =  ClientDetailFragment.newInstance(null);
        getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
    }


    /**
     * Enables the functionality that selected items are automatically highlighted.
     */
    private void enableActiveItemState() {
        ClientListFragment fragmentById = (ClientListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list);
        fragmentById.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * Is the container present? If so, we are using the two-pane layout.
     *
     * @return true if the two pane layout is used.
     */
    private boolean isTwoPaneLayoutUsed() {
        return findViewById(R.id.article_detail_container) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_quotes;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
}
