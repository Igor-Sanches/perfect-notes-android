package com.apps.igordutrasanches.perfectnotes.dataBase;

import android.os.Build;
import android.view.View;

/**
 * Created by igord on 06/04/2019.
 */

public class Notas {

    //Construir itens da nota
    public long id;
    public String titulo;
    public String nota;
    public String data;
    public String dataModifcacao;
    public String senha;
    public float tamnhoDaFonte;
    public boolean italico;
    public boolean negrito;
    public String fonte;
    public String filePath;

    //Retornar se limpos
    public Notas(){
        filePath = "";
        id = 0L;
        titulo = "";
        nota = "";
        data = "";
        dataModifcacao = "";
        senha = "";
        tamnhoDaFonte = 17.0f;
        italico = false;
        negrito = false;
        fonte = "sans-serif";
    }

    //Retorna para lista
    public Notas(long id,
                 String titulo,
                 String nota,
                 String data,
                 String dataModifcacao,
                 String senha,
                 String tamnhoDaFonte,
                 String fonte,
                 String italico,
                 String negrito,
                 String path){

        this.filePath = path;
        this.id = id;
        this.titulo = titulo;
        this.nota = nota;
        this.data = data;
        this.dataModifcacao = dataModifcacao;
        this.senha = senha;
        this.tamnhoDaFonte = fontSize(tamnhoDaFonte);
        this.fonte = fonte;
        this.italico = italico == "true" ? true : false;
        this.negrito = negrito == "true" ? true : false;
    }

    private float fontSize(String tamnhoDaFonte) {
        if(!tamnhoDaFonte.endsWith("f")) tamnhoDaFonte = tamnhoDaFonte + "f";
        float size = Float.valueOf(tamnhoDaFonte);
        return size;
    }

    public String getPath(){ return filePath; }
    public void setPath(String path){filePath = path;}
    public boolean isFile(){
        if (Build.VERSION.SDK_INT >= 9) {
            if (this.filePath != null && !this.filePath.isEmpty()) return true;
            return false;
        }
        if (this.filePath == null || this.filePath.length() == 0) return false;
        return true;  }

    //Tamanho da fonte
    public float getTamnhoDaFonte(){ return tamnhoDaFonte; }
    public void setTamnhoDaFonte(float tamnhoDaFonte){ this.tamnhoDaFonte = tamnhoDaFonte; }

    //Fonte
    public String getFonte(){ return fonte; }
    public void setFonte(String fonte){ this.fonte = fonte; }

    //Italico
    public boolean getItalico(){ return italico; }
    public void setItalico(boolean italico){ this.italico = italico; }

    //Negrito
    public boolean getNegrito(){ return negrito; }
    public void setNegrito(boolean negrito){ this.negrito = negrito; }

    //Adicionar e retorna ID
    public long getID(){ return id; }
    public void setID(long id){ this.id = id; }

    //Adicionar e retorna titulo
    public String getTitulo(){ return titulo; }
    public void setTitulo(String titulo){ this.titulo = titulo; }

    //Adicionar e retorna nota
    public String getNota(){ return nota; }
    public void setNota(String nota){ this.nota = nota; }

    //Adicionar e retorna data
    public String getData(){ return data; }
    public void setData(String nota){ this.nota = data; }
    //Adicionar e retorna data
    public String getDataModificacao(){ return dataModifcacao; }
    public void setDataModifcacao(String dataModifcacao){ this.dataModifcacao = dataModifcacao; }

    //Adicionar e retorna senha
    public String getSenha(){ return senha; }
    public void setSenha(String senha){ this.senha = senha; }

    //Verificar se há senha
    public boolean chave(){
        if (Build.VERSION.SDK_INT >= 9) {
            if (this.senha != null && !this.senha.isEmpty()) return true;
            return false;
        }
        if (this.senha == null || this.senha.length() == 0) return false;
        return true;
    }

}
