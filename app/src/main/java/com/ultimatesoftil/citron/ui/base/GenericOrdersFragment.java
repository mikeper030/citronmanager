package com.ultimatesoftil.citron.ui.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ultimatesoftil.citron.R;

import org.w3c.dom.Text;

/**
 * Created by Mike on 22/08/2018.
 */

public class GenericOrdersFragment extends Fragment{
    private TableLayout t1;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gen_order,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        t1 = (TableLayout) view.findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(getActivity());
      //  tr_head.setId(10);
        tr_head.setBackgroundColor(getResources().getColor(R.color.material_gray1));
        tr_head.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
               105));
        TextView t11= createHeaderTextView("שם הלקוח");
        TextView t12=createHeaderTextView("אתרוגים בהתעניינות");
        TextView t13=createHeaderTextView("לולבים בהתעניינות");
        TextView t14=createHeaderTextView("אתרוגים נרכשו");
        TextView t15=createHeaderTextView("לולבים נרכשו");

        tr_head.addView(t11,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
        tr_head.addView(t12,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
        tr_head.addView(t13,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
        tr_head.addView(t14,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.58f)));
        tr_head.addView(t15,(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,0.5f)));
       t1.addView(tr_head);

        Integer count=0;

        while (count<10) {
            String date ="20/09/1993";// get the first variable
            double weight_kg = 100;// get the second variable
// Create the table row
            TableRow tr = new TableRow(getActivity());
            if(count%2!=0) tr.setBackgroundColor(Color.GRAY);else
                tr.setBackgroundColor(Color.BLACK);
            tr.setId(100+count);
            tr.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

//Create two columns to add as table data
            // Create a TextView to add date
            TextView labelDATE = new TextView(getActivity());
            labelDATE.setId(200+count);
            labelDATE.setText(date);
            labelDATE.setPadding(2, 0, 5, 0);
            labelDATE.setTextColor(Color.WHITE);
            tr.addView(labelDATE);
            TextView labelWEIGHT = new TextView(getActivity());
            labelWEIGHT.setId(200+count);
            labelWEIGHT.setText(String.valueOf(weight_kg));
            labelWEIGHT.setTextColor(Color.WHITE);
            tr.addView(labelWEIGHT);

// finally add this row to table
            t1.addView(tr, new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            count++;
        }
        TableRow tr_bottom = new TableRow(getActivity());
        //  tr_head.setId(10);
        tr_bottom.setBackgroundColor(getResources().getColor(R.color.material_gray1));
        tr_bottom.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                105));
        TextView t=createHeaderTextView("סה\"כ");
        tr_bottom.addView(t);
        t1.addView(tr_bottom);
    }


    private TextView createHeaderTextView(String text){
        TextView textView= new TextView(getActivity());
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(20);
        textView.setText(text);
        return textView;
    }

}
