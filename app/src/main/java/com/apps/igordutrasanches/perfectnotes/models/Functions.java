package com.apps.igordutrasanches.perfectnotes.models;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.igordutrasanches.perfectnotes.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by igord on 06/04/2019.
 */

public class Functions {


    public static final int PDF = 0;
    public static final int IMAGE = 1;
    private static ResourceLoader loader = new ResourceLoader();
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void criarJpg(Context context, View view, int width, int height, String titulo){

        try{
            File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/" + loader.get(context, R.string.app_name) + "/" + loader.get(context, R.string.folder_image) + "/");
            if (!file2.exists() && !file2.mkdirs()) {
                //Toast.makeText((Context)this, (CharSequence)this.getResources().getString(2131427377), (int)1).show();
                file2.createNewFile();
            }  String string = titulo.replaceAll("\\p{Punct}", "").trim();
            File file = new File(file2, string + " (" + DateTime.Now(context).toStrig() + ")" + ".jpg");
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Bitmap bitmap = viewToBitmap(view, width, height);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(context, "saved",Toast.LENGTH_LONG).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static File copyFiles(File source, File destino) throws IOException, FileAlreadyExistsException{
       try{ File[] childrem = source.listFiles();
           File temp = null;
           if(childrem != null){
               for(File child : childrem){
                   if(child.isFile() && !child.isHidden()){
                       String lastExc = child.getName().toString();
                       StringBuilder builder = new StringBuilder(lastExc);
                        temp = new File(destino.toString() + "\\" + child.getName().toString());
                       if(child.getName().contains(".")){
                           if(temp.exists()) {
                               temp = new File(destino.toString() + "\\" + builder.replace(lastExc.lastIndexOf("."), lastExc.lastIndexOf("."), "(1)"));
                           } else{
                               temp = new File(destino.toString() + "\\" + child.getName().toString());
                           }
                           builder = new StringBuilder(temp.toString());
                       }else{
                           temp = new File(destino.toString() + "\\" + child.getName().toString());
                       }
                       if(temp.exists()){
                           for(int x = 0; temp.exists(); x++){
                               if(child.getName().contains(".")){
                                   temp = new File(String.valueOf(builder.replace(temp.toString().lastIndexOf(" "), temp.toString().lastIndexOf("."), " (" + x + ")")));

                               }else{
                                   temp = new File(destino.toString() + "\\" + child.getName() + " (" + x + ")");
                               }
                           }
                           Files.copy(child.toPath(), temp.toPath());
                       }
                       else{
                           Files.copy(child.toPath(), temp.toPath());
                       }
                   }
                   else if(child.isDirectory()){
                       copyFiles(child, destino);

                   }
               }
        }
        return temp;
       }catch (Exception e){
           return new File("");
       }
    }

    public static void goSavePdf(Context context, String titulo, String nota){
     try{   Document document = new Document();
        try{
           File file2 = new File(Environment.getExternalStorageDirectory().toString() + "/" + loader.get(context, R.string.app_name) + "/PDF");
            if (!file2.exists() && !file2.mkdirs()) {
                //Toast.makeText((Context)this, (CharSequence)this.getResources().getString(2131427377), (int)1).show();
                file2.createNewFile();
            }
            String string = titulo.replaceAll("\\p{Punct}", "").trim();
           File file = new File(file2, string + " (" + DateTime.Now(context).toStrig() + ")" + ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(new Paragraph(titulo));
            document.add(new Paragraph(nota));
            document.addCreator(loader.get(context, R.string.app_name));
            document.addAuthor("Igor J. Dutra Sanches");
            Toast.makeText(context, "Salvo 1", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
         Toast.makeText(context, "Salvo 2", Toast.LENGTH_LONG).show();
     } catch (Exception x) {
         Toast.makeText(context, x.getMessage(), Toast.LENGTH_LONG).show();
     }
    }

    private static File getDirs() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(file, "Image Demo");
    }

    public static Bitmap viewToBitmap(View view, int widgt, int height){
        Bitmap bitmap = Bitmap.createBitmap(widgt, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static void goCompartilhar(Context context, String titulo, String nota){
       try{ Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, nota);
        intent.setType("text/plain");
        if(titulo.isEmpty()) titulo = "Compartilhar";
        context.startActivity(Intent.createChooser(intent, titulo));
       } catch (Exception x) {
           Toast.makeText(context, x.getMessage(), Toast.LENGTH_LONG).show();
       }
    }

    public static void goWhatsApp(Context context, String titulo, String nota){
       try{
           Intent intent = new Intent();
           intent.setAction(Intent.ACTION_SEND);
           intent.putExtra(Intent.EXTRA_TEXT, nota);
           intent.setType("text/plain");
           intent.setPackage("com.whatsapp");
           if(titulo.isEmpty()) titulo = "Compartilhar";
           context.startActivity(Intent.createChooser(intent, titulo));
       }catch (Exception x){
           Toast.makeText(context, "Vc n possui o whatsaopp instalado", Toast.LENGTH_LONG).show();
       }
    }

    public static void goCopia(Context context, String nota){
        if(!nota.isEmpty()){
            ((ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE)).setText(nota);
            Toast.makeText(context, "Notas Copiado!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Não existe texto para copia!", Toast.LENGTH_LONG).show();
        }
    }

    public static String goSaveFile(Context context, String titulo, String nota) {
        File file = null;
        try {
                  try {
                       file =  new File(Environment.getExternalStorageDirectory().toString() + "/" + loader.get(context, R.string.app_name) + "/" + loader.get(context, R.string.folder_text));
                      if (!file.exists() && !file.mkdirs()) {
                          //Toast.makeText((Context)this, (CharSequence)this.getResources().getString(2131427377), (int)1).show();
                          file.createNewFile();
                      }
                      String string = titulo.replaceAll("\\p{Punct}", "").trim();
                      File file2 = new File(file, string + " (" + DateTime.Now(context).toStrig() + ")" + ".txt");
                      if (file.canWrite()) {
                          BufferedWriter bufferedWriter = new BufferedWriter((Writer) new FileWriter(file2));
                          bufferedWriter.write(nota);
                          bufferedWriter.close();
                          Toast.makeText(context, "salvo" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                      }
                    Toast.makeText(context, "eerrro" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

                } catch (Exception x) {
                    Toast.makeText(context, x.getMessage(), Toast.LENGTH_LONG).show();
                    }
        } catch (Exception x) {
            Toast.makeText(context, x.getMessage(), Toast.LENGTH_LONG).show();
         }
         return file.getAbsolutePath().toString();
    }

    public static void goPrint(Context context, String nota, String font, float fontSize, boolean negrito,  boolean italico){
    try{
        PrintManager printManager = (PrintManager)context.getSystemService(Context.PRINT_SERVICE);
        String name = loader.get(context, R.string.app_name) + " Document";
        printManager.print(name, new MyPrintDocumentAdapter(context, fontSize, font, nota, negrito, italico), null);
    } catch (Exception x) {
        Toast.makeText(context, x.getMessage(), Toast.LENGTH_LONG).show();
    }
    }

    private static class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 1;
        String font, nota;
        float fontSize;
        boolean italico,negrito;

        public MyPrintDocumentAdapter(Context context, float fontSize, String font, String nota, boolean negrito, boolean italico) {
            this.context = context;
            this.nota=nota;this.italico=italico;this.negrito=negrito;this.font=font;this.fontSize=fontSize;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

            // Create a new PdfDocument with the requested page attributes
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;


            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("print_output.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }

        }

        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {


            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i)) {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page) {
            for (int i = 0; i < pageRanges.length; i++) {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }

        private void drawPage(PdfDocument.Page page,
                              int pagenumber) {
            Canvas canvas = page.getCanvas();

            Paint paint1 = new Paint();
            paint1.setStyle(Paint.Style.FILL);
            paint1.setAntiAlias(true);
            paint1.setColor(Color.BLACK);
            paint1.setTextSize(15);


            TextView tv = new TextView(context);
            tv.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(10, 2, 0, 0); // llp.setMargins(left, top, right, bottom);
            tv.setLayoutParams(llp);
            tv.setTextSize(fontSize - 5);

            tv.setText(nota);
            if (negrito && italico)
                tv.setTypeface(Typeface.create(font, Typeface.BOLD_ITALIC));
            else if (negrito)
                tv.setTypeface(Typeface.create(font, Typeface.BOLD));
            else if (italico)
                tv.setTypeface(Typeface.create(font, Typeface.ITALIC));
            else
                tv.setTypeface(Typeface.create(font, Typeface.NORMAL));
            tv.setDrawingCacheEnabled(true);
            tv.measure(View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
            tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
            canvas.drawBitmap(tv.getDrawingCache(), 5, 10, paint1);
            tv.setDrawingCacheEnabled(false);


        }
    }
}


