package com.apps.igordutrasanches.perfectnotes.models;

import android.content.Context;

/**
 * Created by igord on 07/04/2019.
 */

public class ResourceLoader {
    public String get(Context context, int res){
        return context.getString(res);
    }
    public int get(int res){
        return res;
    }
}
