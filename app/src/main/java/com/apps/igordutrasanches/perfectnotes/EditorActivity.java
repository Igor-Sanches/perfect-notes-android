package com.apps.igordutrasanches.perfectnotes;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.igordutrasanches.perfectnotes.dataBase.BancoDeDados;
import com.apps.igordutrasanches.perfectnotes.dataBase.Notas;
import com.apps.igordutrasanches.perfectnotes.models.DateTime;
import com.apps.igordutrasanches.perfectnotes.models.ReplaceIsBlank;
import com.apps.igordutrasanches.perfectnotes.models.Functions;
import com.apps.igordutrasanches.perfectnotes.models.ResourceLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class EditorActivity extends AppCompatActivity {

    private long id;
    private float fonteSize = 17.0f;

    private String data_modificacao = "", fontFamily = "sans-serif";
    private boolean italico = false, negrito = false;
    private String filePath ="", isTextEdites = "", key = "";
    private Notas nota = null;
    private BancoDeDados db = null;
    private EditText txt_nota;
    private Toolbar barraDeFerramentas, barraDoApp;
    private AlertDialog alertDialog;
    private MenuItem
            print,
            lock,
            unlock;
    private TextToSpeech locutor;
    private Dialog dialog;
    private ResourceLoader loader = new ResourceLoader();
    private View viewDialogs;
    private Context mContext;
    private boolean isSaved = false, fileExplorer = false, isNewNote = false, isEdited = false;
    private Animation toRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        try{
            verficarPermissoes();
            mContext = this;
            txt_nota = (EditText) findViewById(R.id.nota_editor);
            barraDeFerramentas = (Toolbar) findViewById(R.id.ferramentas);
            barraDoApp = (Toolbar) findViewById(R.id.toolbar_editor);
            barraDeFerramentas.inflateMenu(R.menu.menu_editor);
            toRight = AnimationUtils.loadAnimation(this, R.anim.do_lado_direito);
            barraDeFerramentas.setAnimation(toRight);
            db = new BancoDeDados(this);
            barraDeFerramentas.setOnMenuItemClickListener(itemClicked);
            id = getIntent().getExtras().getLong("NOTE_ID");
            if(getIntent().getAction() != null){
                if(getIntent().getAction().equals("android.intent.action.VIEW")){
                    String caminho = getIntent().getData().getPath();
                    String nome = caminho.substring(caminho.lastIndexOf("/") + 1);
                    caminho = caminho.substring(caminho.lastIndexOf(":") + 1);
                    try{
                        File myfile = new File(caminho);//Environment.getExternalStorageDirectory() + "/" + caminho);
                        FileInputStream input= new FileInputStream(myfile);
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
                        String dataRow = "";
                        String aBuffer ="";
                        while ((dataRow = buffer.readLine()) != null){
                            aBuffer += dataRow + "\n";
                        }
                        initEditor(ReplaceIsBlank.getTituto(this, nome.replace(".txt", "")), aBuffer, "", ReplaceIsBlank.getFonte(""), 17.0f, false, false, myfile.getAbsolutePath());
                        fileExplorer = true;
                        buffer.close();

                    }catch (Exception x){
                        Toast.makeText((Context)this, x.getMessage(), Toast.LENGTH_LONG).show();
                    }


                }
            }else {
                initEditor(getIntent().getExtras().getString("titulo"),
                        getIntent().getExtras().getString("nota"),
                        getIntent().getExtras().getString("senha"),
                        getIntent().getExtras().getString("fontFamily"),
                           getIntent().getExtras().getFloat("fontSize"),
                           getIntent().getExtras().getString("bold") == "on" ? true : false,
                           getIntent().getExtras().getString("italic") == "on" ? true : false,
                        getIntent().getExtras().getString("path"));
                fileExplorer = false;
            }
            locutorLoader();
            txt_nota.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().isEmpty()) isEdited = false;
                    else isEdited = true;
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            //barraDoApp.setTitle(loader.get(this, R.string.new_text));
            setSupportActionBar(barraDoApp);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
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

    private void initEditor(String titulo, String nota, String senha, String fontFamily, float fontSize, boolean bold, boolean italic, String path) {
        filePath = path;
        txt_nota.setText(nota);
        key = senha;
        barraDoApp.setTitle(titulo);
        italico = italic;
        negrito = bold;
        this.fontFamily = fontFamily;
        fonteSize = fontSize;
        isTextEdites = txt_nota.getText().toString();
        textStyle(italico, negrito, fontFamily, fonteSize);
    }

    private boolean isText() {
        if (!txt_nota.getText().toString().equals(""))
            return true;
        else {
            Toast.makeText(this, loader.get(this, R.string.text_limpo), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean printSupport() {
        if (PrintHelper.systemSupportsPrint()) return true;
        else return false;
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        try {
            locutorLoader();

        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            if (!isEdited && !isTextEdites.equals(txt_nota.getText().toString())) {
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle(R.string.app_name);
                alertDialog.setMessage(loader.get(this, R.string.dialog_salve_edit));
                alertDialog.setButton(BUTTON_POSITIVE, loader.get(this, R.string.dialog_salve_btn_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        salvarDados();
                        alertDialog.dismiss();
                            finish();
                    }
                });
                alertDialog.setButton(BUTTON_NEGATIVE, loader.get(this, R.string.dialog_salve_ecluir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEUTRAL, loader.get(this, R.string.dialog_save_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            } else super.onBackPressed();
        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void salvarDados() {
        try {
            nota = new Notas();
            db = new BancoDeDados(this);
            nota.setTitulo(barraDoApp.getTitle().toString());
            nota.setNota(txt_nota.getText().toString());
            nota.setSenha(key);
            nota.setNegrito(negrito);
            nota.setItalico(italico);
            nota.setFonte(fontFamily);
            nota.setTamnhoDaFonte(fonteSize);
            nota.setDataModifcacao("");
            nota.setPath(filePath);
            if (id == -1L) {
                nota.setData(DateTime.Now(this).toStrig());
                db.adicionar(nota);
                id = db.getID();
            } else {
                nota.setDataModifcacao(DateTime.Now(this).toStrig());
                db.atualizar(nota);
            }

            if(filePath != null || !filePath.isEmpty()){
                fileUpadate();
            }

            isSaved = true;
            isTextEdites = txt_nota.getText().toString();
        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void fileUpadate() {
        try{
            File file = new File(filePath);
            FileWriter writer = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(txt_nota.getText().toString());
            bw.flush();
            bw.close();
        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.ic_acao_print:
                if (isText())
                    Functions.goPrint(mContext,  txt_nota.getText().toString(), fontFamily, fonteSize, negrito, italico);
                break;
            case R.id.ic_acao_save_pdf:
              try{  if (isText())
                    Functions.goSavePdf(this, barraDoApp.getTitle().toString(), txt_nota.getText().toString());
              } catch (Exception x) {
                  Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
              }
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.ic_acao_Copia:
                if (isText())
                    Functions.goCopia(this, txt_nota.getText().toString());
                break;
            case R.id.ic_acao_salvar:
                if (isText())
                    salvarDados();
                break;
            case R.id.ic_acao_salvar_como:
                if (isText())
                    filePath = Functions.goSaveFile(mContext, barraDoApp.getTitle().toString(), txt_nota.getText().toString());
                break;
            case R.id.ic_acao_compartilhar:
                try {
                    if (isText())
                        Functions.goCompartilhar(this, barraDoApp.getTitle().toString(), txt_nota.getText().toString());
                } catch (Exception x) {
                    Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ic_acao_replace:
                if (isText()) {
                    try {
                        dialog = new Dialog(this);
                        viewDialogs = View.inflate(this, R.layout.dialog_replace, null);
                        dialog.setContentView(viewDialogs);
                        final TextInputEditText antiga = (TextInputEditText) viewDialogs.findViewById(R.id.replace_old);
                        final TextInputEditText nova = (TextInputEditText) viewDialogs.findViewById(R.id.replace_new);
                        final TextView errorReplace = (TextView) viewDialogs.findViewById(R.id.replace_error);
                        AppCompatButton btn_replace = (AppCompatButton) viewDialogs.findViewById(R.id.replace_btn_replace);
                        AppCompatButton btn_cancel = (AppCompatButton) viewDialogs.findViewById(R.id.replace_btn_exit);
                        btn_replace.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!antiga.getText().toString().isEmpty()) {

                                    if (txt_nota.getText().toString().contains(antiga.getText().toString())) {
                                        txt_nota.setText(txt_nota.getText().toString().replace(antiga.getText().toString(), nova.getText().toString()));
                                        dialog.dismiss();
                                        Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_replace_modificado), Toast.LENGTH_LONG).show();
                                    } else {
                                        errorReplace.setText(loader.get(view.getContext(), R.string.replace_error_not_container) + " " + antiga.getText().toString());
                                        errorReplace.setTextColor(Color.RED);
                                    }

                                } else {
                                    errorReplace.setText(loader.get(view.getContext(), R.string.dialog_replace_error));
                                    errorReplace.setTextColor(Color.RED);
                                }
                            }
                        });
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();

                    } catch (Exception x) {
                        Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.ic_acao_bloquear:
                try {
                    dialog = new Dialog(this);
                    viewDialogs = View.inflate(this, R.layout.dialog_bloquear_nota, null);
                    dialog.setContentView(viewDialogs);
                    final TextInputEditText newkey = (TextInputEditText) viewDialogs.findViewById(R.id.lock_key);
                    final TextView errorkey = (TextView) viewDialogs.findViewById(R.id.lock_error);
                    AppCompatButton btn_lock = (AppCompatButton) viewDialogs.findViewById(R.id.dialog_btn_lock);
                    AppCompatButton btn_cancel = (AppCompatButton) viewDialogs.findViewById(R.id.dialog_lock_btn_exit);
                    btn_lock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (newkey.getText().toString().isEmpty()) {
                                errorkey.setText(loader.get(view.getContext(), R.string.dialog_senha_lock_blank));
                                errorkey.setTextColor(Color.RED);
                            } else if(newkey.getText().toString().startsWith(" ")){
                                errorkey.setText(loader.get(view.getContext(), R.string.dialog_senha_lock_space));
                                errorkey.setTextColor(Color.RED);
                            } else {
                                key = newkey.getText().toString();
                                btn_lock();
                                dialog.dismiss();
                                Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_senha_modificado), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } catch (Exception x) {
                    Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ic_acao_desbloquear:
                try {
                    dialog = new Dialog(this);
                    viewDialogs = View.inflate(this, R.layout.dialog_desbloquear_nota, null);
                    dialog.setContentView(viewDialogs);
                    dialog.show();
                    final TextInputEditText newunkey = (TextInputEditText) viewDialogs.findViewById(R.id.unlock_key);
                    final TextView errorunkey = (TextView) viewDialogs.findViewById(R.id.unlock_error_text);
                    AppCompatButton btn_unlock = (AppCompatButton) viewDialogs.findViewById(R.id.dialog_btn_unlock);
                    AppCompatButton btn_uncancel = (AppCompatButton) viewDialogs.findViewById(R.id.dialog_unlock_btn_exit);
                    btn_unlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!newunkey.getText().toString().isEmpty()) {
                                if (newunkey.getText().toString().equals(key)) {
                                    key = "";
                                    dialog.dismiss();
                                    Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_senha_un_modificado), Toast.LENGTH_LONG).show();
                                } else {
                                    errorunkey.setText(loader.get(view.getContext(), R.string.unlock_error));
                                    errorunkey.setTextColor(Color.RED);
                                }

                                btn_lock();
                            } else {
                                Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_senha_un_blank_modificado), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    btn_uncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception x) {
                    Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ic_acao_edit:
                try {
                    dialog = new Dialog(this);
                    viewDialogs = View.inflate(this, R.layout.dialog_add_title, null);
                    dialog.setContentView(viewDialogs);
                    dialog.show();
                    final TextInputEditText title = (TextInputEditText) viewDialogs.findViewById(R.id.add_title_text);
                    final boolean istitle = barraDoApp.getTitle().toString() == loader.get(this, R.string.new_text) ? true : false;
                    if (istitle) title.setHint(barraDoApp.getTitle().toString());
                    else title.setText(barraDoApp.getTitle().toString());
                    AppCompatButton btn_add = (AppCompatButton) viewDialogs.findViewById(R.id.dialog_add_title);
                    AppCompatButton btn_titlecancel = (AppCompatButton) viewDialogs.findViewById(R.id.ldialog_title_btn_exit);
                    btn_add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!title.getText().toString().isEmpty()) {

                                if (title.getText().toString().contains("|"))
                                    title.setText(title.getText().toString().replace("|", ""));
                                if (title.getText().toString().startsWith(" "))
                                    title.setText(title.getText().toString().replace(" ", ""));

                                title.setText(title.getText().toString().replaceAll("\\p{Punct}", "").trim());

                                if (!title.getText().toString().isEmpty()) {
                                    barraDoApp.setTitle(title.getText().toString());
                                    dialog.dismiss();
                                } else {
                                    if (!istitle)
                                        Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_title_add_error), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            } else {
                                if (!istitle)
                                    Toast.makeText(view.getContext(), loader.get(view.getContext(), R.string.dialog_title_add_error), Toast.LENGTH_LONG).show();
                                dialog.dismiss();

                            }
                        }
                    });
                    btn_titlecancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception x) {
                    Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ic_acao_apagar:
                final AlertDialog delete = new AlertDialog.Builder(this).create();
                delete.setTitle(loader.get(this, R.string.dialog_main_delete));
                delete.setMessage(loader.get(this, R.string.delete_note));
                delete.setButton(BUTTON_POSITIVE, loader.get(this, R.string.dialog_main_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (id != -1L) {
                            db = new BancoDeDados(mContext);
                            db.apagar(id);
                        }
                        delete.dismiss();
                        finish();
                    }
                });
                delete.setButton(BUTTON_NEGATIVE, loader.get(this, R.string.btn_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete.dismiss();
                    }
                });
                delete.show();
                break;
            case R.id.ic_acao_qr_share:
                try {
                    if (isText()) {
                        if (txt_nota.getText().toString().length() >= 2550) {
                            Toast.makeText(this, loader.get(this, R.string.qr_error), Toast.LENGTH_LONG).show();
                        } else {
                            QRCodeFragment qrCodeFragment = new QRCodeFragment(this, barraDoApp.getTitle().toString(), txt_nota.getText().toString(), DateTime.Now(this).toStrig(), data_modificacao, fontFamily, String.valueOf(fonteSize), negrito, italico);
                            qrCodeFragment.show(getSupportFragmentManager(), "incluir_fragment");
                        }
                    }
                } catch (Exception x) {
                }
                break;
            case R.id.ic_acao_zap_share:
                if (isText()) {
                    Functions.goWhatsApp(this, barraDoApp.getTitle().toString(), txt_nota.getText().toString());
                    break;

                }

        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor_top, menu);
        print = (MenuItem) menu.findItem(R.id.ic_acao_print);
        lock = (MenuItem) menu.findItem(R.id.ic_acao_bloquear);
        unlock = (MenuItem) menu.findItem(R.id.ic_acao_desbloquear);
        if (!printSupport()) print.setVisible(false);
        btn_lock();
        return true;
    }

    private void btn_lock() {
        try {
            if (key.equals("")) {
                lock.setVisible(true);
                unlock.setVisible(false);
            } else {
                unlock.setVisible(true);
                lock.setVisible(false);

            }
        } catch (Exception x) {
            Toast.makeText(this, x.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener clickFontSize = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.font_size_default:
                    fonteSize = 17.0f;
                    break;
                case R.id.font_size_10:
                    fonteSize = 10.0f;
                    break;
                case R.id.font_size_12:
                    fonteSize = 12.0f;
                    break;
                case R.id.font_size_14:
                    fonteSize = 14.0f;
                    break;
                case R.id.font_size_18:
                    fonteSize = 18.0f;
                    break;
                case R.id.font_size_22:
                    fonteSize = 22.0f;
                    break;
                case R.id.font_size_28:
                    fonteSize = 28.0f;
                    break;
                case R.id.font_size_32:
                    fonteSize = 32.0f;
                    break;
                case R.id.font_size_36:
                    fonteSize = 36.0f;
                    break;
                case R.id.font_size_40:
                    fonteSize = 40.0f;
                    break;
                case R.id.font_size_48:
                    fonteSize = 48.0f;
                    break;
                case R.id.font_size_56:
                    fonteSize = 56.0f;
                    break;
                case R.id.font_size_60:
                    fonteSize = 60.0f;
                    break;
            }

            textStyle(italico, negrito, fontFamily, EditorActivity.this.fonteSize);
            dialog.dismiss();
        }
    };

    private View.OnClickListener clickFontFamily = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.font_family_sansserif:
                    fontFamily = "sans-serif";
                    break;
                case R.id.font_family_sansserifcondensed:
                    fontFamily = "sans-serif-condensed";
                    break;
                case R.id.font_family_serif:
                    fontFamily = "serif";
                    break;
                case R.id.font_family_monospace:
                    fontFamily = "monospace";
                    break;
                case R.id.font_family_serifmonospace:
                    fontFamily = "serif-monospace";
                    break;
                case R.id.font_family_casual:
                    fontFamily = "casual";
                    break;
                case R.id.font_family_cursive:
                    fontFamily = "cursive";
                    break;
                case R.id.font_fontfamily_sansserifsmallcaps:
                    fontFamily = "sans-serif-smallcaps";
                    break;
            }

            textStyle(italico, negrito, fontFamily, fonteSize);
            dialog.dismiss();
        }
    };

    private Toolbar.OnMenuItemClickListener itemClicked = new Toolbar.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {

                case R.id.ic_acao_font_size:
                    dialog = new Dialog(mContext);
                    viewDialogs = View.inflate(mContext, R.layout.dialog_fontes_size, null);
                    dialog.setContentView(viewDialogs);
                    Button btn_sizedefault = (Button) viewDialogs.findViewById(R.id.font_size_default);
                    Button btn_size10 = (Button) viewDialogs.findViewById(R.id.font_size_10);
                    Button btn_size12 = (Button) viewDialogs.findViewById(R.id.font_size_12);
                    Button btn_size14 = (Button) viewDialogs.findViewById(R.id.font_size_14);
                    Button btn_size18 = (Button) viewDialogs.findViewById(R.id.font_size_18);
                    Button btn_size22 = (Button) viewDialogs.findViewById(R.id.font_size_22);
                    Button btn_size24 = (Button) viewDialogs.findViewById(R.id.font_size_24);
                    Button btn_size28 = (Button) viewDialogs.findViewById(R.id.font_size_28);
                    Button btn_size32 = (Button) viewDialogs.findViewById(R.id.font_size_32);
                    Button btn_size36 = (Button) viewDialogs.findViewById(R.id.font_size_36);
                    Button btn_size40 = (Button) viewDialogs.findViewById(R.id.font_size_40);
                    Button btn_size48 = (Button) viewDialogs.findViewById(R.id.font_size_48);
                    Button btn_size56 = (Button) viewDialogs.findViewById(R.id.font_size_56);
                    Button btn_size60 = (Button) viewDialogs.findViewById(R.id.font_size_60);
                    btn_sizedefault.setOnClickListener(clickFontSize);
                    btn_size10.setOnClickListener(clickFontSize);
                    btn_size12.setOnClickListener(clickFontSize);
                    btn_size14.setOnClickListener(clickFontSize);
                    btn_size18.setOnClickListener(clickFontSize);
                    btn_size22.setOnClickListener(clickFontSize);
                    btn_size24.setOnClickListener(clickFontSize);
                    btn_size28.setOnClickListener(clickFontSize);
                    btn_size32.setOnClickListener(clickFontSize);
                    btn_size36.setOnClickListener(clickFontSize);
                    btn_size40.setOnClickListener(clickFontSize);
                    btn_size48.setOnClickListener(clickFontSize);
                    btn_size56.setOnClickListener(clickFontSize);
                    btn_size60.setOnClickListener(clickFontSize);
                    dialog.show();
                    break;

                case R.id.ic_acao_fonte_family:
                    try {
                        dialog = new Dialog(mContext);
                        viewDialogs = View.inflate(mContext, R.layout.dialog_fontes_family, null);
                        dialog.setContentView(viewDialogs);
                        Button btn_sansserif = (Button) viewDialogs.findViewById(R.id.font_family_sansserif);
                        Button btn_sansserifcondensed = (Button) viewDialogs.findViewById(R.id.font_family_sansserifcondensed);
                        Button btn_serif = (Button) viewDialogs.findViewById(R.id.font_family_serif);
                        Button btn_monospace = (Button) viewDialogs.findViewById(R.id.font_family_monospace);
                        Button btn_serifmonospace = (Button) viewDialogs.findViewById(R.id.font_family_serifmonospace);
                        Button btn_casual = (Button) viewDialogs.findViewById(R.id.font_family_casual);
                        Button btn_cursive = (Button) viewDialogs.findViewById(R.id.font_family_cursive);
                        Button btn_sansserifsmallcaps = (Button) viewDialogs.findViewById(R.id.font_fontfamily_sansserifsmallcaps);
                        btn_sansserif.setOnClickListener(clickFontFamily);
                        btn_sansserifcondensed.setOnClickListener(clickFontFamily);
                        btn_serif.setOnClickListener(clickFontFamily);
                        btn_monospace.setOnClickListener(clickFontFamily);
                        btn_serifmonospace.setOnClickListener(clickFontFamily);
                        btn_casual.setOnClickListener(clickFontFamily);
                        btn_cursive.setOnClickListener(clickFontFamily);
                        btn_sansserifsmallcaps.setOnClickListener(clickFontFamily);
                        dialog.show();
                    } catch (Exception x) {
                        Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.iditor_narrador: //Ativado
                    txt_nota.setText(String.valueOf(id));
                    if (isText())
                        iniciarNarrador();
                    break;
                //Aliamento esquerdo
                case R.id.iditor_align_left:
                    //loadAlign("left");
                    break;

                //Aliamento central
                case R.id.iditor_align_center: //Desativado
                    //loadAlign("center");
                    break;

                //Aliamento direito
                case R.id.iditor_align_right: //Desativado
                    //loadAlign("right");
                    break;

                //Bold
                case R.id.iditor_bold: //Ativado
                    if (negrito) negrito = false;
                    else negrito = true;
                    textStyle(italico, negrito, fontFamily, fonteSize);
                    break;

                //Italico
                case R.id.iditor_italic:
                    if (italico) italico = false;
                    else italico = true;
                    textStyle(italico, negrito, fontFamily, fonteSize);
                    break;

                //Limpar
                case R.id.iditor_limpa_text: //Ativado
                    txt_nota.getText().clear();
                    break;

            }

            return false;
        }
    };

    private void iniciarNarrador() {
        if (!txt_nota.getText().toString().isEmpty())
            locutor.speak(txt_nota.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }


    public void pararNarrador() {
        if (locutor != null) {
            locutor.stop();
            locutor.shutdown();
            locutorLoader();
        }
    }

    private void locutorLoader() {
        locutor = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    locutor.setLanguage(Locale.getDefault());
                    locutor.setSpeechRate(0);
                }
            }
        });
    }

    private void textStyle(boolean italico, boolean negrito, String fonte, float tamnhoDaFonte) {
        try {

            if (negrito && italico)
                txt_nota.setTypeface(Typeface.create(fonte, Typeface.BOLD_ITALIC));
            else if (negrito)
                txt_nota.setTypeface(Typeface.create(fonte, Typeface.BOLD));
            else if (italico)
                txt_nota.setTypeface(Typeface.create(fonte, Typeface.ITALIC));
            else
                txt_nota.setTypeface(Typeface.create(fonte, Typeface.NORMAL));
        } catch (Exception x) {
            Toast.makeText(mContext, x.getMessage(), Toast.LENGTH_LONG).show();
        }

        txt_nota.setTextSize(tamnhoDaFonte);

    }

}
