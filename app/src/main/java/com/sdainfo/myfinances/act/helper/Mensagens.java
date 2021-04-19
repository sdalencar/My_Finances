package com.sdainfo.myfinances.act.helper;

import android.content.Context;
import android.widget.Toast;

public class Mensagens {

    private Context ctx;
    public Mensagens(Context ctx) {
        this.ctx = ctx;
    }

    public void msgCurta(String texto){
        Toast.makeText(ctx, texto, Toast.LENGTH_SHORT).show();
    }

    public void msgLonga(String texto){
        Toast.makeText(ctx, texto, Toast.LENGTH_SHORT).show();
    }

}
