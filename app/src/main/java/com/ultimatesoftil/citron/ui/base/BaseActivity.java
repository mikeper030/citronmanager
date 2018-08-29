package com.ultimatesoftil.citron.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ultimatesoftil.citron.ui.activities.GenericOrdersActivity;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.ui.activities.ClientListFragment;
import com.ultimatesoftil.citron.ui.activities.SettingsActivity;


import static com.ultimatesoftil.citron.util.LogUtil.logD;
import static com.ultimatesoftil.citron.util.LogUtil.makeLogTag;

/**
 * The base class for all Activity classes.
 * This class creates and provides the navigation drawer and toolbar.
 * The navigation logic is handled in {@link BaseActivity#goToNavDrawerItem(int)}
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = makeLogTag(BaseActivity.class);

    protected static final int NAV_DRAWER_ITEM_INVALID = -1;
    //public static HashMap<Client,Order> data;
    private DrawerLayout drawerLayout;
    private Toolbar actionBarToolbar;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    /**
     * Sets up the navigation drawer.
     */
    private void setupNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            // current activity does not have a drawer.
            return;
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerSelectListener(navigationView);
            setSelectedItem(navigationView);
        }

        logD(TAG, "navigation drawer setup finished");
    }

    /**
     * Updated the checked item in the navigation drawer
     * @param navigationView the navigation view
     */
    private void setSelectedItem(NavigationView navigationView) {
        // Which navigation item should be selected?
        int selectedItem = getSelfNavDrawerItem(); // subclass has to override this method
        navigationView.setCheckedItem(selectedItem);
    }

    /**
     * Creates the item click listener.
     * @param navigationView the navigation view
     */
    private void setupDrawerSelectListener(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        drawerLayout.closeDrawers();
                        onNavigationItemClicked(menuItem.getItemId());
                        return true;
                    }
                });
    }

    /**
     * Handles the navigation item click.
     * @param itemId the clicked item
     */
    private void onNavigationItemClicked(final int itemId) {
        if(itemId == getSelfNavDrawerItem()) {
            // Already selected
            closeDrawer();
            return;
        }

        goToNavDrawerItem(itemId);
    }

    /**
     * Handles the navigation item click and starts the corresponding activity.
     * @param item the selected navigation item
     */
    private void goToNavDrawerItem(int item) {
        Log.d("item",String.valueOf(item));
        switch (item) {

            case R.id.quotes:
//                GenericOrdersFragment frag1=new GenericOrdersFragment();
//                getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag1).addToBackStack(null).commit();
                Intent i= new Intent(BaseActivity.this,GenericOrdersActivity.class);
                startActivity(i);
                Log.d("ss","going to orders");
                break;
            case R.id.nav_samples:
                SmsBatch frag=new SmsBatch();
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag).addToBackStack(null).commit();
                break;
            case R.id.nav_settings:
               startActivity(new Intent(BaseActivity.this, SettingsActivity.class));
            break;
//            case R.id.rest:
//            data=new HashMap<>();
//
//               String telephone=null;
//                try{
//                    File file = new File(Environment.getExternalStorageDirectory()+"/Download/cht.vcf");
//                    List<VCard> vcards = Ezvcard.parse(file).all();
//                    for (VCard vcard : vcards){
//                      Client client=new Client();
//                      try {
//                          vcard.getFormattedName().getValue();
//                      }catch (Exception e){
//                          e.printStackTrace();
//                          continue;
//                      }
//
//
//                      Log.d("Name: " ,vcard.getFormattedName().getValue());
//                      String temp=vcard.getFormattedName().getValue();
//                        String[] words = temp.split("\\s+");
//                       StringBuilder sb=new StringBuilder();
//                        double sum=0;
//                        for (int i = 0; i < words.length; i++) {
//                            words[i] = words[i].replaceAll("[^\\w]", "");
//                        }
//                       for (int i=0;i<words.length;i++){
//                            try {
//                              sum =Double.parseDouble(words[i]);
//                            }catch (Exception e){
//
//                                sb.append(words[i]+" ");
//                                e.printStackTrace();
//                            }
//                       }
//                       client.setName(sb.toString());
//                        Order order=new Order();
//                        ArrayList<Product>products=new ArrayList<>();
//                        Product product= new Product();
//                        product.setKind("0");
//                        product.setPrice(sum);
//                        products.add(product);
//                        order.setProducts(products);
//                       Log.d("Telephone numbers:","");
//                        for (Telephone tel : vcard.getTelephoneNumbers()){
//                            System.out.println(tel.getTypes() + ": " + tel.getText());
//                          telephone=tel.getText();
//                          telephone=telephone.replace("+","");
//                           client.setPhone(telephone);
//                        }
//                    data.put(client,order);
//
//                    }
//                }catch(Exception e){e.printStackTrace();}
//                break;
       }
    }

    /**
     * Provides the action bar instance.
     * @return the action bar.
     */
    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * have to override this method.
     */
    protected int getSelfNavDrawerItem() {
        return NAV_DRAWER_ITEM_INVALID;
    }

    protected void openDrawer() {
        if(drawerLayout == null)
            return;

        drawerLayout.openDrawer(GravityCompat.START);
    }

    protected void closeDrawer() {
        if(drawerLayout == null)
            return;

        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public abstract boolean providesActivityToolbar();

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
