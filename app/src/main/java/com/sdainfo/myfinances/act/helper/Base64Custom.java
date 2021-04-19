package com.sdainfo.myfinances.act.helper;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String codificar){

        return Base64.encodeToString(codificar.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    public static String decodificarBase64(String decodificar){

        return new String(Base64.decode(decodificar, Base64.DEFAULT));
    }

}
