package com.sdainfo.myfinances.act.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.sdainfo.myfinances.R;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;
import com.sdainfo.myfinances.act.model.Usuario;
import com.sdainfo.myfinances.act.helper.Mensagens;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_senha;
    private Button bt_logar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;
    private Mensagens mensagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        iniciaComponentes();

        mensagens = new Mensagens(getApplicationContext());

        bt_logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String semail, ssenha;
                semail = et_email.getText().toString();
                ssenha = et_senha.getText().toString();
                if (!semail.isEmpty()) {
                    if (!ssenha.isEmpty()) {

                        usuario = new Usuario();
                        usuario.setEmail(semail);
                        usuario.setSenha(ssenha);

                        logarUser();
                    } else {
                        mensagens.msgCurta("Preencha a senha");
                    }
                } else {
                    mensagens.msgCurta("Preecha o e-mail");
                }
            }
        });
    }
    private void iniciaComponentes() {
        et_email = findViewById(R.id.editText_email_logar);
        et_senha = findViewById(R.id.editText_passord_logar);
        bt_logar = findViewById(R.id.button_logar);
    }
    private void logarUser() {
        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
             if(task.isSuccessful()){
                iniciaPrincipal();
             }else {
                 String msgExcecao = "";
                 try {
                     throw task.getException();
                 }catch (FirebaseAuthInvalidCredentialsException e) {
                     msgExcecao = "Dados inválidos.";
                 } catch ( FirebaseAuthInvalidUserException e) {
                     msgExcecao = "Usuário não cadastrado.";
                 } catch (Exception e) {
                     msgExcecao = "Erro ao cadastrar usuário : " + e.getMessage();
                     e.printStackTrace();
                 }
                 mensagens.msgCurta(msgExcecao);
             }
        });
    }
    private void iniciaPrincipal() {
        limparCampos();
        startActivity(new Intent(LoginActivity.this, PrincipalActivity.class));
        finish();
    }
    public void limparCampos() {
        et_senha.setText("");
        et_email.setText("");
    }
}