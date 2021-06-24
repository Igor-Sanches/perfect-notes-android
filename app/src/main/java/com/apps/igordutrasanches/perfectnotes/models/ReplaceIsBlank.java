package com.apps.igordutrasanches.perfectnotes.models;

import android.content.Context;

import com.apps.igordutrasanches.perfectnotes.R;

/**
 * Created by igord on 14/04/2019.
 */

public class ReplaceIsBlank {

    private static ResourceLoader loader = new ResourceLoader();

    public static String getTituto(Context c, String txt){
        if(txt.isEmpty()) return loader.get(c, R.string.new_text);
        else return txt;
    }
    public static String getFonte(String txt){
        if(txt.isEmpty()) return "sans-serif";
        else return txt;
    }
}
