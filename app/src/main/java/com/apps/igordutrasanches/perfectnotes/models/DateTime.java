package com.apps.igordutrasanches.perfectnotes.models;

import android.content.Context;

import com.apps.igordutrasanches.perfectnotes.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by igord on 16/04/2019.
 */

public class DateTime {

    private ResourceLoader loader = new ResourceLoader();
    private Context context;

    public DateTime(Context context){
        this.context = context;
    }

    public static DateTime Now(Context context){
        return new DateTime(context);
    }

    public String toStrig(){
        SimpleDateFormat dia = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat mes = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat ano = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat hr = new SimpleDateFormat("HH_mm_ss", Locale.getDefault());
        return dia.format(new Date()).toString() + " " + loader.get(context, R.string.of) + " " + getMes(mes.format(new Date()).toString()) + " " + loader.get(context, R.string.of) + " " + ano.format(new Date()).toString() + " " + loader.get(context, R.string.as) + " " + hr.format(new Date()).toString();
    }

    public String toString(String date){
        SimpleDateFormat format = new SimpleDateFormat(date, Locale.getDefault());
        return format.format(new Date());
    }

    private String getMes(String mes) {
        String esteMes = "";
        switch (mes) {
            case "01":
                esteMes = loader.get(context, R.string.data_jan);
                break;
            case "02":
                esteMes = loader.get(context, R.string.data_fev);
                break;
            case "03":
                esteMes = loader.get(context, R.string.data_mai);
                break;
            case "04":
                esteMes = loader.get(context, R.string.data_abr);
                break;
            case "05":
                esteMes = loader.get(context, R.string.data_mar);
                break;
            case "06":
                esteMes = loader.get(context, R.string.data_jun);
                break;
            case "07":
                esteMes = loader.get(context, R.string.data_jul);
                break;
            case "08":
                esteMes = loader.get(context, R.string.data_ago);
                break;
            case "09":
                esteMes = loader.get(context, R.string.data_set);
                break;
            case "10":
                esteMes = loader.get(context, R.string.data_out);
                break;
            case "11":
                esteMes = loader.get(context, R.string.data_nov);
                break;
            case "12":
                esteMes = loader.get(context, R.string.data_dez);
                break;
        }

        return esteMes;
    }
}
