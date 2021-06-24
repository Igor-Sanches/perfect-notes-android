package com.apps.igordutrasanches.perfectnotes;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
//import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QRCodeFragment extends DialogFragment {

    private View view;
    private Toolbar toolbar;

    private void animateDialogIn(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= 14) {
            View view = dialog.getWindow().getDecorView();
            int n = view.getLeft() + view.getRight();
            int n2 = view.getTop() + view.getBottom();
            Math.max((int) view.getWidth(), (int) view.getHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            Property property = View.X;
            float[] arrf = new float[]{(float) n / 2.0f, 0.0f};
            AnimatorSet.Builder builder = animatorSet.play((Animator) ObjectAnimator.ofFloat((Object) view, (Property) property, (float[]) arrf));
            Property property2 = View.Y;
            float[] arrf2 = new float[]{(float) n / 2.0f, 0.0f};
            AnimatorSet.Builder builder2 = animatorSet.play((Animator) ObjectAnimator.ofFloat((Object) view, (Property) property2, (float[]) arrf2)).with((Animator) ObjectAnimator.ofFloat((Object) view, (Property) View.SCALE_X, (float[]) new float[]{0.0f, 1.0f})).with((Animator) ObjectAnimator.ofFloat((Object) view, (Property) View.SCALE_Y, (float[]) new float[]{0.0f, 1.0f}));
            animatorSet.setDuration(250L);
            animatorSet.setInterpolator((TimeInterpolator) new DecelerateInterpolator());
            animatorSet.setStartDelay(250L);
            animatorSet.start();
        }
    }

    private void animateDialogOut(){
        final Dialog dialog = this.getDialog();
        if (Build.VERSION.SDK_INT >= 21) {
            View view = dialog.getWindow().getDecorView();
            int n = view.getLeft() + view.getRight();
            int n2 = view.getTop() + view.getBottom();
            Math.max((int)view.getWidth(), (int)view.getHeight());
            AnimatorSet animatorSet = new AnimatorSet();
            Property property = View.X;
            float[] arrf = new float[]{0.0f, (float)n / 2.0f};
            AnimatorSet.Builder builder = animatorSet.play((Animator)ObjectAnimator.ofFloat((Object)view, (Property)property, (float[])arrf));
            Property property2 = View.Y;
            float[] arrf2 = new float[]{0.0f, (float)n2 / 2.0f};
            builder.with((Animator)ObjectAnimator.ofFloat((Object)view, (Property)property2, (float[])arrf2)).with((Animator)ObjectAnimator.ofFloat((Object)view, (Property)View.SCALE_X, (float[])new float[]{1.0f, 0.0f})).with((Animator)ObjectAnimator.ofFloat((Object)view, (Property)View.SCALE_Y, (float[])new float[]{1.0f, 0.0f}));
            animatorSet.setDuration(250L);
            animatorSet.setInterpolator((TimeInterpolator)new DecelerateInterpolator());
            animatorSet.addListener(new Animator.AnimatorListener(){

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    dialog.dismiss();
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }
            });
            animatorSet.start();
            return;
        }
        dialog.dismiss();
    }

    private void initDialog(final Dialog dialog){
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                animateDialogIn(dialog);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        this.toolbar = (Toolbar)this.view.findViewById(R.id.qr_toolbar);
        this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_qr_share){

                    try{

                        ImageView image = (ImageView)view.findViewById(R.id.codigo);
                        Bitmap bitmap = viewToBitmap(image, image.getWidth(), image.getHeight());
                        Intent share = new Intent(Intent.ACTION_SEND);
                        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayInputStream);
                        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"/Perfect Notes/.ShareTemp/Codigo.png");
                        try{
                            file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            fileOutputStream.write(byteArrayInputStream.toByteArray());
                        }catch (IOException c){
                            c.printStackTrace();
                        }

                        share.setType("image/*");
                        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.getAbsolutePath()));
                        startActivity(Intent.createChooser(share, "via"));
                    } catch (Exception ee) {
                        Toast.makeText(mContext, ee.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }

                if(item.getItemId() == R.id.action_qr_exit){
                    animateDialogOut();
                    return true;
                }

                return false;
            }
        });


            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            try{
                com.google.zxing.common.BitMatrix bit = multiFormatWriter.encode(QRCode.criarContent(titulo, nota, data, dataM, fonte, fontSize, bold, italic
                ), BarcodeFormat.QR_CODE, 250, 250);
                BarcodeEncoder encoder = new BarcodeEncoder();
                Bitmap codigo = encoder.createBitmap(bit);

                ((ImageView)view.findViewById(R.id.codigo)).setImageBitmap(codigo);
            } catch (com.google.zxing.WriterException e) {
                e.printStackTrace();
            }
        } catch (Exception ee) {
            Toast.makeText(mContext, ee.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap viewToBitmap(View view, int widgt, int height){
        Bitmap bitmap = Bitmap.createBitmap(widgt, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        View view = this.getDialog().getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.setScaleX((View)view, (float)0.0f);
            ViewCompat.setScaleY((View)view, (float)0.0f);
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
       try{
           view = LayoutInflater.from(mContext).inflate(R.layout.fragment_qrcode, null);
           this.setHasOptionsMenu(true);
           AlertDialog.Builder builder = new AlertDialog.Builder((Context)this.getActivity());
           builder.setView(view);
           alertDialog = builder.create();
           initDialog((Dialog)alertDialog);
         } catch (Exception ee) {
           Toast.makeText(mContext, ee.getMessage(), Toast.LENGTH_LONG).show();
       }
       return alertDialog;
    }

    AlertDialog alertDialog;
    public void onCreateOptionsMenu(Menu menu2, MenuInflater menuInflater) {
        this.toolbar.inflateMenu(R.menu.menu_qrcode);
    }

    public void onResume() {
        super.onResume();
    }

    private Context mContext;

    public QRCodeFragment(Context context, String titulo, String nota, String data, String dataM, String font, String fontSize, boolean bold, boolean italic) {
             mContext = context;
            this.titulo =titulo;
            this.nota = nota;
            this.data = data;
            this.dataM = dataM;
            this.fonte = font;
            this.fontSize = fontSize;
            this.bold = bold;
            this.italic = italic;
    }

    private String titulo = "", nota = "", data = "", dataM = "", fonte = "", fontSize = "";
    private boolean bold = false, italic = false;


}
