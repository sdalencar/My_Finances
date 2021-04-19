package com.sdainfo.myfinances.act.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;

public class Usuario {
    private String idUser;
    private String nome;
    private String email;
    private String senha;
    private Double despesasTotal = 0.00;
    private Double receitasTotal = 0.00;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public Usuario() {
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Double getDespesasTotal() {
        return despesasTotal;
    }

    public void setDespesasTotal(Double despesasTotal) {
        this.despesasTotal = despesasTotal;
    }

    public Double getReceitasTotal() {
        return receitasTotal;
    }

    public void setReceitasTotal(Double receitasTotal) {
        this.receitasTotal = receitasTotal;
    }

    public void salvar() {
        firebaseAuth = ConfiguracaoFireBase.getFirebaseAutenticacao();
        databaseReference = ConfiguracaoFireBase.getFirebaseDatabase();
        databaseReference.child("usuarios").child(this.idUser).setValue(this);
    }

}
