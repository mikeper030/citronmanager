package com.ultimatesoftil.citron.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // welcome setting is on
        if( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("welcome",false)&&getIntent().getBooleanExtra("done",true)){
            finish();
            startActivity(new Intent(MainActivity.this,SplashActivity.class));
        }else {


            setContentView(R.layout.activity_list);
            auth = FirebaseAuth.getInstance();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            FirebaseUser user = auth.getCurrentUser();


            try {
                userID = user.getUid();
            } catch (Exception e) {
                startActivity(new Intent(MainActivity.this, EmailLogin.class));
                finish();
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.no_connection), Snackbar.LENGTH_SHORT).show();
            }
            if (user == null || userID == null) {
                startActivity(new Intent(MainActivity.this, EmailLogin.class));
                finish();
            } else {
                setupToolbar();
                Log.d("user", user.getUid());
                if (Build.VERSION.SDK_INT >= 23 && !hasPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA}))
                    requestpermissions(this);
                if (isTwoPaneLayoutUsed()) {
                    twoPaneMode = true;
                    LogUtil.logD("TEST", "TWO POANE TASDFES");
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    enableActiveItemState();
                }

                if (savedInstanceState == null && twoPaneMode) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    setupDetailFragment();
                }
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
           getSupportFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
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
       getSupportFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
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
       // getMenuInflater().inflate(R.menu.sample_actions, menu);
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

//    @Override
//    protected int getSelfNavDrawerItem() {
//        return R.id.quotes;
//    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }
    public static void requestpermissions(Activity activity) {


        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "Write permission is needed for camera", Toast.LENGTH_SHORT).show();
            // Show an explanation to the user *asynchronously* -- don't
            // block this thread waiting for the user's response! After the
            // user sees the explanation, try again to request the
            // permission.
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.SEND_SMS},
                    5);

            Toast.makeText(activity, "REQUEST  PERMISSIONS", Toast.LENGTH_LONG).show();

        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.SEND_SMS},
                    6);


        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
