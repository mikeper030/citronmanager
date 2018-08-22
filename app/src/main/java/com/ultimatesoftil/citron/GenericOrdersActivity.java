package com.ultimatesoftil.citron;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GenericOrdersActivity extends AppCompatActivity {
    private TableLayout t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gen_order);
       if(Build.VERSION.SDK_INT>=21) {
           Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
           window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
           window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
           window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_primary_light));
       }t1 = (TableLayout) findViewById(R.id.main_table);
        TableRow tr_head = new TableRow(this);
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
            TableRow tr = new TableRow(this);
            if(count%2!=0) tr.setBackgroundColor(Color.GRAY);else
                tr.setBackgroundColor(Color.BLACK);
            tr.setId(100+count);
            tr.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

//Create two columns to add as table data
            // Create a TextView to add date
            TextView labelDATE = new TextView(this);
            labelDATE.setId(200+count);
            labelDATE.setText(date);
            labelDATE.setPadding(2, 0, 5, 0);
            labelDATE.setTextColor(Color.WHITE);
            tr.addView(labelDATE);
            TextView labelWEIGHT = new TextView(this);
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
        TableRow tr_bottom = new TableRow(this);
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
        TextView textView= new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setTextSize(20);
        textView.setText(text);
        return textView;

    }
}
