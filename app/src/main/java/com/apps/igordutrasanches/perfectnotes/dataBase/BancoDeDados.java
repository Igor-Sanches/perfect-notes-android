package com.apps.igordutrasanches.perfectnotes.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.apps.igordutrasanches.perfectnotes.R;
import com.apps.igordutrasanches.perfectnotes.models.ResourceLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by igord on 06/04/2019.
 */

public class BancoDeDados extends SQLiteOpenHelper {

    public static final String FABIA = "fabia";
    public static final String ID = "rowid";
    public static final String TABELA = "hagata";
    public static final String TITULO = "titulo";
    public static final String NOTA = "nota";
    public static final String DATA = "data";
    public static final String DATA_MODIFICACAO = "dataModificacao";
    public static final String SENHA = "senha";
    public static final String FONTE = "fonte";
    public static final String FONTE_SIZE = "fontesize";
    public static final String ITALICO = "italico";
    public static final String NEGRITO = "negrito";
    public static final String PATH = "path";
    private Context context;
    private ResourceLoader loader = new ResourceLoader();

    private static final String[] dados = new String[]{ID, TITULO, NOTA, DATA, DATA_MODIFICACAO, SENHA, FONTE_SIZE, FONTE, ITALICO, NEGRITO, PATH};

    public BancoDeDados(Context context){
        super(context, FABIA, null, 3); this.context = context;
    }

    public long getID(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT count(*) FROM " + TABELA, null);
        long id = 0L;
        if(cursor.moveToFirst()){
            do {
                id = cursor.getLong(0);
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return id;
    }

    public Notas getID(long id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Notas nota;
        String[] isDados = dados;;
        String[] isDadosDB = new String[]{String.valueOf(id)};
        Cursor cursor = sqLiteDatabase.query(TABELA, isDados, ID + " = ?", isDadosDB, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            nota = new Notas(cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10));
            cursor.close();
        }
        else{
            nota = null;
        }
        sqLiteDatabase.close();
        return nota;
    }

    public List<Notas> lista(int position){
        SQLiteDatabase sqLiteDatabase;
        String ordem = null;
        String por = null;
        Cursor cursor;
        LinkedList lista = new LinkedList();
        switch (position){
            case 1:
                por = TITULO;
                ordem = "ASC";
                break;
            case 2:
                por = DATA;
                ordem = "DESC";
                break;
            case 3:
                por = SENHA;
                ordem = "DESC";
                break;
        }
        if((cursor = (sqLiteDatabase = this.getWritableDatabase()).rawQuery("SELECT rowid,* FROM " + TABELA + " ORDER BY " + por + " " + ordem, null)).moveToFirst()){
            do{
                lista.add(new Notas(cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getString(10)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return lista;
    }

    public void adicionar(Notas nota){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(TITULO, nota.getTitulo());
        valores.put(NOTA, nota.getNota());
        valores.put(DATA, nota.getData());
        valores.put(DATA_MODIFICACAO, nota.getDataModificacao());
        valores.put(SENHA, nota.getSenha());
        valores.put(FONTE_SIZE, nota.getTamnhoDaFonte());
        valores.put(FONTE, nota.getFonte());
        valores.put(ITALICO, nota.getItalico());
        valores.put(NEGRITO, nota.getNegrito());
        valores.put(PATH, nota.getPath());
        sqLiteDatabase.insert(TABELA, null, valores);
        sqLiteDatabase.close();
        Toast.makeText(context, loader.get(context, R.string.note_saved), Toast.LENGTH_SHORT).show();
    }

    public void atualizar(Notas nota){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(TITULO, nota.getTitulo());
        valores.put(NOTA, nota.getNota());
        valores.put(DATA, nota.getData());
        valores.put(DATA_MODIFICACAO, nota.getDataModificacao());
        valores.put(SENHA, nota.getSenha());
        valores.put(FONTE_SIZE, nota.getTamnhoDaFonte());
        valores.put(FONTE, nota.getFonte());
        valores.put(ITALICO, nota.getItalico());
        valores.put(NEGRITO, nota.getNegrito());
        valores.put(PATH, nota.getPath());
        String[] id = new String[]{String.valueOf(nota.getID())};
        sqLiteDatabase.update(TABELA, valores, ID + " = ?", id);
        sqLiteDatabase.close();
        Toast.makeText(context, loader.get(context, R.string.note_update), Toast.LENGTH_SHORT).show();
    }

    public void apagar(long id){
        SQLiteDatabase sql = this.getReadableDatabase();
        String[] ids = new String[]{String.valueOf(id)};
        sql.delete(TABELA, ID + " = ?", ids);
        sql.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "
                + TABELA + " ("
                + TITULO + " TEXT, "
                + NOTA + " TEXT, "
                + DATA + " TEXT, "
                + DATA_MODIFICACAO + " TEXT, "
                + SENHA + " TEXT, "
                + FONTE_SIZE + " TEXT, "
                + FONTE + " TEXT, "
                + ITALICO + " TEXT, "
                + NEGRITO  + " TEXT, "
                + PATH + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if(i == 2 && i1 == 3){
            sqLiteDatabase.execSQL("ALTER TABLE " + TABELA + " ADD COLUMN " + SENHA + " TEXT;");
        }
    }
}
