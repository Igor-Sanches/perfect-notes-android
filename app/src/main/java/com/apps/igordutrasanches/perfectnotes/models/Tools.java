package com.apps.igordutrasanches.perfectnotes.models;

import android.content.Context;

/**
 * Created by igord on 06/04/2019.
 */

public class Tools extends Keys {

    public Context mContent;

    public Tools(Context context){
        mContent = context;
    }

    public static Tools com(Context context){
        return new Tools(context);
    }

    public void setTextChanged_bool(boolean value){
        set("TextChanged_bool", value, mContent);
    }

    public boolean getTextChanged_bool(){
        return get("TextChanged_bool", false, mContent);
    }

    public void setTextChanged(String value){
        set("TextChanged", value, mContent);
    }

    public String getTextChanged(){
        return get("TextChanged", "", mContent);
    }

    public long getID(){
        return get("ID", -1L, mContent);
    }

    public void setID(long value){
        set("ID", value, mContent);
    }
}
