package com.ultimatesoftil.citron.models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ultimatesoftil.citron.util.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Mike on 17/08/2018.
 */

public class PdfDocument extends AsyncTask<Void,Void,String> {
     OnPdfUpdateListener listener;
     Client client;
     ArrayList<Order>orders;
    public interface OnPdfUpdateListener {
        public void OnDownloadDeckFinish(String Response);
    }


    public PdfDocument(OnPdfUpdateListener listener, Client client, ArrayList<Order> orders) {
         this.client=client;
         this.orders=orders;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String str = "מייצא מסמך אנא המתן...";
        Log.d("creating","doc");

        try {
            Document document = new Document(PageSize.A4);
            // step 2
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStorageDirectory()+"/salesmanager/"+client.getName()+".pdf"));
            // step 3
            document.open();
            // step 4
            BaseFont bf = BaseFont.createFont(
                    Environment.getExternalStorageDirectory()+"/salesmanager/ARIALUNI.TTF", BaseFont.IDENTITY_H, true);
            Font font = new Font(bf, 14);

            ColumnText column = new ColumnText(writer.getDirectContent());
            column.setSimpleColumn(36, 770, 569, 36);
            column.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            column.addElement(new Paragraph("שם הלקוח: "+client.getName()+"\n"+  "טלפון: " +client.getPhone(), font));



            // step 5
            PdfPTable table=new PdfPTable(new float[] {1, 1, 1,1,1 });
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell cell1 = new PdfPCell(new Phrase("מצב",font));
            cell1.setBackgroundColor(BaseColor.GRAY);
            cell1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            PdfPCell cell2 = new PdfPCell(new Phrase("מחיר",font));
            cell2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            cell2.setBackgroundColor(BaseColor.GRAY);
            PdfPCell cell3 = new PdfPCell(new Phrase("סוג",font));
            cell3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            cell3.setBackgroundColor(BaseColor.GRAY);
            PdfPCell cell4 = new PdfPCell(new Phrase("תאריך",font));
            cell4.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            cell4.setBackgroundColor(BaseColor.GRAY);
            PdfPCell cell5 = new PdfPCell(new Phrase("מס'",font));
            cell5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            cell5.setBackgroundColor(BaseColor.GRAY);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            column.addElement(Chunk.NEWLINE);
            column.addElement(Chunk.NEWLINE);
            column.addElement(Chunk.NEWLINE);
            column.addElement(table);
            for(int i=0;i<orders.size();i++){
               for (int j=0;j<orders.get(i).getProducts().size();j++){
                   String s=getprStatus(orders.get(i).getProducts().get(j));
                   cell1 = new PdfPCell(new Phrase(s,font));

                   cell1.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                   cell2 = new PdfPCell(new Phrase(String.valueOf(orders.get(i).getProducts().get(j).getPrice()),font));
                   cell2.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                   cell3 = new PdfPCell(new Phrase(getKind(orders.get(i).getProducts().get(j)),font));
                   cell3.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                   cell4 = new PdfPCell(new Phrase(String.valueOf(Utils.FormatMillis(orders.get(i).getProducts().get(j).getTime())),font));
                   cell4.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                   cell5 = new PdfPCell(new Phrase(String.valueOf(i+j+1),font));
                   cell5.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

                   table.addCell(cell1);
                   table.addCell(cell2);
                   table.addCell(cell3);
                   table.addCell(cell4);
                   table.addCell(cell5);
               }


            }

            column.go();



            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return str;
    }

    private String getKind(Product product) {
     String s=null;
       switch (product.getKind()){
           case "0":
               s="אתרוג";
               break;

           case"1":
                s="לולב";
               break;
       }
    return s;
    }

    private String getprStatus(Product product) {
     String s=null;
     switch (product.getStatus()){
         case 0:
             s="לא נבחר";
             break;
         case 1:
             s="נשלח לבדיקה";
             break;
         case 2:
            s="שולם"     ;
             break;
         case 3:
             s=String.valueOf(product.getDue()+"חוב");
             break;


     }
     Log.d("status",String.valueOf(product.getStatus()));
     Log.d("status",s);
    return s;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if( listener != null ) {
            listener.OnDownloadDeckFinish(result);
        }
    }
    private Paragraph getCellParagraph() {
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        // set other styles you need like custom font
        return paragraph;
    }
    private PdfPCell getPdfPCellNoBorder(Paragraph paragraph) {
        PdfPCell cell = new PdfPCell();
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        cell.setPaddingBottom(8);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.addElement(paragraph);
        return cell;
    }
}
