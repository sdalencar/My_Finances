package com.sdainfo.myfinances.act.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sdainfo.myfinances.R;
import com.sdainfo.myfinances.act.adapter.AdapterMovimentacao;
import com.sdainfo.myfinances.act.config.ConfiguracaoFireBase;
import com.sdainfo.myfinances.act.helper.Base64Custom;
import com.sdainfo.myfinances.act.helper.Mensagens;
import com.sdainfo.myfinances.act.model.Movimentacao;
import com.sdainfo.myfinances.act.model.Usuario;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private Mensagens mensagens;

    private MaterialCalendarView materialCalendarView;
    private TextView tv_carregando, tv_saldo;
    private double despesaTotal = 0.00;
    private double receitaTotal = 0.00;
    private double resumoUser = 0.00;


    private FirebaseAuth autenticaco = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private DatabaseReference bd = ConfiguracaoFireBase.getFirebaseDatabase();
    private DatabaseReference userRef;
    private DatabaseReference movimentacaoRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacao;

    private AdapterMovimentacao adapterMovimentacao;
    private RecyclerView recyclerView;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;

    private String noMovimentacao = "movimentacao";
    private String noUsuario = "usuario";
    private String noMes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mensagens = new Mensagens(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar_principal_activity);
        toolbar.setTitle("Saldo Total");
        setSupportActionBar(toolbar);


        tv_carregando = findViewById(R.id.textView_carregando_principal);
        tv_saldo = findViewById(R.id.textView_valor_principal);
        materialCalendarView = findViewById(R.id.calendarView_principal);
        recyclerView = findViewById(R.id.recycler_view_principal);

        configurarCalendarView();
        swipe();

        //cofingurar adapter
        adapterMovimentacao = new AdapterMovimentacao(movimentacoes, this);

        //congigurar view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMovimentacao);

    }
    public void swipe() {

        ItemTouchHelper.Callback itemTouchHelper = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //movimento de arrastar e soltar qualquer direcao
                int dragFlag = ItemTouchHelper.ACTION_STATE_IDLE;

                //movimento de arrastar para os lados
                int swipreFlag = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlag, swipreFlag);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirObjeto("Atenção...", "Você quer excluir a movimentação?", viewHolder);
            }
        };
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);

    }
    private void excluirObjeto(String titulo, String msg, RecyclerView.ViewHolder viewHolder) {
        alerta("Atenção.", "Você quer excluir esse valor ", viewHolder);
    }
    private void alerta(String titulo, String msg, RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(titulo);
        builder.setTitle(msg);
        builder.setCancelable(false);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mensagens.msgCurta("Exclusão cancelada...");
                adapterMovimentacao.notifyDataSetChanged();
            }
        });
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int posicao = viewHolder.getAdapterPosition();
                movimentacao = movimentacoes.get(posicao);

                String emailUser = autenticaco.getCurrentUser().getEmail();
                String idUser = Base64Custom.codificarBase64(emailUser);
                movimentacaoRef = bd.child(noMovimentacao).child(idUser).child(noMes);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(posicao);
                atualizaSaldo();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void atualizaSaldo() {
        String emailUser = autenticaco.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = bd.child(noUsuario).child(idUser);

        if(movimentacao.getTipo().equals("r")){
            receitaTotal -= movimentacao.getValor();
            userRef.child("receitasTotal").setValue(receitaTotal);
        }

        if(movimentacao.getTipo().equals("d")){
            despesaTotal -= movimentacao.getValor();
            userRef.child("despesasTotal").setValue(despesaTotal);
        }

    }
    public void recuperarMovimentacao() {
        String emailUser = autenticaco.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        movimentacaoRef = bd.child(noMovimentacao).child(idUser).child(noMes);
        valueEventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movimentacoes.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add(movimentacao);
                }
                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void recuperarSaldo() {
        String emailUser = autenticaco.getCurrentUser().getEmail();
        String idUser = Base64Custom.codificarBase64(emailUser);
        userRef = bd.child(noUsuario).child(idUser);
        valueEventListenerUsuario = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                    despesaTotal = usuario.getDespesasTotal();
                    receitaTotal = usuario.getReceitasTotal();

                resumoUser = receitaTotal - despesaTotal;

                @SuppressLint("SimpleDateFormat")
                DateFormat decimal = new SimpleDateFormat("0.00");
                String resultaFormatado = decimal.format(resumoUser);

                tv_carregando.setText("olá, " + usuario.getNome());
                tv_saldo.setText("R$ " + resultaFormatado);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sair:
                deslogarUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogarUser() {
        if (autenticaco.getCurrentUser() != null) {
            autenticaco.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    public void menu_principal(View view) {
        if (view.getId() == R.id.fab_receita) {
            startActivity(new Intent(this, ReceitaActivity.class));
        } else if (view.getId() == R.id.fab_despesa) {
            startActivity(new Intent(this, DespesaActivity.class));
        }
    }
    private void configurarCalendarView() {
        CharSequence meses[] = {"JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO", "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};
        materialCalendarView.setTitleMonths(meses);

        CalendarDay dataAtual = materialCalendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1 ));//dá o formato % = abrir a formatação, 0 = sera incluido, 2 = qde de digitos, d = digitos
        noMes = String.valueOf(mesSelecionado + "" + dataAtual.getYear());

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1 ));
                noMes = String.valueOf(mesSelecionado + " " + date.getYear());

                movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
                recuperarMovimentacao();
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        recuperarSaldo();
        recuperarMovimentacao();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);
    }
}