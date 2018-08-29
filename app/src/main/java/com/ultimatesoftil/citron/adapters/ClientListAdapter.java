package com.ultimatesoftil.citron.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.Order;
import com.ultimatesoftil.citron.models.PdfDocument;
import com.ultimatesoftil.citron.ui.activities.MainActivity;

/**
 * Created by Mike on 01/08/2018.
 */

public class ClientListAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<Client> items; //dat
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ArrayList names;

    String searchText;
    public ClientListAdapter(Context context, ArrayList<Client> items) {

        this.context = context;
        this.items = items;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = auth.getCurrentUser();
        try {
            userID = user.getUid();
        } catch (Exception e) {

        }
    }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_client, container, false);
            }
            final Activity activity= (Activity) convertView.getContext();
            final Client itemc = (Client) getItem(position);
            TextView name=(TextView) convertView.findViewById(R.id.list_item_name);
            name.setText(itemc.getName());
            TextView phone=convertView.findViewById(R.id.list_item_phone);
            phone.setText(context.getResources().getString(R.string.mobile)+":"+" "+itemc.getPhone());

            ((TextView) convertView.findViewById(R.id.list_item_added)).setText(itemc.getDateTimeFormatted());
            if(searchText!=null)
                if(searchText.length()>0) {
                    //color your text here
                    String desc=null;
                    if(name.getText().toString().contains(searchText))
                     desc = items.get(position).getName();
                    else
                    if(phone.getText().toString().contains(searchText))
                      desc=items.get(position).getPhone();

                        SpannableStringBuilder sb=null;

                    int index = desc.indexOf(searchText);
                    while (index > -1) {
                        sb = new SpannableStringBuilder(desc);
                        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 255, 51)); //specify color here
                        sb.setSpan(new BackgroundColorSpan(Color.YELLOW),index,index+searchText.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        //sb.setSpan(fcs, index, index+searchText.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        index = desc.indexOf(searchText, index + 1);

                    }
                    if(name.getText().toString().contains(searchText))
                    name.setText(sb);
                    else
                        if(phone.getText().toString().contains(searchText))
                            phone.setText(sb);

                }
            ((ImageButton)convertView.findViewById(R.id.client_menu)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, view);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(final MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.one:
                                    AlertDialog.Builder builder;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                                    } else {
                                        builder = new AlertDialog.Builder(context);
                                    }
                                    builder.setTitle(R.string.delete)
                                            .setMessage(R.string.sure_delete)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                  deleteClient(itemc,activity);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                    break;
                                    
                                case R.id.two:
                                          //exprot pdf

                                    ArrayList<Order>orders=getOrders(items.get(position),activity);


                                    break;
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });
//            final ImageView img = (ImageView) convertView.findViewById(R.id.thumbnail);
//            Glide.with(getActivity()).load(item.photoId).asBitmap().fitCenter().into(new BitmapImageViewTarget(img) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    img.setImageDrawable(circularBitmapDrawable);
//                }
//            });

            return convertView;
        }
    public void setFilter(ArrayList<Client> notes,String searchText){
        items=new ArrayList<>();
        items.addAll(notes);
        notifyDataSetChanged();
        this.searchText=searchText;
    }
    private void deleteClient(Client client, final Activity activity) {
      myRef.child("users").child(userID).child("clients").child(client.getName()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {

              Snackbar.make(activity.findViewById(android.R.id.content),context.getResources().getString(R.string.deleted),Snackbar.LENGTH_SHORT).show();

          }
      });
    }
   private ArrayList<Order> getOrders(final Client client, final Activity activity){
        final ArrayList<Order> orders=new ArrayList<>();
        final int[]counter={0};
       final PdfDocument.OnPdfUpdateListener finishListener=new PdfDocument.OnPdfUpdateListener() {
           @Override
           public void OnDownloadDeckFinish(String Response) {
               Snackbar.make(activity.findViewById(android.R.id.content),context.getResources().getString(R.string.exported),Snackbar.LENGTH_SHORT).show();

           }
       };
        myRef.child("users").child(userID).child("clients").child(client.getName()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    orders.add(order);
                    counter[0]++;
                    if (counter[0]==dataSnapshot.getChildrenCount()) {
                        if (orders != null && orders.size() > 0) {
                            ProgressDialog dialog = new ProgressDialog(context);
                            dialog.setMessage("מייצא מסמך אנא המתן...");
                            dialog.show();
                            // you can add here  your stuffs

                            PdfDocument mTask = new PdfDocument(finishListener, client, orders);
                            mTask.execute();
                        } else {
                            //no datat
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    return orders;
    }
}
