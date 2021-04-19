package com.sdainfo.myfinances.act.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sdainfo.myfinances.R;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;
import com.sdainfo.myfinances.act.helper.Base64Custom;
import com.sdainfo.myfinances.act.helper.FormataData;
import com.sdainfo.myfinances.act.helper.Mensagens;
import com.sdainfo.myfinances.act.model.Movimentacao;
import com.sdainfo.myfinances.act.model.Usuario;

public class ReceitaActivity extends AppCompatActivity {

    private Mensagens mensagens;
    private String data, categoria, descricao, valor, noUser = "usuarios", noReceita = "receitasTotal";
    private DatabaseReference userRef;

    private TextInputEditText etData, etCategoria, etDescricao;
    private EditText etValor;
    private Movimentacao movimentacao;

    private DatabaseReference firebaseRef = ConfiguracaoFireBase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private Double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);
        inicialComponentes();
    }
    private void inicialComponentes() {
        etData = findViewById(R.id.et_receita_data);
        etCategoria = findViewById(R.id.et_receita_categoria);
        etDescricao = findViewById(R.id.et_receita_descricao);
        etValor = findViewById(R.id.et_receita_valor);

        //recupera a data atual
        etData.setText(FormataData.dataAtual());
        recuperaReceitaTotal();
    }
    public void salvarReceitas() {
        //criando objeto/instancia
        movimentacao = new Movimentacao();
        String dataEscolhida = etData.getText().toString();
        double valorRecuperado = Double.parseDouble(etValor.getText().toString());

        //dando valores aos atributos da instancia/objeto
        movimentacao.setCategoria(etCategoria.getText().toString());
        movimentacao.setData(etData.getText().toString());
        movimentacao.setDescricao(etDescricao.getText().toString());
        movimentacao.setValor(valorRecuperado);
        movimentacao.setTipo("r");

        //guardando o valor da despesa
        double receitaAtualizada = receitaTotal + valorRecuperado;
        atualizarReceita(receitaAtualizada);

        //salvando
        movimentacao.salvar(dataEscolhida);

        mensagens.msgCurta("Receita incluída");
        limpaCampos();
        finish();

    }
    public void verificaCampos(View view) {
        data = etData.getText().toString();
        categoria = etCategoria.getText().toString();
        descricao = etDescricao.getText().toString();
        valor = etValor.getText().toString();

        if (!data.isEmpty()) {
            if (!categoria.isEmpty()) {
                if (!descricao.isEmpty()) {
                    if (!valor.isEmpty()) {
                        salvarReceitas();
                    } else {
                        mensagens.msgCurta("Qual o valor");
                    }
                } else {
                    mensagens.msgCurta("Descreva a despesa");
                }
            } else {
                mensagens.msgCurta("Escolha uma categoria");
            }
        } else {
            mensagens.msgCurta("Preencha o valor");
        }
    }
    public void recuperaReceitaTotal() {
        String emailUser = autenticacao.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = firebaseRef.child(noUser).child(idUser);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitasTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void atualizarReceita(Double receitas){
        String emailUser = autenticacao.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = firebaseRef.child(noUser).child(idUser);
        userRef.child(noReceita).setValue(receitas);
    }
    private void limpaCampos() {
        etData.setText("");
        etCategoria.setText("");
        etDescricao.setText("");
        etValor.setText("");
    }
}