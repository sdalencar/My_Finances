package com.sdainfo.myfinances.act.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.sdainfo.myfinances.R;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;
import com.sdainfo.myfinances.act.helper.Base64Custom;
import com.sdainfo.myfinances.act.helper.Mensagens;
import com.sdainfo.myfinances.act.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText et_nome, et_senha, et_email;
    private Button bt_salvar;
    private Mensagens mensagens;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        iniciarComponentes();

        getSupportActionBar().setTitle("Cadastro");
        mensagens = new Mensagens(getApplicationContext());

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String snome, ssenha, semail;
                snome = et_nome.getText().toString();
                semail = et_email.getText().toString();
                ssenha = et_senha.getText().toString();

                if (!snome.isEmpty()) {
                    if (!semail.isEmpty()) {
                        if (!ssenha.isEmpty()) {

                            usuario = new Usuario();
                            usuario.setNome(snome);
                            usuario.setEmail(semail);
                            usuario.setSenha(ssenha);
                            cadastrarUser();

                        } else {
                            mensagens.msgLonga("Preencha a senha.");
                        }

                    } else {
                        mensagens.msgLonga("Preencha o e-mail.");
                    }

                } else {
                    mensagens.msgLonga("Preencha o nome.");
                }

            }
        });

    }
    private void iniciarComponentes() {
        et_email = findViewById(R.id.editText_email_cadastrar);
        et_nome = findViewById(R.id.editText_nome_cadastro);
        et_senha = findViewById(R.id.editText_passord_cadastrar);
        bt_salvar = findViewById(R.id.button_logar);
    }
    private void cadastrarUser() {
        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String idUser = Base64Custom.codificarBase64(usuario.getEmail());
                usuario.setIdUser(idUser);
                usuario.salvar();
                limpaCampos();
                finish();
            } else {
                String msgExcecao = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    msgExcecao = "Digite uma senha mais forte.";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    msgExcecao = "Digite um e-mail válido.";
                } catch (FirebaseAuthUserCollisionException e) {
                    msgExcecao = "E-mail já cadastrado.";
                } catch (Exception e) {
                    msgExcecao = "Erro ao cadastrar usuário : " + e.getMessage();
                    e.printStackTrace();
                }
                mensagens.msgCurta(msgExcecao);
            }
        });
    }

    private void limpaCampos() {
        et_senha.setText("");
        et_email.setText("");
        et_nome.setText("");
    }


}