package com.apps.igordutrasanches.perfectnotes;

import android.content.Context;
import android.util.Xml;
import android.view.View;

import com.apps.igordutrasanches.perfectnotes.models.ResourceLoader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by igord on 10/04/2019.
 */

public class QRCode {

    private static ResourceLoader loader = new ResourceLoader();

    public static final String separator = "<P_N>";

    public static String criarContent(String titulo, String nota, String data, String data_m, String fonte, String fontSize, boolean bold, boolean italic){
        String book = ""; ;
        String negrito = bold == true ? "on" : "off";
        String italico = bold == true ? "on" : "off";
        try{
           book = titulo + separator + nota + separator + data + separator + data_m + separator + fonte + separator + fontSize + separator + negrito + separator + italico;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return book;
    }

    public static boolean isContents(String content){

         if(content.contains(separator))
            return true;
        else return false;
    }

    public static String getTitulo(String xml){
        String[] content = xml.split(separator);
        return content[0];
    }
    public static String getNota(String xml){
        String[] content = xml.split(separator);
        return content[1];

    }
    public static String getData(String xml){
        String[] content = xml.split(separator);
        return content[2];

    }
    public static String getDataModificacao(String xml){
        String[] content = xml.split(separator);
        return content[3];

    }
    public static String getFonte(String xml){
        String[] content = xml.split(separator);
        return content[4];

    }
    public static float getTamanhoDaFonte(String xml){
        String[] content = xml.split(separator);
        return Float.valueOf(content[5] + "f");

    }

    public static boolean getItalico(String xml){
        String[] content = xml.split(separator);
        //if(content[6].equals("on")|| content[6].equals("off"))
            return content[6] == "on" ? true : false;
        //else return false;
    }
    public static boolean getNegrito(String xml){
        String[] content = xml.split(separator);
        //if(content[7].equals("on")|| content[7].equals("off"))
        return content[7] == "on" ? true : false;
        //else return false;

    }
}
