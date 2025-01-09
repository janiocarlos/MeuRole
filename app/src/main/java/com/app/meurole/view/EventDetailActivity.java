package com.app.meurole.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.meurole.R;
import com.app.meurole.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetailActivity extends AppCompatActivity {

    private TextView textViewNome, textViewData, textViewLocal, textViewValor;
    private Button buttonConfirmarInscricao;

    private String eventId; // Recebe o ID do evento selecionado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);

        // Inicializa as views
        textViewNome  = findViewById(R.id.textViewDetalheNome);
        textViewData  = findViewById(R.id.textViewDetalheData);
        textViewLocal = findViewById(R.id.textViewDetalheLocal);
        textViewValor = findViewById(R.id.textViewDetalheValor);

        buttonConfirmarInscricao = findViewById(R.id.buttonConfirmarInscricao);

        // Recupera o EVENT_ID da Intent
        eventId = getIntent().getStringExtra("EVENT_ID");

        if (eventId != null) {
            carregarDetalhesEvento(eventId);
        } else {
            Toast.makeText(this, "Erro ao obter o ID do evento", Toast.LENGTH_SHORT).show();
        }

        // Exemplo de clique do botão
        buttonConfirmarInscricao.setOnClickListener(v -> {
            // Aqui você coloca a lógica para verificar se o usuário está logado.
            // Se estiver logado, efetua a inscrição, caso contrário, redireciona para login.
            // Por enquanto, só um Toast de exemplo:
            Toast.makeText(this, "Inscrição confirmada para o evento!", Toast.LENGTH_SHORT).show();

            // Você pode, por exemplo, salvar no Realtime Database a inscrição do usuário
            // e então navegar para outra tela se quiser.
        });
    }
    private void carregarDetalhesEvento(String eventId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("eventos").child(eventId);

        eventRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);

                if (event != null) {
                    textViewNome.setText(event.getNome());
                    textViewData.setText(event.getData());
                    textViewLocal.setText(event.getLocal());

                    // Convertendo double para String
                    String valorText = String.valueOf(event.getValorInscricao());
                    textViewValor.setText("R$ " + valorText);
                } else {
                    Toast.makeText(EventDetailActivity.this,
                            "Não foi possível encontrar o evento",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventDetailActivity.this,
                        "Falha ao carregar detalhes do evento",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}