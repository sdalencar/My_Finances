package com.sdainfo.myfinances.act.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;
import com.sdainfo.myfinances.act.helper.Base64Custom;
import com.sdainfo.myfinances.act.helper.FormataData;

public class  Movimentacao {
    private String key;
    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private Double valor;

    public Movimentacao() {
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void salvar(String  dataEscolhida) {
        String dataDespesa = FormataData.dataEscolhida(dataEscolhida);

        FirebaseAuth userAutenticado = ConfiguracaoFireBase.getFirebaseAutenticacao();

        String idUser = Base64Custom.codificarBase64(userAutenticado.getCurrentUser().getEmail());

        DatabaseReference databaseReference = ConfiguracaoFireBase.getFirebaseDatabase();

        databaseReference.child("movimentacao")
                .child(idUser)
                .child(dataDespesa)
                .push()
                .setValue(this);
    }

}
