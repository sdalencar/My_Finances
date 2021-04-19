package com.sdainfo.myfinances.act.helper;

import java.text.SimpleDateFormat;

public class  FormataData {

    public static String dataAtual() {
        long dataSystema = System.currentTimeMillis();
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dataFormatada = formatoData.format(dataSystema);
        return dataFormatada;
    }

    public static String dataEscolhida(String data) {
       String[] retornoData = data.split("/");

       String dia = retornoData[0];
       String mes = retornoData[1];
       String ano = retornoData[2];

       return  mes + ano;
    }


}
