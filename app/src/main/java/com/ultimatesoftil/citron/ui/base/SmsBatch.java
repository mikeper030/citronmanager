package com.ultimatesoftil.citron.ui.base;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ultimatesoftil.citron.R;
import com.ultimatesoftil.citron.adapters.SmsListAdapter;
import com.ultimatesoftil.citron.models.Client;
import com.ultimatesoftil.citron.models.FButton;
import com.ultimatesoftil.citron.ui.activities.ClientListFragment;
import com.ultimatesoftil.citron.util.Utils;

import java.util.ArrayList;

/**
 * Created by Mike on 21/08/2018.
 */

public class SmsBatch extends Fragment{
    private ListView listView;
    private FButton send;
    private TextInputEditText content;
    private boolean isAll=false;
    private ArrayList<Client>clients,lateClients;
    boolean late=false,owes=false,defaultb=true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView=(ListView) view.findViewById(R.id.listView22);
        send=(FButton)view.findViewById(R.id.sms_batch_send);
        content=(TextInputEditText)view.findViewById(R.id.sms_input);
        Bundle bundle=getArguments();
        if(bundle==null) {
            final SmsListAdapter adapter = new SmsListAdapter(getActivity(), ClientListFragment.clients);
            isAll=true;
            listView.setAdapter(adapter);
        }else{
            if(bundle.getSerializable("clients")!=null){
                late=true;
                isAll=false;
                clients=(ArrayList<Client>) bundle.getSerializable("clients");
                final SmsListAdapter adapter = new SmsListAdapter(getActivity(), clients);
                listView.setAdapter(adapter);
                content.setText(R.string.late);
            }
            else
              if(bundle.getSerializable("owe")!=null){
                 lateClients =(ArrayList<Client>) bundle.getSerializable("owe");
                owes=true;
                  String msg= PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("debt", "null");
                  if(msg.equals("null")){
                      msg=getResources().getString(R.string.due_late);
                      content.setText("default");


                  }else{
                      defaultb=false;
                     content.setText(msg);
                  }
                  final SmsListAdapter adapter = new SmsListAdapter(getActivity(), lateClients);
                  listView.setAdapter(adapter);
              }

        }
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(content.getText().toString())) {
                        if(isAll){
                            for (int i = 0; i < ClientListFragment.clients.size(); i++) {
                                View v = getViewByPosition(i, listView);
                                CheckBox checkBox = v.findViewById(R.id.sms_check);
                                if (checkBox.isChecked()) {
                                    Log.d("sending", "message");

                                        sendMsg(getActivity(), content.getText().toString(), ClientListFragment.clients.get(i).getPhone());
                                }
                            }
                        }
                        else
                            if(late){
                                for (int i = 0; i < clients.size(); i++) {
                                    View v = getViewByPosition(i, listView);
                                    CheckBox checkBox = v.findViewById(R.id.sms_check);
                                    if (checkBox.isChecked()) {
                                        Log.d("sending", "message");

                                        sendMsg(getActivity(), content.getText().toString(), clients.get(i).getPhone());

                                    }
                                }
                            }
                            else
                                if(owes){
                                    for (int i = 0; i < lateClients.size(); i++) {
                                        View v = getViewByPosition(i, listView);
                                        CheckBox checkBox = v.findViewById(R.id.sms_check);
                                        if (checkBox.isChecked()) {
                                            Log.d("sending", "message");
                                            if(defaultb){
                                               StringBuilder sb= new StringBuilder();
                                               sb.append(getResources().getString(R.string.due_late));
                                               sb.append(" ");
                                               double debt=0;
                                               for(int j=0;j<lateClients.get(i).getOrders().size();j++){
                                                  if(lateClients.get(i).getOrders().get(j).getDue()!=0){
                                                      debt+=lateClients.get(i).getOrders().get(j).getDue();
                                                  }
                                               }
                                                sb.append(debt);
                                                sb.append(" ");
                                                sb.append("please pay your debt as soon as possible. Thanks, Shimon Badush.");
                                                Log.d("smsmsg",sb.toString());
                                                sendMsg(getActivity(),sb.toString() , lateClients.get(i).getPhone());
                                            }

                                            else{
                                                sendMsg(getActivity(),content.getText().toString(), lateClients.get(i).getPhone());
                                                Log.d("smsmsg","default");
                                            }

                                        }
                                    }
                                }

                        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "ההודעות נשלחו בהצלחה!", Snackbar.LENGTH_LONG);
                        View v = snack.getView();
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)v.getLayoutParams();
                        params.gravity = Gravity.TOP|Gravity.CENTER;
                        v.setLayoutParams(params);
                        snack.show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "נא הזן תוכן!", Snackbar.LENGTH_SHORT).show();
                    }
                }

            });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       if(Utils.isTabletDevice(getActivity()))
        return inflater.inflate(R.layout.send_message,container,false);
      else
           return inflater.inflate(R.layout.send_message_small,container,false);

    }
    protected void sendMsg(Context context, String content,String number) {
        SmsManager smsMgr = SmsManager.getDefault();

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);


                    //  Send 160 bytes of the total message until all parts are sent
                    smsMgr.sendTextMessage(number, null, content, sentPI, deliveredPI);
                }



    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
