package com.apps.igordutrasanches.perfectnotes;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.igordutrasanches.perfectnotes.dataBase.BancoDeDados;
import com.apps.igordutrasanches.perfectnotes.dataBase.Notas;
import com.apps.igordutrasanches.perfectnotes.models.Keys;
import com.apps.igordutrasanches.perfectnotes.models.ReplaceIsBlank;
import com.apps.igordutrasanches.perfectnotes.models.ResourceLoader;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {

    private ListView lista;
    private Notas nota;
    private BancoDeDados dados;
    private Adapter adapter;
    private ResourceLoader loader = new ResourceLoader();
    private String get(int res){
        return loader.get(this, res);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            dados = new BancoDeDados(this);
            nota = new Notas();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            lista = (ListView)findViewById(R.id.lista);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    nota = new BancoDeDados(view.getContext()).getID(id);
                    initEditor(id, nota.getTitulo(), nota.getNota(), nota.getSenha(), nota.getFonte(), nota.getTamnhoDaFonte(), nota.getNegrito() == true ? "on" : "off", nota.getItalico() == true ? "on" : "off", nota.getPath());
                }
            });
            final Activity activity = this;
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
               IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(get(R.string.code_scan));
                integrator.setCameraId(0);
                integrator.initiateScan();
                }
            });
            listRefress();
        }catch (Exception x){
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
        verficarPermissoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listRefress();
    }

    private void listRefress(){
        try{
            adapter = new Adapter(this, dados.lista(Keys.get("sort", 1, this)));
            lista.setAdapter(adapter);

        }catch (Exception x){
            Toast.makeText((Context)this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String dados= "";
        if (requestCode == 101) {

            try{
                String caminho = data.getData().getPath();
                String nome = caminho.substring(caminho.lastIndexOf("/") + 1);
                caminho = caminho.substring(caminho.lastIndexOf(":") + 1);
                     File myfile = new File(Environment.getExternalStorageDirectory() + "/" + caminho);
                    FileInputStream input= new FileInputStream(myfile);
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                    String dataRow = "";
                     while ((dataRow = buffer.readLine()) != null){
                         dados += dataRow + "\n";
                    }
                    buffer.close();
                initEditor(-1L, nome.replace(".txt", ""), dados, "", "", 17.0f, "off", "off", myfile.getAbsolutePath());
            }catch (Exception x){
              Toast.makeText((Context)this, x.getMessage(), Toast.LENGTH_LONG).show();
          }
        }

        if(result != null){
            if(result.getContents() != null){
                String contents = result.getContents();
                if (QRCode.isContents(contents)) {
                    initEditor(-1L, QRCode.getTitulo(contents), QRCode.getNota(contents), "", QRCode.getFonte(contents), QRCode.getTamanhoDaFonte(contents), QRCode.getNegrito(contents) == true ? "on" : "off", QRCode.getItalico(contents) == true ? "on" : "off", "");
                } else {
                    initEditor(-1L, "", QRCode.getNota(contents), "", "", 17.0f, "off", "off", "");
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initEditor(long id, String titulo, String nota, String senha, String fontFamily, float fontSize, String bold, String italic, String path){
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("titulo", ReplaceIsBlank.getTituto(this, titulo));
        intent.putExtra("nota", nota);
        intent.putExtra("fontFamily", ReplaceIsBlank.getFonte(fontFamily));
        intent.putExtra("fontSize", fontSize);
        intent.putExtra("bold", bold);
        intent.putExtra("italic", italic);
        intent.putExtra("senha", senha);
        intent.putExtra("path", path);
        intent.putExtra("NOTE_ID", id);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_adicionar){
            initEditor(-1L, "", "", "", "", 17.0f, "off", "off", "");
        }
        if(id == R.id.action_buscar_arquivo){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/plain");
            startActivityForResult(intent, 101);
        }
        return super.onOptionsItemSelected(item);
    }

    private void verficarPermissoes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 555);

            } catch (Exception x) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandPermission) {
        if (requestCode == 555 && grandPermission[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            verficarPermissoes();
        }
    }
}
