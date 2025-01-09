package com.app.meurole.view;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

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
            // Carrega detalhes do evento (exemplo)
            carregarDetalhesEvento(eventId);
        } else {
            Toast.makeText(this, "Erro ao obter o ID do evento", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Exemplo de clique do botão
        // Clique em "Confirmar Inscrição"
        buttonConfirmarInscricao.setOnClickListener(v -> {
            // Verificar se o usuário está logado
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                // Não está logado. Redirecione para tela de UserLoginActivity ou peça para logar.
                Toast.makeText(EventDetailActivity.this,
                        "É necessário estar logado para se inscrever.",
                        Toast.LENGTH_LONG).show();

                // Redirecionando para a tela de login
                Intent intent = new Intent(EventDetailActivity.this, UserLoginActivity.class);
                startActivity(intent);
                finish();

            } else {
                // Se está logado, recupera userId (UID)

                String userId = currentUser.getUid();
                confirmarInscricao(userId, eventId);
            }
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

    private void confirmarInscricao(String userId, String eventId) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Montamos um Map com os caminhos e valores
        Map<String, Object> updates = new HashMap<>();
        updates.put("inscricoes/" + eventId + "/" + userId, true);
        updates.put("usuarios/" + userId + "/eventosInscritos/" + eventId, true);

        // Faz o update em todos esses caminhos de uma vez
        rootRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this,
                        "Inscrição realizada com sucesso!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Falha ao realizar inscrição no evento.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}