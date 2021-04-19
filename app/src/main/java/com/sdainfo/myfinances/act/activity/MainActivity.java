package com.sdainfo.myfinances.act.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.sdainfo.myfinances.R;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticaco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        criarViewsInicial();
    }
    private void criarViewsInicial() {
        setButtonBackVisible(false);
        setButtonNextVisible(false);
        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slide1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slide2)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slide3)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.slide4)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .background(R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoBackward(false)
                .canGoForward(false)
                .build()
        );

    }
    public void  entrar(View view){
       switch (view.getId()){
           case R.id.button_info_cadastrar:
               startActivity(new Intent(MainActivity.this, CadastroActivity.class));
               break;
           case R.id.textView_tenho_cadastro:
               startActivity(new Intent(MainActivity.this, LoginActivity.class));
               break;
       }

    }
    private void iniciaPrincipal() {
        startActivity(new Intent(MainActivity.this, PrincipalActivity.class));
    }
    public void verificaUserLogado(){
        autenticaco = ConfiguracaoFireBase.getFirebaseAutenticacao();
        if( autenticaco.getCurrentUser() != null){
            iniciaPrincipal();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        verificaUserLogado();
    }
}