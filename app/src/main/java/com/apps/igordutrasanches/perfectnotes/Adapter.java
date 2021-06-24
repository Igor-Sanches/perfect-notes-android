package com.apps.igordutrasanches.perfectnotes;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.apps.igordutrasanches.perfectnotes.R;
import com.apps.igordutrasanches.perfectnotes.dataBase.Notas;

import java.util.List;

/**
 * Created by igord on 06/04/2019.
 */

public class Adapter extends ArrayAdapter<Notas> {
    private final List<Notas> itens;
    private final Activity mActivity;

    private TextView data, titulo ;
    private ImageView iView;

    public Adapter(Activity activity, List<Notas> list){
        super(activity, R.layout.activity_main, list);
        itens = list;
        mActivity = activity;
    }

    public int getCount(){return itens.size();}

    public long getItemId(int id){
        return itens.get(id).getID();
    }

    public View getView(int id, View view, ViewGroup viewGroup){
        view = mActivity.getLayoutInflater().inflate(R.layout.item_lista, viewGroup, false);

        titulo = (TextView)view.findViewById(R.id.lista_titulo);
        data = (TextView)view.findViewById(R.id.lista_data);
        titulo.setText(itens.get(id).getTitulo());
        data.setText(getData(itens.get(id).getData(), itens.get(id).getDataModificacao()));

        iView = (ImageView)view.findViewById(R.id.lista_item_lock);
        iView.setVisibility(setlock(id));
        return view;
    }

    private int setlock(int id) {
        if(itens.get(id).chave()) return View.VISIBLE;
        else return View.GONE;
    }

    private String getData(String data, String dataModifcacao) {
        String d = "";
        if(!dataModifcacao.equals("")){
            d = " / " + dataModifcacao;
        }
        return data + d;
    }

}